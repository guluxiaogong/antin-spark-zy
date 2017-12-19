package com.antin.parser.db

import com.antin.parser.input.{InputResult, HbaseResult, OracleResult}
import org.apache.hadoop.hbase.client.Scan
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-03.
  */
class DataFromOracle extends DataFrom {
  def loadData(dbMap: Map[String, Any], ss: SparkSession): RDD[InputResult] = {
    null //TODO
  }
}
