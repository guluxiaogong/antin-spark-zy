package com.antin.parser2.input

import com.antin.parser2.source.HBaseResult
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.{universe => ru}

/**
  * Created by win7 on 2017/12/3.
  */
class HBaseParser(val ss: SparkSession, val config: HBaseInputConfig, val hBaseResult: HBaseResult) extends Serializable {
  val fields = config.fields.map(Field.load(_).asInstanceOf[HbaseField])
  //fields.foreach(_.setDefaultFamily(config.family))
  val yields = fields.flatMap(_.yields)
//  override def dataframe(filter: ((String, HBaseResult)) => Boolean): DataFrame = {
//    import org.apache.spark.sql.Row
//    val rdd = rows(filter).map { case (key, row) => Row.fromSeq(key +: row) }
//    //转成DataFrame
//    val structType = yieldsToStruct(Yield("_key", "string") +: yields)
//    ss.createDataFrame(rdd, structType)
//  }
  private def newInstance[T](tag: TypeTag[_], kv: Map[String, Any]): T = {
    val constructor = tag.tpe.decl(ru.termNames.CONSTRUCTOR).asMethod
    val params = constructor.paramLists.head.map { param => kv(param.name.decodedName.toString) }
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val klass = tag.tpe.typeSymbol.asClass
    val cm = m.reflectClass(klass)
    val ctorm = cm.reflectConstructor(constructor)
    ctorm(params: _*).asInstanceOf[T]
  }

  private def rows(filter: ((String, HBaseResult)) => Boolean) = null//loadData.filter(filter).map[(String, Seq[Any])] { case (key, row) => (key, fields.flatMap(_ (row))) }

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
