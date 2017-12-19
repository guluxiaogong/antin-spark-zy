package com.antin.parser.input

import com.antin.parser.util.CollectionConverters
import org.apache.hadoop.hbase.client.Result
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.reflect.runtime.{universe => ru}
import org.apache.hadoop.hbase.util.Bytes

object HbaseResult {
  type Value = Array[Byte]
  type Cell = Map[Long, Value]
  type Family = Map[String, Value]
  type Row = Map[String, Family]
}

abstract class InputResult extends Serializable

class HbaseResult(val rowKey: String, result: Result, val familyName: Option[String] = None, val isOnlyLastVersion: Boolean = false) extends InputResult {

  import HbaseResult._

  val row = processResult(result)

  def this(rowKey: String, result: Result, familyName: String) = this(rowKey, result, Some(familyName))

  def this(rowKey: String, result: Result, familyName: String, isOnlyLastVersion: Boolean) = this(rowKey, result, Some(familyName), isOnlyLastVersion)

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

class HbaseInput(val config: HbaseInputConfig, val ss: SparkSession, val inputResult: RDD[InputResult]) extends Input with Serializable {
  //
  val hbaseResult = inputResult.map(x => x.asInstanceOf[HbaseResult]).map(x => (x.rowKey, x))

  val fields = config.fields.map(Field.load(_).asInstanceOf[HbaseField])
  fields.foreach(_.setDefaultFamily(config.family))

  val yields = fields.flatMap(_.yields)

  val filterFields = (kv: (String, HbaseResult)) => {
    val family = config.family.getOrElse[String]("")
    val hasFieldsContains = config.include.map(f => {
      val column = f.getOrElse("code", "").asInstanceOf[String]
      val hasColumn = kv._2(family).get(column) match {
        case Some(v) => {
          //列值0130,0231
          f.getOrElse("value", "").asInstanceOf[String].split(",").map(_.trim).contains(Bytes.toString(v))
        }
        case None => false
      }
      hasColumn
    })
    //hasFieldsContains.foldLeft(true)((a, b) => a && b)
    hasFieldsContains.forall(p => p)
  }

  override def dataframe(filter: ((String, HbaseResult)) => Boolean): (String, DataFrame) = {
    import org.apache.spark.sql.Row
    val rdd = rows(filter).map { case (key, row) => {
      Row.fromSeq(key +: row)
    }
    }
    //转成DataFrame
    val structType = yieldsToStruct(Yield("_key", "string") +: yields)
    (config.output, ss.createDataFrame(rdd, structType))
  }

  override def dataframe: (String, DataFrame) = dataframe(_ => true)

  override def dataframe(filter: ((String, HbaseResult)) => Boolean, isExpansion: Boolean): (String, DataFrame) = {
    isExpansion match {
      case true => dataframes(filter, isExpansion): (String, DataFrame)
      case false => dataframe(filter): (String, DataFrame)
    }
  }

  private def dataframes(filter: ((String, HbaseResult)) => Boolean, isExpansion: Boolean): (String, DataFrame) = {
    import org.apache.spark.sql.Row

    val rdd = rows(filter).map { case (key, row) => {
      val indexs = row.filter(x => x.isInstanceOf[Seq[Any]]).map(x => x.asInstanceOf[Seq[Any]].indices)
      val range = if (indexs.isEmpty) Seq(0) else indexs.head
      range.map(index => {
        val r = row.map(x => {
          x match {
            case i: Seq[Any] => i(index)
            case _ => x
          }
        })
        Row.fromSeq((key + "_" + index) +: r)
      })
    }
    }.flatMap(f => f)

    //转成DataFrame
    val structType = yieldsToStruct(Yield("_key", "string") +: yields, isExpansion)
    (config.output, ss.createDataFrame(rdd, structType))
  }

  private def rows(filter: ((String, HbaseResult)) => Boolean) = hbaseResult.filter(filterFields).filter(filter).map[(String, Seq[Any])] {
    case (key, row) => {
      (key, fields.flatMap(_ (row)))
    }
  }


  private def yieldsToStruct(yields: Seq[Yield], isExpansion: Boolean = false): StructType = {
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
        case Some("array") => if (isExpansion) typeMap(y.typ) else ArrayType(typeMap(y.typ), true)
        case Some(other) => throw new UnsupportedOperationException(s"Unknow collection: $other")
      }
      )
    })
  }
}
