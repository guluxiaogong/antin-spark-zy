package com.antin.parser2.analyzer

import com.antin.parser2.source.DataFromFarctory
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.{DataFrame, SparkSession}

abstract class Analyzer(ss: SparkSession, ymlpath: String) extends Serializable {
  val dataF = DataFromFarctory.dataFrom(ymlpath)
  val sourceData = dataF.loadData(ss)
  val configs = dataF.configs

  def processDataset: DataFrame

  def getCatalog: String = null

  def writeDataset(dt: DataFrame): Unit = {

    dt.toDF.write.options(Map(HBaseTableCatalog.tableCatalog -> getCatalog, HBaseTableCatalog.newTable -> "5")) //你需要额外传递给驱动的参数
      .format("org.apache.spark.sql.execution.datasources.hbase") //驱动程序，类似JDBC的 driver class
      .save
  }
}
