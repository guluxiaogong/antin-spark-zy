package com.antin.parser2.source

import com.antin.parser2.input.InputConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017-11-24.
  */
abstract class DataFrom(ymlpaths: String) extends Serializable {

  def parserConfig(paths: String): List[InputConfig]

  val configs: List[InputConfig] = parserConfig(ymlpaths): List[InputConfig]

  def loadData(ss: SparkSession): RDD[DataResult]
}
