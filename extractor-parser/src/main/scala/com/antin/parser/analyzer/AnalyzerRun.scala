package com.antin.parser.analyzer

import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017/6/23.
  *
  */
object AnalyzerRun {
  def main(args: Array[String]) {

    System.setProperty("HADOOP_USER_NAME", "jcj") //本地运行

    //创建sparkSession
    val ss = SparkSession.builder()
      .master("local[10]") //本地运行
      //.master("yarn") //集群运行
      .appName("AnalyzerRun")
      .getOrCreate()

    try {
      println("====================================================================================================================")
      val startTime = System.currentTimeMillis()
      val analyzer = new DemoAnalyzer(ss, "/yml/hbase/physic.yml,/yml/hbase/examine.yml") //药品、检查
      val dts = analyzer.processDataset
      analyzer.writeDataset(dts)
      val endTime = System.currentTimeMillis()
      println((endTime - startTime) / 1000)
      println("====================================================================================================================")

    } catch {
      case e: Exception => e.printStackTrace()
    }
    finally {
      ss.close
    }
  }
}
