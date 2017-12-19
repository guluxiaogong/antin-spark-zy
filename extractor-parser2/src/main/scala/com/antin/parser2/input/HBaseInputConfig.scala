package com.antin.parser2.input

//封装yml文件配置信息类
class HBaseInputConfig(protected val config: Map[String, Any]) extends InputConfig {
  override def checkConfig: Unit = {
  }
  val include = input("include").asInstanceOf[Seq[Map[String, Any]]]

  val fields = input("fields").asInstanceOf[Seq[Map[String, Any]]]

  val family = input.get("family").asInstanceOf[Option[String]]

  val onlyLastVersion = input.getOrElse("only-last-version", true).asInstanceOf[Boolean]

}
