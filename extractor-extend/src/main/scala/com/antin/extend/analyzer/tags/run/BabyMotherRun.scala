package com.antin.extend.analyzer.tags.run

import com.antin.extend.analyzer.tags.{BabyMotherAnalyzer, DiabetesAnalyzer}
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-22.
  */
object BabyMotherRun {
  def main(args: Array[String]) {

   // System.setProperty("HADOOP_USER_NAME", "jcj")

    val ss = SparkSession.builder()
      //.master("local[*]")
       .master("yarn")
      .appName("BabyMotherRun")
      //.config("hbase.client.keyvalue.maxsize", "524288000") //最大500m
      .getOrCreate()

    try {
      val analyzer = new BabyMotherAnalyzer(ss, "/yml/oracle/babyMother.yml") //

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
