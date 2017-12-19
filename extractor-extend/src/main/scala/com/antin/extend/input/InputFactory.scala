package com.antin.extend.input

import org.apache.spark.sql.SparkSession

//工厂类
object InputFactory {
  def getInput(conf: InputConfig, ss: SparkSession): Input = conf.source match {
    case "hbase" => new HbaseInput(conf.asInstanceOf[HbaseInputConfig], ss)
    case "jdbc" => new OracleInput(conf.asInstanceOf[OracleInputConfig], ss)
    case _ => throw new IllegalArgumentException("Unknow type of source...")
  }
}
