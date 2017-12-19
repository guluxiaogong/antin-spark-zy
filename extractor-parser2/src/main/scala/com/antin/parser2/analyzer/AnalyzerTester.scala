package com.antin.parser2.analyzer

import org.apache.spark.sql.SparkSession

object AnalyzerTester {
  def main(args: Array[String]) {

    System.setProperty("HADOOP_USER_NAME", "jcj")

    val ss = SparkSession.builder()
      .master("local[*]")
      // .master("yarn")
      .appName("AnalyzerTester")
      .getOrCreate()

    try {

      val analyzer = new ParserXmlAnalyzer(ss, "/yml/hbase/physic.yml")

      val dt = analyzer.processDataset

      //analyzer.writeDataset(dt)

    } catch {
      case e: Exception => e.printStackTrace()
    }
    finally {
      ss.close
    }
  }
}
