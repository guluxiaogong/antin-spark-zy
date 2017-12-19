package com.antin.extend.analyzer.test.test3

import com.antin.extend.analyzer.Analyzer
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
  * Created by Administrator on 2017-11-29.
  */
class PregnancySmjkindexAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, Row](ss, ymlpath) {
  def processDataset: DataFrame = _input.dataframe
}
