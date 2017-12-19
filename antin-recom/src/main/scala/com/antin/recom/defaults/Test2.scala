package com.antin.recom.defaults

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

import scala.collection.mutable.ListBuffer

/**
  * Created by Administrator on 2017/9/11.
  */
object Test2 {
  def main(args: Array[String]) {
    //System.setProperty("HADOOP_USER_NAME", "jcj")

    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val spark = SparkSession
      .builder()
      .appName("Test2")
      //.master("local[*]")
      .master("yarn")
      .getOrCreate()
    import spark.sql
    val analyzerDF = spark.read.json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_weather")
    analyzerDF.createOrReplaceTempView("hadoop_reservation_weather")

    val resultDF = spark.read.json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_result")
    val resultRow = resultDF.rdd.map(row => {

      val lines = row.mkString.split(",")
      Row(lines(0), lines(1), lines(2), lines(3).toDouble.toInt.toString, lines(4).toDouble.toInt.toString, lines(5))
    })
    val wSchemaString = "time_range temperature pressure humidity wind visibility"

    // Generate the schema based on the string of schema
    val wfields = wSchemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val wschema = StructType(wfields)

    val weatherDF = spark.createDataFrame(resultRow, wschema)

    weatherDF.createOrReplaceTempView("hadoop_result")


    val resultR = sql("select * from hadoop_result")
      //.show(100,false)
      .rdd.map(m => {
      m.toString
    }) collect()

    val list = new ListBuffer[String]

    resultR.foreach(x => {
      sql("select counts,org_id,dept_code,dept_name,doctor_code,doctor_name,time_range,temperature,pressure,humidity,wind,visibility from hadoop_reservation_weather").filter(r => {
        x == "[" + r(6).toString.split(" ")(1).split(":")(0) + "," + r(7).toString + "," + r(8).toString + "," + r(9).toString + "," + r(10).toString + "," + r(11).toString + "]"
      }).rdd.foreach(r => {
        list += r.mkString
      })

    })

    spark.sparkContext.parallelize(list).saveAsTextFile("hdfs://hadoop-cluster/user/jcj/rec/hadoop_result_value")

  }

}
