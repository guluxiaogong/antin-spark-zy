package com.antin.extend.analyzer.test.test2

import org.apache.spark.sql.SparkSession

object TestRun2 {
  def main(args: Array[String]) {

    System.setProperty("HADOOP_USER_NAME", "jcj")

    val ss = SparkSession.builder()
      .master("local[*]")
      //.master("yarn") //集群运行
      .appName("TestRun2")
      .config("hbase.client.keyvalue.maxsize", "524288000")
      .getOrCreate()

    try {

      val analyzer = new TestAnalyzer2(ss, "/yml/hbase/test.yml")

      val dt = analyzer.processDataset

      analyzer.writeDataset(dt)

    } catch {
      case e: Exception => e.printStackTrace()
    }
    finally {
      ss.close
    }

  }

}
