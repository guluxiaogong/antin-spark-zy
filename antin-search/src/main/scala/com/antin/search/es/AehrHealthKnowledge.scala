package com.antin.search.es


import com.antin.search.util.JdbcRDDHelper
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 健康知识数据数据导入ES中（全文检索）
  * Created by jichangjin on 2017/9/27.
  * nohup /usr/hdp/2.6.1.0-129/spark2/bin/spark-submit --class com.antin.es.SehrXmanEhr --master yarn --deploy-mode client --driver-memory 6g --executor-memory 6g  --executor-cores 3 --num-executors 6 /home/jcj/runJar/antin-test.jar >/dev/null 2>1&1 &
  * nohup spark-submit testSpark.jar >/dev/null 2>1&1 &
  */
object AehrHealthKnowledge {
  def main(args: Array[String]) {
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val url = "jdbc:oracle:thin:@192.168.0.91:1521:xmhealth"
    val username = "sehr"
    val password = "sehr"
    val query = "select * from (select rownum row_num,t.* from aehr_health_knowledge t) where row_num >=? and row_num <=?"


    val conf = new SparkConf()
      .setAppName("AehrHealthKnowledge")
      .setMaster("local[1]")
    //.setMaster("yarn")

    conf.set("es.nodes", "hadoop-001,hadoop-002,hadoop-003,hadoop-004,hadoop-005")
    conf.set("es.port", "9200")
    conf.set("es.index.auto.create", "true")
    conf.set("es.mapping.id", "id")
    conf.set("es.mapping.exclude", "id")

    val sc = new SparkContext(conf)
    val jdbcRDD = JdbcRDDHelper.query(sc, url, username, password, query)
  //TODO   val content = StringUtil.streamToBytes(stream)
    //import org.elasticsearch.spark._
    import org.elasticsearch.spark._
    //    jdbcRDD.saveToEs("health_sehr_xman_ehr_test/sehr_xman_ehr")
    jdbcRDD.saveToEs("hadoop_search-aehr_health_knowledge/aehr_health_knowledge")
  }


}
