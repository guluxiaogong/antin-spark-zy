package com.antin.extend.test.hbase

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog

/**
  * Created by jichangjin on 2017/9/21.
  */
object PortraitUser {
  def main(args: Array[String]) {
    //创建sparkSession
    val ss = SparkSession.builder()
      .master("local[10]") //本地运行
      .appName("PortraitUser")
      .getOrCreate()

    val schema =
      s"""{
          |"table":{"namespace":"hpor", "name":"ehr_r"},
          |"rowkey":"xmanid",
          |"columns":{
          |"xmanid":{"cf":"rowkey", "col":"xmanid", "type":"string"},
          |"name":{"cf":"population", "col":"name", "type":"string"},
          |"age":{"cf":"population", "col":"age", "type":"string"},
          |"sex":{"cf":"population", "col":"sex", "type":"String"},
          |"weight":{"cf":"population", "col":"weight", "type":"String"},
          |"birthDate":{"cf":"population", "col":"birthDate", "type":"string"},
          |"idNo":{"cf":"population", "col":"idNo", "type":"string"},
          |"address":{"cf":"population", "col":"address", "type":"string"},
          |"marriage":{"cf":"population", "col":"marriage", "type":"string"},
          |"work":{"cf":"population", "col":"work", "type":"string"},
          |"diabetes":{"cf":"health", "col":"diabetes", "type":"string"},
          |"hypertension":{"cf":"health", "col":"hypertension", "type":"string"},
          |"pregnant":{"cf":"health", "col":"pregnant", "type":"string"},
          |"ageGroup":{"cf":"health", "col":"ageGroup", "type":"string"},
          |"babyMF":{"cf":"health", "col":"babyMF", "type":"string"}
          |}
          |}""".stripMargin


    val resultDF = ss.read
      .options(Map(HBaseTableCatalog.tableCatalog -> schema))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .load()
    resultDF.createOrReplaceTempView("hadoop_portrait")
    //val resultSql = ss.sql("select * from hadoop_portrait where name is not null and (diabetes is not null or hypertension is not null or pregnant is not null or ageGroup is not null or babyMF is not null)")
    //val resultSql = ss.sql("select * from hadoop_portrait where name is not null or age is not null and sex is not null or weight is not null and ageGroup is not null and marriage is not null or work is not null")
    //val resultSql = ss.sql("select * from hadoop_portrait where diabetes is not null or hypertension is not null or babyMF is not null")
    val resultSql = ss.sql("select * from hadoop_portrait where diabetes is not null")

    resultSql.show(1000, false)

  }

}
