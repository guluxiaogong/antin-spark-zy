package com.antin.extend.analyzer.tags

import java.util

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.model.HypertensionModel
import com.antin.extend.util.JsonHelper
import org.apache.spark.sql._

import scala.collection.JavaConverters._

/**
  * Created by jichangjin on 2017/9/18.
  * 高血压
  */
class HypertensionAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, HypertensionModel](ss, ymlpath) {
  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"hadoop", "name":"ehr_r"},
        |"rowkey":"xmanId",
        |"columns": {
        |"xmanId":{"cf":"rowkey", "col":"xmanId","type":"string"},
        |"hypertension":{"cf":"h", "col":"HYPERTENSION", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[HypertensionModel] = {
    import ss.implicits._
    val dt = _input.dataframe
    dt.createOrReplaceTempView("sehr_chronic_register_rpt")
    val resultDF = ss.sql("select xmanId,category,registrationDate,lasttime from sehr_chronic_register_rpt where category='高血压' and xmanId is not null and xmanId <> ''")
      .toDF("id", "name", "startTime", "endTime").rdd
    val resultModel = resultDF.groupBy(_ (0)).map(m => {
      val maps = m._2.map(r => {
        val map: java.util.Map[String, String] = new util.HashMap[String, String]
        //val map= new mutable.HashMap[String, String]
        map.put("name", r(1).toString)
        map.put("parentCode", "chronic")
        map.put("type", "0")
        map.put("startTime", r(2).toString)
        map.put("endTime", r(3).toString)
        map.put("code", "I10.x00")
        map
      })
      HypertensionModel(m._1.toString, JsonHelper.obj2JsonString(maps.asJava))
    })
    resultModel.toDS
  }

  //  override def writeDataset(dt: Dataset[HypertensionModel]): Unit = {
  //    dt.show(false)
  //
  //  }
}
