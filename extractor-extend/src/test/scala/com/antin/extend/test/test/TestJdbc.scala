package com.antin.extend.test.test

import java.sql.DriverManager

import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by jichangjin on 2017/9/16.
  */
object TestJdbc {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("TestJdbc").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val connection = () => {
      Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
      DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.91:1521:xmhealth", "sehr", "sehr")
    }
    val jdbcRDD = new JdbcRDD(
      sc,
      connection,
      "select * from (select rownum row_num,t.* from TEST t) where ? <= row_num and row_num <= ?",
      1, 20, 2,
      r => {
        val id = r.getInt(1)
        val code = r.getString(2)
        (id, code)
      }
    )
    val jrdd = jdbcRDD.collect()
    println(jdbcRDD.collect().toBuffer)
    sc.stop()
  }
}
