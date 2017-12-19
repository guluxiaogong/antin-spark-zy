package com.antin.parser2.source

import java.sql.DriverManager

import com.antin.parser2.input.InputConfig
import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.sql.SparkSession


/**
  * Created by Administrator on 2017-11-03.
  */
class DataFromOracle(dbConfig: Map[String, Any], ymlpath: String) extends DataFrom(ymlpath) {


  def parserConfig(paths: String): List[InputConfig] = {
    null
  }

  //连接数据库RDD[mutable.HashMap[String, Any]]
  def loadData(ss: SparkSession): RDD[DataResult] = {

    //val dbConfig = Map("" -> "") //TODO

    val connection = () => {
      Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
      DriverManager.getConnection(dbConfig.get("url").asInstanceOf[String], dbConfig("username").asInstanceOf[String], dbConfig("password").asInstanceOf[String])
    }

    val jdbcRDD = new JdbcRDD(
      ss.sparkContext,
      connection,
      dbConfig("query").asInstanceOf[String],
      dbConfig.get("lowerBound").asInstanceOf[Int], dbConfig.get("upperBound").asInstanceOf[Int], dbConfig.get("numPartitions").asInstanceOf[Int],
      r => new OracleResult(r)
    )
    jdbcRDD.map(x => x)
  }
}
