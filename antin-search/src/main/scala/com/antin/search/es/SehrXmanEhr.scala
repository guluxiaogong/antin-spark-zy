package com.antin.search.es

import java.sql.DriverManager


import com.antin.search.util.{JdbcRDDHelper, StringUtil}
import oracle.sql.BLOB
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable

/**
  * 健康档案数据数据导入ES中（全文检索）
  * Created by jichangjin on 2017/9/27.
  * nohup /usr/hdp/2.6.1.0-129/spark2/bin/spark-submit --class com.antin.es.SehrXmanEhr --master yarn --deploy-mode client --driver-memory 6g --executor-memory 6g  --executor-cores 3 --num-executors 6 /home/jcj/runJar/antin-test.jar >/dev/null 2>1&1 &
  * nohup spark-submit testSpark.jar >/dev/null 2>1&1 &
  */
object SehrXmanEhr {
  def main(args: Array[String]) {
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val url = "jdbc:oracle:thin:@192.168.2.146:1521:orcl"
    // val url = "jdbc:oracle:thin:@192.168.0.91:1521:xmhealth"
    val username = "sehr_hadoop"
    val password = "sehr_hadoop"
    val query = "select * from (select rownum row_num,xman_id, event, catalog_code, serial, content, t.xml.getclobval() xml, compression, encryption, status, version, title, commit_time from sehr_v_xman_ehr_total t) where row_num >=? and row_num <=?"


    val conf = new SparkConf()
      .setAppName("SehrXmanEhr")
      .setMaster("local[10]")
    //.setMaster("yarn")

    conf.set("es.nodes", "hadoop-001,hadoop-002,hadoop-003,hadoop-004,hadoop-005")
    conf.set("es.port", "9200")
    conf.set("es.index.auto.create", "true")

    val sc = new SparkContext(conf)
    val jdbcRDD = JdbcRDDHelper.query(sc, url, username, password, query, 0, 30000000, 10)

    //import org.elasticsearch.spark._
    import org.elasticsearch.spark._
    //    jdbcRDD.saveToEs("health_sehr_xman_ehr_test/sehr_xman_ehr")
    jdbcRDD.saveToEs("hadoop_search-sehr_v_xman_ehr_total/sehr_v_xman_ehr_total")
  }


}
