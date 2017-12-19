package com.antin.parser.input

import scala.collection.mutable

//封装yml文件配置信息类
class HbaseInputConfig(protected val config: Map[String, Any]) extends InputConfig {
  override def checkConfig: Unit = {
  }

//  val input = config("input").asInstanceOf[Map[String, Any]]
//
//  val output = config("output").asInstanceOf[Option[String]]

  val include = input("include").asInstanceOf[Seq[Map[String, Any]]]

  val fields = input("fields").asInstanceOf[Seq[Map[String, Any]]]

  val family = input.get("family").asInstanceOf[Option[String]]

  val onlyLastVersion = input.getOrElse("only-last-version", true).asInstanceOf[Boolean]

}
