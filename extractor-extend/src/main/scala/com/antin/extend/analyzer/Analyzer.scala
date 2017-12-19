package com.antin.extend.analyzer

import com.antin.extend.input.{InputFactory, InputConfig}
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.{ Dataset, SparkSession}

import scala.reflect.ClassTag

/**
  * Created by Administrator on 2017/6/23.
  * 父类抽象出的一些公共方法
  */
abstract class Analyzer[K <: Product : ClassTag, R](ss: SparkSession, ymlpath: String)extends Serializable{

  //解析yml文件，封装成HbaseInputConfig对象
  val _inputConfig = InputConfig.load(ymlpath)

  //根据配置信息创建与hbase交互工厂
  val _input = InputFactory.getInput(_inputConfig, ss)

  def processDataset: Dataset[R]

  def getCatalog: String = null

  //将数据写入hbase
  def writeDataset(dt: Dataset[R]): Unit = {

    //SPARK连接Hbase组件SHC
    dt.toDF.write.options(Map(HBaseTableCatalog.tableCatalog -> getCatalog, HBaseTableCatalog.newTable -> "5")) //你需要额外传递给驱动的参数
      .format("org.apache.spark.sql.execution.datasources.hbase") //驱动程序，类似JDBC的 driver class
      .save
  }
}
