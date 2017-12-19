package com.antin.extend.analyzer.tags.run

import com.antin.extend.analyzer.tags.PregnantAnalyzer
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-02.
  */
object PregnanRun {
  def main(args: Array[String]) {

    //System.setProperty("HADOOP_USER_NAME", "jcj")

    val ss = SparkSession.builder()
      //.master("local[*]")
      .master("yarn")
      .appName("PregnanRun")
      .config("hbase.client.keyvalue.maxsize", "524288000") //最大500m
      .getOrCreate()

    try {
      //      val sehrXman = new SehrXmanAnalyzer(ss, "/yml/hbase/sehr-xman.yml")
      //      val sehrXmanDT = sehrXman.processDataset
      val analyzer = new PregnantAnalyzer(ss, "/yml/oracle/pregnant.yml") //孕产妇

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
