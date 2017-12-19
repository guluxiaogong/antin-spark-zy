package com.antin.recom.defaults

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017/9/7.
  * 加载预约记录
  */
//class ReadFromOracle

object ReadFromOracle {

  // private var path: String = classOf[ReadFromOracle].getClassLoader.getResource(".").toString.substring(6)

  def main(args: Array[String]) {

    //System.setProperty("HADOOP_USER_NAME", "jcj")

    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val spark = SparkSession
      .builder()
      .appName("ReadFromOracle")
      //.master("local[*]")
      .master("yarn")
      .getOrCreate()
    val jdbcDF = spark.read
      .format("jdbc")
      .option("url", "jdbc:oracle:thin:@192.168.2.146:1521:orcl")
      .option("driver", "oracle.jdbc.driver.OracleDriver")
      .option("dbtable", "urp_reservation_history")
      .option("user", "urp")
      .option("password", "urp")
      .load()

    jdbcDF.select("*").createTempView("urp_reservation_history")
    //import spark.implicits._

    val rowDF = spark.sql("select * from urp_reservation_history")
    rowDF.rdd.map(row => row.mkString("\t"))
      .saveAsTextFile("hdfs://hadoop-cluster/user/jcj/rec/urp_reservation_history.text")
    //.foreach(println)
  }

}
//spark-submit --class com.hadoop.recom.defaults.ReadFromOracle --master yarn --deploy-mode client --driver-memory 2g --executor-memory 2g  --executor-cores 2 /home/jcj/runJar/hadoop-recom-1.0-SNAPSHOT.jar
