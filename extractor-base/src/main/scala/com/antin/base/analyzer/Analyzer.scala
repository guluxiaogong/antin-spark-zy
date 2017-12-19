package com.antin.base.analyzer

import com.antin.base.input.{HbaseResult, InputFactory, InputConfig}
import org.apache.spark.sql.execution.datasources.hbase.{HBaseRelation, HBaseTableCatalog}
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.reflect.ClassTag
import reflect.runtime.universe.TypeTag

/**
  * Created by Administrator on 2017/6/23.
  */
abstract class Analyzer[K <: Product: ClassTag, R](ss: SparkSession, ymlpath: String) {
  val _inputConfig = InputConfig.load(ymlpath)
  val _input  = InputFactory.getInput(_inputConfig,ss)
  import ss.implicits._
  def  processDataset:Dataset[R]
  def getCatalog:String
  def writeToHbase(dt:Dataset[R]):Unit = {
    dt.toDF.write.options(Map(HBaseTableCatalog.tableCatalog -> getCatalog,
      HBaseTableCatalog.newTable -> "5",
      HBaseRelation.HBASE_CONFIGFILE -> "/etc/hbase/conf/hbase-site.xml"
     ))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .save
  }
}
