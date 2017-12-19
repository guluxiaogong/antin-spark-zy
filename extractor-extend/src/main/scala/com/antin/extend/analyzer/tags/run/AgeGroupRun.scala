package com.antin.extend.analyzer.tags.run

import com.antin.extend.analyzer.tags.AgeGroupAnalyzer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-02.
  */
object AgeGroupRun {
  def main(args: Array[String]) {
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    //System.setProperty("HADOOP_USER_NAME", "jcj")

    val ss = SparkSession.builder()
     // .master("local[*]")
      .master("yarn")
      .appName("AgeGroupRun")
      .config("hbase.client.keyvalue.maxsize", "524288000")
      .getOrCreate()

    try {

      val analyzer = new AgeGroupAnalyzer(ss, "/yml/hbase/ageGroup.yml") //年龄段

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
