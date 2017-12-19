package com.antin.extend.analyzer.tags

import com.antin.extend.analyzer.Analyzer
import org.apache.spark.sql.{Dataset, Row, SparkSession}



/**
  * Created by Administrator on 2017-11-21.
  */
class SehrXmanAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, SehrXmanModel](ss, ymlpath) {
  def processDataset: Dataset[SehrXmanModel] = {
    val resultDS = _input.dataframe.rdd.map(row=>{
      SehrXmanModel(row.getAs[String]("_key"),row.getAs[String]("idNo"))
    })
    import ss.implicits._
    resultDS.toDS
  }

  override def writeDataset(dt: Dataset[SehrXmanModel]): Unit = {
    dt.show(500, false)

  }
}
