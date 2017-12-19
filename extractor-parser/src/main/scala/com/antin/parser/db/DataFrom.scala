package com.antin.parser.db

import com.antin.parser.input.{InputResult, HbaseResult}
import org.apache.hadoop.hbase.client.Scan
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-03.
  */
trait DataFrom extends Serializable{
  def loadData(dbMap: Map[String, Any], ss: SparkSession): RDD[InputResult]
}
