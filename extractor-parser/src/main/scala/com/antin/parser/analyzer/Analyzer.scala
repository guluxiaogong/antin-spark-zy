package com.antin.parser.analyzer

import com.antin.parser.input.{InputFactory, InputConfig}
import com.antin.parser.util.{JsonUtil, JsonHelper}
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Created by Administrator on 2017/6/23.
  * 父类抽象出的一些公共方法
  */
abstract class Analyzer(ss: SparkSession, ymlpath: String) {
  //加载数据源
  val dbConfig = InputConfig.loadDB("/db/hbase-db.yml")

  //封装成HbaseInputConfig对象
  val inputConfigs = InputConfig.load(ymlpath, dbConfig.get("source"))

  //根据配置信息创建与hbase交互工厂
  val inputs = InputFactory.getInputs(dbConfig, inputConfigs, ss)

  def processDataset: List[(String, DataFrame)]

  //将数据写入hbase
  def writeDataset(dts: List[(String, DataFrame)]): Unit = {
    dts.foreach(dt => {
      //SPARK连接Hbase组件SHC
      dt._2.toDF.write.options(Map(HBaseTableCatalog.tableCatalog -> dt._1, HBaseTableCatalog.newTable -> "5")) //你需要额外传递给驱动的参数
        .format("org.apache.spark.sql.execution.datasources.hbase") //驱动程序，类似JDBC的 driver class
        .save
    })
  }
}
