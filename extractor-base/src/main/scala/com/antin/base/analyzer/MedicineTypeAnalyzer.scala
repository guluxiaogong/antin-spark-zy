/*
package com.antin.base.analyzer

import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{Row, Dataset, DataFrame, SparkSession}
import org.apache.spark.SparkConf
import com.antin.base.util.AutoClose.open
import com.antin.base.input.{InputConfig, InputFactory, HbaseResult}
import org.apache.spark.sql.execution.datasources.hbase.{HBaseTableCatalog, HBaseRelation}

case class Record(val XMAN_ID: String, var medicineType: Seq[Int])

case class Result(val xman_id: String, val medicine_habit: String)

object MedicineTypeAnalyzer {
  def catalog =
    s"""{
        |"table":{"namespace":"default", "name":"EHR_R"},
        |"rowkey":"key",
        |"columns":{
        |"xman_id":{"cf":"rowkey", "col":"key", "type":"string"},
        |"medicine_habit":{"cf":"r", "col":"medicine_habit", "type":"string"}
        |}
        |}""".stripMargin

  def main(args: Array[String]): Unit = {
    val inputConfig = InputConfig.load("/medicine-habit.yml")
    val sparkConf = new SparkConf()
    sparkConf.setMaster("local[10]")

    open[SparkSession, Unit](SparkSession.builder().config(sparkConf).getOrCreate()) { ss =>
      import ss.implicits._
      val ss = new SparkSession()
      val input = InputFactory.getInput(inputConfig, ss)
      val in = input.dataset[Record] { args: (String, HbaseResult) =>
        Bytes.toString(args._2("i")("CATALOG_CODE")) == "0141"
      }
      in.map { r => Record(r.XMAN_ID, r.medicineType.filter {
        _ != 0
      })
      }.
        filter {
          _.medicineType.nonEmpty
        }.
        groupByKey { r => r.XMAN_ID }.
        mapGroups { (k, groups) => Record(k, groups.flatMap(_.medicineType).toSeq) }.
        map { case Record(xman_id, medicineType) =>
          (xman_id, medicineType.groupBy {
            case 2 => 2
            case 3 => 3
            case _ => 1
          })
        }.
        map { case (xman_id, groups) => (xman_id, groups.map { case (k, v) => (k -> v.size) }) }.
        map { case (xman_id, groups) => (xman_id, Map(1 -> 0, 2 -> 0, 3 -> 0) ++ groups.toSeq) }.
        map { case (xman_id, groups) => Result(xman_id, groups.toSeq.sorted.map(_._2).mkString(":")) }.
        write.option(HBaseTableCatalog.tableCatalog, catalog).
        option(HBaseTableCatalog.newTable, "5").
        option(HBaseRelation.HBASE_CONFIGFILE, "/usr/hdp/2.6.1.0-129/hbase/conf/hbase-site.xml").
        format("org.apache.spark.sql.execution.datasources.hbase").
        save()
    }
  }
}
*/
