package com.antin.extend.analyzer.test.test1

import org.apache.spark.sql.SparkSession
import spire.implicits

/**
  * Created by Administrator on 2017-11-30.
  */
object Counter {
  def main(args: Array[String]) {

    // System.setProperty("HADOOP_USER_NAME", "jcj")

    val spark = SparkSession.builder()
      // .master("local[*]")
      .master("yarn") //集群运行
      .appName("TestRun")
      // .config("hbase.client.keyvalue.maxsize", "524288000")
      .getOrCreate()

    //链接hbase
    //    val hbaseConf = HBaseConfiguration.create()
    //    hbaseConf.set(TableInputFormat.INPUT_TABLE, "test:jcj_temp")
    //    hbaseConf.set(TableInputFormat.SCAN_COLUMN_FAMILY, "i")
    //
    //    val sc = ss.sparkContext
    //    sc.newAPIHadoopRDD(hbaseConf,
    //      classOf[TableInputFormat],
    //      classOf[ImmutableBytesWritable],
    //      classOf[Result]).
    //      map { case (rowKey, result) =>
    //        rowKey
    //      }

    import spark.implicits._
    val keys = spark.read.text("hdfs://hadoop-cluster/apps/hive/warehouse/test.db/jcj_temp_key/")
    //keys.show(false)
    val xmanIds = keys.select("value").map(v => v.getAs[String]("value").split("_")(0))
    val xmanIds2 = keys.select("value").map(v => v.getAs[String]("value").substring(0,80))
    //xmanIds.show(false)
    xmanIds2.show(false)
    xmanIds.distinct().count()
    xmanIds2.distinct().count()

  }
}
