package com.antin.parser.input

import com.antin.parser.util.CollectionConverters
import org.yaml.snakeyaml.Yaml

abstract class InputConfig extends Serializable {
  val input = config("input").asInstanceOf[Map[String, Any]]

  val output = config("output").asInstanceOf[String]
  //match {
//    case Some(s) => s //TODO//check
//    case None => throw new IllegalArgumentException("output is nothing...")
//  }

  protected val config: Map[String, Any]

  protected def checkConfig: Unit = {
    //TODO
  }

  def source: String = config("source").asInstanceOf[String]
}

object InputConfig {

  //加载yaml文件，将信息封装成inputConfig对象
  def load(paths: String, dbType: Option[Any]): List[InputConfig] = {
    val yml = new Yaml()
    val files = paths.split(",").map(_.trim).toList
    val nodesMaps = files.map(f => {
        val inputStream = this.getClass.getResourceAsStream(f)
        val nodesMap = CollectionConverters.recursivelyToScala(
          yml.load(inputStream).asInstanceOf[java.util.LinkedHashMap[String, Any]]
        )
        nodesMap
      })
    //将yml信息封装到HbaseInputConfig中
    val inputConfigs = dbType match {
      case Some(source) => source match {
        case "hbase" => nodesMaps.map(f => new HbaseInputConfig(f))
        case "jdbc" => nodesMaps.map(f => new OracleInputConfig(f))
        case _ => throw new IllegalArgumentException(s"Unknow source: $source")
      }
      case None => throw new IllegalArgumentException("Missing source")
    }
    //inputConfig.checkConfig //TODO
    inputConfigs
  }

  /**
    * 加载数据源配置文件
    *
    * @param path
    * @return
    */
  def loadDB(path: String): Map[String, Any] = {
    val yml = new Yaml()
    val inputStream = this.getClass.getResourceAsStream("/db/hbase-db.yml")
    //将java map转成scala tuple
    CollectionConverters.recursivelyToScala(
      yml.load(inputStream).asInstanceOf[java.util.LinkedHashMap[String, Any]]
    )
  }

}

