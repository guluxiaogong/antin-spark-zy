package com.antin.extend.input

import java.io.IOException

import com.antin.extend.util.CollectionConverters
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Scan, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableInputFormatBase
import org.apache.hadoop.hbase.mapreduce.{TableMapReduceUtil, TableInputFormat}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.slf4j.{LoggerFactory, Logger}

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.{universe => ru}

object HbaseResult {
  type Value = Array[Byte]
  type Cell = Map[Long, Value]
  type Family = Map[String, Value]
  type Row = Map[String, Family]
}

abstract class InputResult extends Serializable

class HbaseResult(result: Result, val familyName: Option[String] = None, val isOnlyLastVersion: Boolean = false) extends InputResult {

  import HbaseResult._

  val row = processResult(result)

  def this(result: Result, familyName: String) = this(result, Some(familyName))

  def this(result: Result, familyName: String, isOnlyLastVersion: Boolean) = this(result, Some(familyName), isOnlyLastVersion)

  def apply(f: String): Family = row(f)

  def get(f: String): Option[Family] = row.get(f)

  def getFields(fields: Seq[Field]): Seq[Tuple2[String, Map[Long, Any]]] = ???

  def getLastVersionCell(family: String, column: String): Value = getCellLastVersion(row(family)(column).asInstanceOf[Map[Long, Value]])

  private def getCellLastVersion(cell: Map[Long, Value]): Value =
    cell.toSeq.sortBy {
      _._1
    }.last._2

  private def processResult(result: Result): Row = {
    CollectionConverters.recursivelyToScala[Value](result.getMap).map { case (fname, f) =>
      (
        Bytes.toString(fname),
        f.asInstanceOf[Map[Value, Any]].map { case (cname, c) =>
          (Bytes.toString(cname),
            isOnlyLastVersion match {
              case false => c
              case true => getCellLastVersion(c.asInstanceOf[Map[Long, Value]])
            }
            )
        }
        )
    }.asInstanceOf[Row].
      filterKeys { k => familyName.getOrElse(k) == k }.map(identity)
  }
}

class HbaseInput(val config: HbaseInputConfig, val ss: SparkSession) extends Input with Serializable {

  private val log: Logger = LoggerFactory.getLogger(classOf[HbaseInput])

  val fields = config.fields.map(Field.load(_).asInstanceOf[HbaseField])
  fields.foreach(_.setDefaultFamily(config.family))

  val yields = fields.flatMap(_.yields)

  val columns = config.fields.map(x => x.get("column"))

  override def dataframe(filter: ((String, HbaseResult)) => Boolean): DataFrame = dataframe(null, filter: ((String, HbaseResult)) => Boolean)

  override def dataframe(scan: Scan, filter: ((String, HbaseResult)) => Boolean): DataFrame = {
    import org.apache.spark.sql.Row
    val rdd = rows(scan, filter).map { case (key, row) => Row.fromSeq(key +: row) }
    //转成DataFrame
    val structType = yieldsToStruct(Yield("_key", "string") +: yields)
    ss.createDataFrame(rdd, structType)
  }

  override def dataframe: DataFrame = dataframe(_ => true)

  import ss.implicits._

  override def dataset[T <: Product : ClassTag](filter: ((String, HbaseResult)) => Boolean)(implicit tag: TypeTag[T]): Dataset[T] = dataset[T](null, filter: ((String, HbaseResult)) => Boolean)

  override def dataset[T <: Product : ClassTag](scan: Scan, filter: ((String, HbaseResult)) => Boolean)(implicit tag: TypeTag[T]): Dataset[T] = {
    val columns = "_key" +: yields.map(_.name)
    val rdd = rows(scan, filter).map { case (key, row) => newInstance[T](tag, Map(columns.zip(key +: row): _*)) }
    ss.createDataset[T](rdd)
  }

  override def dataset[T <: Product : ClassTag](implicit tag: TypeTag[T]): Dataset[T] = dataset[T]((_: (String, HbaseResult)) => true)

  private def newInstance[T](tag: TypeTag[_], kv: Map[String, Any]): T = {
    val constructor = tag.tpe.decl(ru.termNames.CONSTRUCTOR).asMethod
    val params = constructor.paramLists.head.map { param => kv(param.name.decodedName.toString) }
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val klass = tag.tpe.typeSymbol.asClass
    val cm = m.reflectClass(klass)
    val ctorm = cm.reflectConstructor(constructor)
    ctorm(params: _*).asInstanceOf[T]
  }

  private def rows(scan: Scan, filter: ((String, HbaseResult)) => Boolean) = loadData(scan).filter(filter).map[(String, Seq[Any])] { case (key, row) => (key, fields.flatMap(_ (row))) }


  private def loadData(scan: Scan): RDD[(String, HbaseResult)] = {
    //链接hbase
    val hbaseConf = HBaseConfiguration.create()
    config.hbaseConfig.get("file").foreach(f => hbaseConf.addResource(new Path(f.asInstanceOf[String])))
    config.hbaseConfig.get("properties").
      map(_.asInstanceOf[Map[String, String]]).
      foreach {
        _.foreach { case (k, v) => hbaseConf.set(k, v) }
      }
    if (scan != null) {
      config.family match {
        case Some(f) => {
          columns.foreach { x => {
            x match {
              case Some(v) => scan.addColumn(Bytes.toBytes(f), Bytes.toBytes(v.toString))
              case None => log.info(s"columns has None of family $f ")
            }
          }
          }
          scan.addFamily(Bytes.toBytes(f))
        }
        case None => log.error(s"can't find family config")
      }
      val proto: ClientProtos.Scan = ProtobufUtil.toScan(scan)
      hbaseConf.set(TableInputFormat.SCAN, Base64.encodeBytes(proto.toByteArray))
    }

    val sc = ss.sparkContext
    sc.newAPIHadoopRDD(hbaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]).
      map { case (rowkey, result) =>
        (
          Bytes.toString(rowkey.get),
          new HbaseResult(result, isOnlyLastVersion = true) //封装返回结果信息
          )
      }
  }

  private def yieldsToStruct(yields: Seq[Yield]): StructType = {
    def typeMap(typ: String): DataType = typ match {
      case "byte" => BinaryType
      case "int" => IntegerType
      case "long" => LongType
      case "float" => FloatType
      case "double" => DoubleType
      case "string" => StringType
    }

    StructType(yields.map { y =>
      StructField(y.name, y.collection match {
        case None => typeMap(y.typ)
        case Some("array") => ArrayType(typeMap(y.typ), true)
        case Some(other) => throw new UnsupportedOperationException(s"Unknow collection: $other")
      }
      )
    })
  }
}
