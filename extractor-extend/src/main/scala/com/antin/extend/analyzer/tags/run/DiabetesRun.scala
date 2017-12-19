package com.antin.extend.analyzer.tags.run

import com.antin.extend.analyzer.tags.DiabetesAnalyzer
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-22.
  */
object DiabetesRun {
  def main(args: Array[String]) {

    //System.setProperty("HADOOP_USER_NAME", "jcj")

    val ss = SparkSession.builder()
     // .master("local[*]")
       .master("yarn")
      .appName("PregnanRun")
      .config("hbase.client.keyvalue.maxsize", "524288000") //最大500m
      .getOrCreate()

    try {
      val analyzer = new DiabetesAnalyzer(ss, "/yml/hbase/sehr_chronic_register_rpt.yml") //高血压

      val dt = analyzer.processDataset

      analyzer.writeDataset(dt)


      //sehrXman.writeDataset(sehrXmanDT)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    finally {
      ss.close
    }

  }
}
