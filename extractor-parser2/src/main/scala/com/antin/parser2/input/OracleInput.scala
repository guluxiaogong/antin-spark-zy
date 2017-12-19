package com.antin.parser2.input

import java.sql.{DriverManager, ResultSet}

import com.antin.parser2.util.StringUtil
import oracle.sql.BLOB
import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.sql._
import org.apache.spark.sql.types._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

/**
  * Created by jichangjin on 2017/9/14.
  */
class OracleInput(val config: OracleInputConfig, val ss: SparkSession) extends Input with Serializable {

  def dataset[T <: Product : ClassTag](implicit tag: TypeTag[T]): Dataset[T] = {
    //TODO
    null
  }

  //  def dataset[T <: Product : ClassTag](implicit tag: TypeTag[T]): RDD[mutable.HashMap[String,Any]] = {
  //    //TODO
  //    val jdbcRDD = loadData
  //    val maps=jdbcRDD.map(x => {
  //      x._3
  //    })
  //    maps
  //  }
  def dataframe: DataFrame = {
    val jdbcRDD = loadData
    //    if (!jdbcRDD.isEmpty())
    //      return null
    val labels = jdbcRDD.first._1
    val result = jdbcRDD.map(_._3).map(attributes => {
      Row.fromSeq(labels.map(attributes(_)).map(value => if (value == null) "" else value.toString))
    })
    //转成Dataset
    val schema = buildSchema(labels.zip(jdbcRDD.first._2))
    ss.createDataFrame(result, schema)
  }

  //连接数据库RDD[mutable.HashMap[String, Any]]
  private def loadData: RDD[(ListBuffer[String], ListBuffer[String], mutable.HashMap[String, Any])] = {
    val dbConfig = config.oracleConfig

    val connection = () => {
      Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
      DriverManager.getConnection(dbConfig("url").toString, dbConfig("username").toString, dbConfig("password").toString)
    }

    val jdbcRDD = new JdbcRDD(
      ss.sparkContext,
      connection,
      dbConfig("query").toString,
      dbConfig("lowerBound").toString.toInt, dbConfig("upperBound").toString.toInt, dbConfig("numPartitions").toString.toInt,
      r => queryAsJson(r)
    )
    jdbcRDD
  }

  private def queryAsJson(rs: ResultSet): (ListBuffer[String], ListBuffer[String], mutable.HashMap[String, Any]) = {
    // import scala.collection.mutable.ListBuffer
    // val array = new ArrayBuffer[mutable.HashMap[String, Any]]
    val labels = new ListBuffer[String]
    val types = new ListBuffer[String]
    val column = rs.getMetaData.getColumnCount
    val map = new mutable.HashMap[String, Any]
    for (i <- 1 to column) {
      val resultSetMetaData = rs.getMetaData
      val label = resultSetMetaData.getColumnLabel(i)
      val columnNme = StringUtil.camelCaseName(label.toLowerCase)
      labels += columnNme
      val typeName = resultSetMetaData.getColumnTypeName(i)
      types += typeName
      typeName match {
        case "CLOB" => map.put(columnNme, rs.getString(i))
        case "BLOB" => {

          val blob = rs.getBlob(i).asInstanceOf[BLOB]
          if (blob == null) {
            map.put(columnNme, null)
          } else {
            // 得到数据库的输出流
            val stream = blob.getBinaryStream
            val content = StringUtil.readFromStream(stream)
            map.put(columnNme, content)
          }

        }
        case _ => map.put(columnNme, rs.getObject(i))
      }
    }
    (labels, types, map)
  }

  private def buildSchema(yields: Seq[(String, String)]): StructType = {
    def typeMap(typ: String): DataType = typ.toLowerCase match {
      case "byte" => BinaryType
      case "int" => IntegerType
      case "long" => LongType
      case "float" => FloatType
      case "double" => DoubleType
      case "string" => StringType
      case "varchar2" => DataTypes.StringType
      case "number" => DataTypes.LongType
      case "date" => DataTypes.DateType
    }

    StructType(yields.map(y => {
      // StructField(y._1, typeMap(y._2), nullable = true)//TODO 字段类型
      StructField(y._1, DataTypes.StringType, nullable = true)
    }))

  }
}

