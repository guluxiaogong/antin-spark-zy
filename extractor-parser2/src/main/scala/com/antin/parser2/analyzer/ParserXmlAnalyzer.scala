package com.antin.parser2.analyzer

import com.antin.parser2.input.{HBaseInputConfig, HBaseParser}
import com.antin.parser2.source.{DataResult, DataResultSet, HBaseResult}
import org.apache.spark.sql.{DataFrame, Row, Dataset, SparkSession}

/**
  * Created by Administrator on 2017-11-24.
  */
class ParserXmlAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer(ss, ymlpath) {

  def processDataset: DataFrame = {
    val configBroadcast = ss.sparkContext.broadcast(configs)
    sourceData.map(s => {
      val hBaseResult = s.asInstanceOf[HBaseResult]
      val configsBroadcast = configBroadcast.value
      configsBroadcast.map(config => {
        //new HBaseInput(ss, c, hBaseResult)//TODO
        new HBaseParser(ss, config.asInstanceOf[HBaseInputConfig], hBaseResult)
      })
    })

    sourceData.map(_.asInstanceOf[HBaseResult]).map(row => {
      row("i")("XMAN_ID")
    }).take(10).foreach(f => println(f))

    // println(sourceData.asInstanceOf[HBaseResult]("i")("XMAN_ID"))
    null
  }

  override def writeDataset(dt: DataFrame): Unit = {
    dt.show(false)
  }
}
