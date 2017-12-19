package com.antin.base.input

import java.io.FileInputStream
import org.yaml.snakeyaml.Yaml
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{SparkSession, Dataset}
import com.antin.base.util.CollectionConverters

abstract class InputConfig extends Serializable {
  protected val config: Map[String, Any]
  protected def checkConfig: Unit = {
  }

  def source: String = config("source").asInstanceOf[String]
}

case class Test(id: String, _key: String, title: String)

object InputConfig {
  def main(args: Array[String]): Unit = {
    val path = "/test.yml"
    val inputConfig = load(path)
    val ss = SparkSession.builder()
      .getOrCreate()
    try {
      val input = InputFactory.getInput(inputConfig, ss)
      input.dataframe.take(5).foreach(println)
    } finally {
      ss.close
    }
  }

  def load(path: String): InputConfig = {
    val yml = new Yaml()
    val inputStream = getClass.getResourceAsStream(path)
    val map = CollectionConverters.recursivelyToScala(
      yml.load(inputStream).asInstanceOf[java.util.LinkedHashMap[String, Any]]
    )
    val inputConfig = map.get("source") match {
      case Some(source) => source match {
        case "hbase" => new HbaseInputConfig(map)
        case "jdbc" => throw new UnsupportedOperationException("Not implemented, yet")
        case _ => throw new IllegalArgumentException(s"Unknow source: $source")
      }
      case None => throw new IllegalArgumentException("Missing source")
    }

    inputConfig.checkConfig
    inputConfig
  }

}

