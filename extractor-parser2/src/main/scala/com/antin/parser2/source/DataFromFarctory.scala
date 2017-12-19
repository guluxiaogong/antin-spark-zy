package com.antin.parser2.source

import com.antin.parser2.input.DBConfig


/**
  * Created by Administrator on 2017-11-24.
  */
object DataFromFarctory {
  //val dbConfig = DBConfig.loadDB("/db/hbase-db.yml")

  def dataFrom(ymlpaths: String): DataFrom = {
    DBConfig.source match {
      case Some(source) => source match {
        case "hbase" => new DataFromHBase(DBConfig.config, ymlpaths)
        case "jdbc" => new DataFromOracle(DBConfig.config, ymlpaths)
        case _ => throw new IllegalArgumentException(s"Unknow source: $source")
      }
      case None => throw new IllegalArgumentException("Missing source")
    }
  }
}
