package com.antin.base.input

class HbaseInputConfig(protected val config: Map[String, Any]) extends InputConfig {
  override def checkConfig: Unit = {
  }

  val hbaseConfig = config("config").asInstanceOf[Map[String, Any]]

  val fields = config("fields").asInstanceOf[Seq[Map[String, Any]]]

  val family = config.get("family").asInstanceOf[Option[String]]

  val onlyLastVersion = config.getOrElse("only-last-version", true).asInstanceOf[Boolean]

}
