package com.antin.extend.analyzer.test.test1

import org.apache.spark.sql.SparkSession

object TestRun {
  def main(args: Array[String]) {

    //System.setProperty("HADOOP_USER_NAME", "jcj")

    val ss = SparkSession.builder()
      //.master("local[*]")
      .master("yarn") //集群运行
      .appName("TestRun")
      //.config("hbase.client.keyvalue.maxsize", "524288000")
      .getOrCreate()

    try {

      val analyzer = new TestAnalyzer(ss, "/yml/hbase/test.yml")

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
