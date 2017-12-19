package com.antin.extend.analyzer.tags.run

import com.antin.extend.analyzer.tags.{BabyMAnalyzer, BabyMotherAnalyzer}
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-22.
  */
object BabyMRun {
  def main(args: Array[String]) {

    //System.setProperty("HADOOP_USER_NAME", "jcj")

    val ss = SparkSession.builder()
      // .master("local[*]")
      .master("yarn")
      .appName("BabyMFRun")
      //.config("hbase.client.keyvalue.maxsize", "524288000") //最大500m
      .getOrCreate()

    try {
      val analyzerM = new BabyMAnalyzer(ss, "/yml/oracle/babyM.yml") //
      val dtM = analyzerM.processDataset
      analyzerM.writeDataset(dtM)

    } catch {
      case e: Exception => e.printStackTrace()
    }
    finally {
      ss.close
    }

  }
}
