package com.antin.extend.input

/**
  * Created by jichangjin on 2017/9/14.
  */
class OracleInputConfig(protected val config: Map[String, Any]) extends InputConfig {

  val oracleConfig = config("config").asInstanceOf[Map[String, Any]]
}
