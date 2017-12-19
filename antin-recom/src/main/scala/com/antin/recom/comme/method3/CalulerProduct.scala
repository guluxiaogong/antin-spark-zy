package com.antin.recom.comme.method3

import java.util.Properties

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{DataTypes, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

import scala.collection.mutable.ListBuffer

/**
  * Created by Administrator on 2017/9/5.
  */
object CalulerProduct {
  private[comme] var path: String = classOf[LoadFromOracle].getClassLoader.getResource(".").toString.substring(6)

  def main(args: Array[String]) {
    //    val conf = new SparkConf()
    //      .setMaster("local[*]")
    //      .setAppName("Caluler")
    //    val sc = new SparkContext(conf)

    val spark = SparkSession
      .builder
      .appName("CalulerProduct")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext

    //val df = sc.textFile("file:///" + path + "/dept3/*/part-00000-ad7e22e5-6844-4da9-83a8-d949ac3ab5f5.txt")
    val df = sc.textFile("file:///" + path + "/dept3/*/part-*")

    val resultRdd = df.mapPartitions(iterator => {
      val res = iterator.map(line => {
        val lines = line.split(" ")
        (lines.head, lines.tail.map(x => Math.abs(x.toDouble)).product)
      })
      val sortRdd = res.toList.sortWith((a, b) => a._2 < b._2)

      val devResult = dev(sortRdd)
      // val rsult = sortRdd.map(x => x._1 + " " + x._2)
      devResult.toIterator
      //sortRdd
    })
    resultRdd.repartition(1)

    //resultRdd.saveAsTextFile("file:///" + path + "/dept5")

    writeToFile(resultRdd)
    writeToOracle(spark, resultRdd)

  }

  def dev(rdd: List[(String, Double)]): List[String] = {

    val doctors = rdd.map(m => {
      val id = m._1
      val d = rdd.map(f => {

        (f._1, Math.abs(m._2 - f._2))
      }).filter(id != _._1)

      val sortR = d.toList.sortWith((a, b) => a._2 < b._2)
      //同科室
      //val same = sortR.filter(_._1.split("-")(1) == id.split("-")(1))
      val same = sortR.filter(f => {
        val slave = f._1.split("-")
        val master = id.split("-")
        (slave(0) + slave(1)) == (master(0) + master(1))
      })
      val lb = new ListBuffer[(String, Double)]
      lb ++= same
      sortR.foreach(s => {
        if (!same.contains(s))
          lb += s
        s
      })

      //val r = lb.take(5).map(s => {
      val r = lb.take(150).map(s => {
        //s._1 + "||" + s._2
        s._1
      })
      id + " " + r.mkString(",")
    })
    doctors
  }

  def writeToFile(rdd: RDD[String]): Unit = {
    rdd.saveAsTextFile("file:///" + path + "/dept5")
  }

  def writeToOracle(spark: SparkSession, result: RDD[String]): Unit = {
    //标准科室-机构-科室-性别-医生编号 相似医生
    //09-7-1042-0-T001 09-7-1042-0-T002,09-98-20199-0-5483
    val schema = StructType(
      List(
        StructField("S_ORG_ID", StringType, true),
        StructField("ORG_ID", StringType, true),
        StructField("DEPT_CODE", StringType, true),
        StructField("SEX", StringType, true),
        StructField("DOCTOR_CODE", StringType, true),
        StructField("CODES", DataTypes.StringType, true)
      )
    )
    val rowRDD = result.map(_.split(" ")).filter(x => x.length > 1).filter(x => x(0).split("-").length == 5).map(p => {
      val ids = p(0).split("-")
      Row(ids(0).trim, ids(1).trim, ids(2).trim, ids(3).trim, ids(4).trim, p(1).trim)
    })
    val personDataFrame = spark.sqlContext.createDataFrame(rowRDD, schema)
    val prop = new Properties()
    prop.put("user", "urp")
    prop.put("password", "urp")
    personDataFrame.write.mode("append").jdbc("jdbc:oracle:thin:@192.168.2.146:1521:orcl", "rec_pre_doctor_doctorss", prop)
  }
}
