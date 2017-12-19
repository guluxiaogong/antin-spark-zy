package com.antin.extend.analyzer.guide.run

import com.antin.extend.analyzer.guide.ExamineAnalyzer
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-02.
  */
object ExamineRun {
  def main(args: Array[String]) {
   // System.setProperty("HADOOP_USER_NAME", "jcj") //本地运行
    //创建sparkSession
    val ss = SparkSession.builder()
        //.master("local[*]") //本地运行
         .master("yarn") //集群运行
        .appName("ExamineRun")
        .config("hbase.client.keyvalue.maxsize", "524288000") //最大500m
        .getOrCreate()

    try {
      val analyzer = new ExamineAnalyzer(ss, "/yml/hbase/examine.yml") //检查
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
