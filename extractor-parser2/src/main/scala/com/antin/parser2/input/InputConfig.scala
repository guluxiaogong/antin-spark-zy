package com.antin.parser2.input

import com.antin.parser2.util.CollectionConverters
import org.yaml.snakeyaml.Yaml

abstract class InputConfig extends Serializable {
  protected val config: Map[String, Any]

  protected def checkConfig: Unit = {
    //TODO
  }
  val input = config("input").asInstanceOf[Map[String, Any]]

  val output = config("output").asInstanceOf[String]

 // def source: String = config("source").asInstanceOf[String]
}

//case class Test(id: String, _key: String, title: String)

object InputConfig {
//  def main(args: Array[String]): Unit = {
//    //val path = "/jcj/test-01.yml"
//    // val path = "/yml/MedicalOrg.yml"
//    val path = "/yml/hbase/checkout.yml"
//    val inputConfig = load(path)
//    val ss = SparkSession.builder().master("local").appName("InputConfig")
//      .getOrCreate()
//    try {
//      val input = InputFactory.getInput(inputConfig, ss)
//      input.dataframe.take(5).foreach(println)
//    } finally {
//      ss.close
//    }
//  }

  def load(path: String): InputConfig = {
    val yml = new Yaml()
    val inputStream = this.getClass.getResourceAsStream(path)
    //将java map转成scala tuple
    val map = CollectionConverters.recursivelyToScala(
      yml.load(inputStream).asInstanceOf[java.util.LinkedHashMap[String, Any]]
    )
    //将yml信息封装到HbaseInputConfig中
    val inputConfig = map.get("source") match {
      case Some(source) => source match {
        case "hbase" => new HBaseInputConfig(map)
        case "jdbc" => new OracleInputConfig(map) //throw new UnsupportedOperationException("Not implemented, yet")
        case _ => throw new IllegalArgumentException(s"Unknow source: $source")
      }
      case None => throw new IllegalArgumentException("Missing source")
    }

    inputConfig.checkConfig
    inputConfig
  }

}

