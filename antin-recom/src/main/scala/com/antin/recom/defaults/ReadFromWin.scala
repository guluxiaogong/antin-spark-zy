package com.antin.recom.defaults

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017/9/8.
  * 加载天气数据
  */
object ReadFromWin {
  def main(args: Array[String]) {

    System.setProperty("HADOOP_USER_NAME", "jcj")

    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val spark = SparkSession
      .builder()
      .appName("ReadFromOracle")
      .master("local[*]")
      //.master("yarn")
      .getOrCreate()

    val csvPath = "file:///D:\\ZHS\\data\\weather\\59134.01.01.2017.08.09.2017.1.0.0.cn.utf8.00000000.csv"
    val csvDF = spark.read.csv(csvPath)
    val resultRdd = csvDF.rdd.map(row => {
      row.mkString("\t").split(";")
    }).filter(_.size == 30).map(x => x.mkString("\t"))

    resultRdd.saveAsTextFile("hdfs://hadoop-cluster/user/jcj/rec/weather.text")

    //println(resultRdd.collect().size)

    //csvDF.createTempView("hadoop_weather")
    //spark.sql("select * from hadoop_weather").show(false)

    //    val sqlContext = spark.sqlContext
    //    val df = sqlContext.load("com.databricks.spark.csv", Map("path" -> csvPath, "quote" -> "#", "header" -> "false"))
    //    df.select("*").show(false)
    //.save("newcars.csv", "com.databricks.spark.csv")
  }


}
