package com.antin.base.analyzer

import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017/6/23.
  */
object AnalyzerTester {
  def main(args: Array[String]) {
    val ss = SparkSession.builder()
      .getOrCreate()
    try {
      val analyzer = new ChronicDiseaseAnalyzer(ss, "/ChronicDisease.yml")
      val dt = analyzer.processDataset
      analyzer.writeToHbase(dt)
    }
    finally {
      ss.close
    }
  }
}
