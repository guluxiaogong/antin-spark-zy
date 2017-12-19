package com.antin.base.analyzer

import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{Row, Dataset, DataFrame, SparkSession}
import org.apache.spark.sql.execution.datasources.hbase.{HBaseTableCatalog, HBaseRelation}
import org.apache.spark.SparkConf
import com.antin.base.util.AutoClose.open
import com.antin.base.input.{InputConfig, InputFactory, HbaseResult}

// case class Result(val xman_id: String, val medicine_habit: String)

object BaseinfoAnalyzer {
  case class Record1(
    val XMAN_ID: String,
    val age: Option[Int],
    val weight: Option[Int],
    val marriage: String,
    val work: Option[Int],
    val sex: String,
    val name: String,
    val address: String,
    val id_no: String,
    val birthDate: String
  )

  def catalog = s"""{
       |"table":{"namespace":"default", "name":"EHR_R"},
       |"rowkey":"key",
       |"columns":{
         |"XMAN_ID":{"cf":"rowkey", "col":"key", "type":"string"},
         |"age":{"cf":"i","col":"age","type":"int"},
         |"weight":{"cf":"i","col":"weight","type":"int"},
         |"marriage":{"cf":"i","col":"marriage","type":"string"},
         |"work":{"cf":"i","col":"work","type":"int"},
         |"sex":{"cf":"i", "col":"sex", "type":"int"},
         |"name":{"cf":"i", "col":"name", "type":"string"},
         |"birthDate":{"cf":"i", "col":"birth_date", "type":"string"},
         |"id_no":{"cf":"i", "col":"id_no", "type":"string"},
         |"address":{"cf":"i", "col":"address", "type":"string"}
       |}
     |}""".stripMargin

  def main(args: Array[String]): Unit = {
    val inputConfig = InputConfig.load("/baseinfo.yml")
    val sparkConf = new SparkConf()

    open[SparkSession, Unit](SparkSession.builder().config(sparkConf).getOrCreate()) { ss =>
      import ss.implicits._
      val input = InputFactory.getInput(inputConfig, ss)
      val in: Dataset[Record1] = input.dataset[Record1] { args: (String, HbaseResult) =>
        Bytes.toString(args._2("i")("CATALOG_CODE")) == "0301"
      }
      in.write.
        //csv("baseinfo.csv")
        option(HBaseTableCatalog.tableCatalog, catalog).
        option(HBaseTableCatalog.newTable, "5").
        option(HBaseRelation.HBASE_CONFIGFILE, "/usr/hdp/current/hbase-client/conf/hbase-site.xml").
        format("org.apache.spark.sql.execution.datasources.hbase").
        save
    }
  }
}
