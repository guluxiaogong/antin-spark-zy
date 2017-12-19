package com.antin.extend.analyzer.tags.run

import com.antin.extend.analyzer.tags.BaseInfoNewAnalyzer
import org.apache.spark.sql.SparkSession

/**
  *
  */
object BaseInfoRun {
  def main(args: Array[String]) {

    //System.setProperty("HADOOP_USER_NAME", "jcj") //本地运行

    //创建sparkSession
    val ss = SparkSession.builder()
      //.master("local[*]") //本地运行
      .master("yarn") //集群运行
      .appName("BaseInfoRun")
      .config("hbase.client.keyvalue.maxsize", "524288000") //最大500m
      //.config("hbase.zookeeper.quorum", "hadoop-001,hadoop-002,hadoop-003")
      //.config("zookeeper.znode.parent", "/hbase-unsecure")
      .getOrCreate()

    try {
      /*
       * 用户画像标签计算
       */
      val analyzer = new BaseInfoNewAnalyzer(ss, "/yml/hbase/baseInfo.yml") //基本信息

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
