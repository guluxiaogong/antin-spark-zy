package com.antin.extend.analyzer.es

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  *
  */
object HBase2Es_0212Run {
  def main(args: Array[String]) {

    // System.setProperty("HADOOP_USER_NAME", "jcj")

    val conf = new SparkConf()
      .setAppName("HBase2EsRun")
      // .setMaster("local[*]")
      .setMaster("yarn")

    conf.set("es.nodes", "hadoop-001,hadoop-002,hadoop-003,hadoop-004,hadoop-005")
    conf.set("es.port", "9200")
    conf.set("es.index.auto.create", "true")
    conf.set("es.mapping.id", "_key")
    conf.set("es.mapping.exclude", "id")

    val ss = SparkSession.builder()
      .config(conf)
      .config("hbase.client.keyvalue.maxsize", "524288000") //最大500m
      .getOrCreate()

    try {

      val analyzer = new HBase2EsAnalyzer(ss, "/yml/hbase/hbase2es_0212.yml", "sehr_xman_ehr_0212/i")

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
