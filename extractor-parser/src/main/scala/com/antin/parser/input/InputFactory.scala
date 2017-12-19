package com.antin.parser.input

import com.antin.parser.db.DataFromFactory
import org.apache.spark.sql.SparkSession

//工厂类
object InputFactory {
  def getInputs(dbMap: Map[String, Any], confs: List[InputConfig], ss: SparkSession): List[Input] = dbMap.get("source") match {
    case Some(source) => source match {
      case "hbase" => confs.map(conf => new HbaseInput(conf.asInstanceOf[HbaseInputConfig], ss, DataFromFactory.dataFromHBase.loadData(dbMap("config").asInstanceOf[Map[String, Any]], ss)))
      // case "jdbc" => confs.map(conf => new OracleInput(conf.asInstanceOf[OracleInputConfig], ss, DataFromFactory.dDataFromOracle().loadData(dbMap("config").asInstanceOf[Map[String, Any]], ss)))
      case _ => throw new IllegalArgumentException("Unknow type of source...")
    }
  }


}
