package com.antin.parser2.input

import com.antin.parser2.util.CollectionConverters
import org.yaml.snakeyaml.Yaml

/**
  * Created by Administrator on 2017-11-24.
  */
object DBConfig {
  private val dbConfig = loadDB("/db/hbase-db.yml")
  val source: Option[Any] = dbConfig.get("source")
  val config: Map[String, Any] = dbConfig("config").asInstanceOf[Map[String, Any]]

  /**
    * 加载数据源配置文件
    *
    * @param path
    * @return
    */
  def loadDB(path: String): Map[String, Any] = {
    val yml = new Yaml()
    val inputStream = this.getClass.getResourceAsStream(path)
    //将java map转成scala tuple
    CollectionConverters.recursivelyToScala(
      yml.load(inputStream).asInstanceOf[java.util.LinkedHashMap[String, Any]]
    )
  }
}
