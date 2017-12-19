package com.antin.recom.feature.test

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext;

/**
  * Created by wds on 2017/11/13.
  */
object SparkHiveTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new HiveContext(sc)
    sqlContext.table("test.person") // 库名.表名 的格式
      .registerTempTable("person") // 注册成临时表
    sqlContext.sql(
      """
        | select *
        |   from person
        |  limit 10
      """.stripMargin).show()
    sc.stop()
  }

}
