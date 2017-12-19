package com.antin.base.input
import org.apache.spark.sql.SparkSession

object InputFactory {
  def getInput(conf: InputConfig, ss: SparkSession): Input = conf.source match {
    case "hbase" => new HbaseInput(conf.asInstanceOf[HbaseInputConfig], ss)
  }
}
