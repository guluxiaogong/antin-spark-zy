package com.antin.extend.analyzer.tags.run

import com.antin.extend.analyzer.tags.{BabyFAnalyzer, BabyMAnalyzer}
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-22.
  */
object BabyFRun {
  def main(args: Array[String]) {

   // System.setProperty("HADOOP_USER_NAME", "jcj")

    val ss = SparkSession.builder()
       // .master("local[*]")
       .master("yarn")
      .appName("BabyMFRun")
      //.config("hbase.client.keyvalue.maxsize", "524288000") //最大500m
      .getOrCreate()

    try {
      val analyzerF = new BabyFAnalyzer(ss, "/yml/oracle/babyF.yml") //
      val dtF = analyzerF.processDataset
      analyzerF.writeDataset(dtF)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    finally {
      ss.close
    }

  }
}
