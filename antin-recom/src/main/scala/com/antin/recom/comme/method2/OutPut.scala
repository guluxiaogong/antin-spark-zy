package com.antin.recom.comme.method2

import java.util.Properties

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created by Administrator on 2017/8/15.
  */
object OutPut {

  def ToOracle2(spark: SparkSession, result: RDD[String]): Unit = {
    val schema = StructType(
      List(
        StructField("ORG_ID", StringType, true),
        StructField("DEPT_CODE", StringType, true),
        StructField("DOCTOR_CODE", StringType, true),
        StructField("CODES", StringType, true)
      )
    )
    val rowRDD = result.map(_.split(" ")).filter(x => x.length > 1).filter(x=>x(0).split("-").length==3).map(p => {
      val ids = p(0).split("-")
      Row(ids(0).trim, ids(1).trim, ids(2).trim, p(1).trim)
    })
    val personDataFrame = spark.sqlContext.createDataFrame(rowRDD, schema)
    val prop = new Properties()
    prop.put("user", "urp")
    prop.put("password", "urp")
    personDataFrame.write.mode("append").jdbc("jdbc:oracle:thin:@192.168.2.146:1521:orcl", "rec_pre_doctor_doctors", prop)
  }

  def ToOracle(spark: SparkSession, result: RDD[String]): Unit = {
    val schema = StructType(
      List(
        StructField("ID", StringType, true),
        StructField("CODES", StringType, true)
      )
    )
    val rowRDD = result.map(_.split(" ")).filter(x => x.length > 1).map(p => {
      //      var value = p(1)
      //      if (value == null)
      //        value = ""
      //      Row(p(0).trim, value.trim)
      Row(p(0).trim, p(1).trim)
    })
    val personDataFrame = spark.sqlContext.createDataFrame(rowRDD, schema)
    val prop = new Properties()
    prop.put("user", "urp")
    prop.put("password", "urp")
    personDataFrame.write.mode("append").jdbc("jdbc:oracle:thin:@192.168.2.146:1521:orcl", "rec_pre_doctor_doctorsss", prop)
  }

  def ToFile(): Unit = {

  }

}
