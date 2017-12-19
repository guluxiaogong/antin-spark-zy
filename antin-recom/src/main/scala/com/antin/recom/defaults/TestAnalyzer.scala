package com.antin.recom.defaults

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017/9/11.
  */
object TestAnalyzer {
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

    //countsReservation(spark)
    //reservationJoinWeather(spark)
    analyzer(spark)

    //convertTimeRange(spark)


  }

  def analyzer(spark: SparkSession): Unit = {
    import spark.sql
    val analyzerDF = spark.read.json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_weather")
    analyzerDF.createOrReplaceTempView("hadoop_reservation_weather")

    //    sql("select * from hadoop_reservation_weather")
    //      // .write.mode("overwrite").json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_weather")
    //      .show(false)

    sql("select counts,org_id,dept_code,doctor_code,time_range,temperature,pressure,humidity,wind,visibility from hadoop_reservation_weather").show(false)
  }

  def reservationJoinWeather(spark: SparkSession): Unit = {
    import spark.sql
    val weatherDF = spark.read.json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_weather.json")
    val reservationCountsDF = spark.read.json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_counts")
    weatherDF.createOrReplaceTempView("hadoop_weather")
    reservationCountsDF.createOrReplaceTempView("hadoop_reservation_counts")

    sql("select t1.*,t2.temperature,t2.pressure,t2.humidity,t2.wind,t2.visibility from hadoop_reservation_counts t1 inner join hadoop_weather t2 on t1.time_range=t2.time_range")
      .write.mode("overwrite").json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_weather")
    // .show(false)
  }


  def countsReservation(spark: SparkSession): Unit = {
    val reservationDF = spark.read.json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation.json")

    import spark.sql
    reservationDF.createOrReplaceTempView("hadoop_reservation")

    //sql("select * from hadoop_reservation").show(false)
    //sql("select count(*) as counts from hadoop_reservation").show(false)
    sql("select org_id,dept_code,dept_name,doctor_name,doctor_code,time_range,count(*) counts from hadoop_reservation group by org_id,dept_code,dept_name,doctor_name,doctor_code,time_range")
      .write.mode("overwrite").json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_counts")
    //.show(false)
  }

  def convertTimeRange(spark: SparkSession): Unit = {
    import spark.sql
    val analyzerDF = spark.read.json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_weather")
    analyzerDF.createOrReplaceTempView("hadoop_reservation_weather")

    sql("select counts,org_id,dept_code,doctor_code,time_range,temperature,pressure,humidity,wind,visibility from hadoop_reservation_weather").
      rdd.map(m => {
      val timeDouble = m(4).toString.split(" ")(1).split(":")(0).toDouble
      (m(0).toString.toDouble, m(1).toString.toDouble, m(2).toString.hashCode.toDouble, m(3).toString.hashCode.toDouble, timeDouble, m(5), m(6), m(7).toString.toDouble, m(8).toString.toDouble, m(9))
    }).saveAsTextFile("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_weather_double")
    // .write.mode("overwrite").json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_weather")
    //.show(false)
  }

}
