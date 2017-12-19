package com.antin.extend.analyzer.tags

import java.text.SimpleDateFormat
import java.util

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.model.BabyMFModel
import com.antin.extend.util.{JsonHelper, DateHelper}
import org.apache.spark.sql.{Dataset, SparkSession}
import scala.collection.JavaConverters._

/**
  * Created by jichangjin on 2017/9/19.
  */
class BabyMotherAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, BabyMFModel](ss, ymlpath) {
  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"hadoop", "name":"ehr_r"},
        |"rowkey":"xmanId",
        |"columns": {
        |"xmanId":{"cf":"rowkey", "col":"xmanId","type":"string"},
        |"babyMF":{"cf":"h", "col":"BABY_MF", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[BabyMFModel] = {
    import ss.implicits._
    val dt = _input.dataframe

    val sehrXman = new SehrXmanAnalyzer(ss, "/yml/hbase/sehr-xman.yml")
    val sehrXmanDT = sehrXman.processDataset

    sehrXmanDT.toDF().createOrReplaceTempView("sehr_xman")

    dt.createOrReplaceTempView("woman_birthrecord")
    //val resultDF = ss.sql("select t1.idNo,t1.gestDtime from woman_birthrecord t1 where t1.idNo is not null")
    val resultDF = ss.sql("select t2.xmanId,t1.gestDtime from woman_birthrecord t1,sehr_xman t2 where t1.idNo is not null and t1.idNo <> '' and t1.idNo=t2.idNo")

    val resultModel = resultDF.rdd.groupBy(_ (0)).map(m => {
      val maps = m._2.map(r => {
        //一个人可能有多个孩子
        val sdf = new SimpleDateFormat("yyyy-MM-dd")
        val map: java.util.Map[String, String] = new util.HashMap[String, String]
        try {
          map.put("name", "宝妈")
          map.put("type", "9")
          map.put("parentCode", null)
          val startTime = DateHelper.converToFormat(r(1).toString)
          val endTime = sdf.format(DateHelper.devYear(sdf.parse(startTime), 6))
          map.put("startTime", startTime)
          map.put("endTime", endTime) //6周岁以下的为宝妈
          map
        } catch {
          case e: Exception => {
            e.printStackTrace()
            null
          }
        }
      }).filter(f => f != null)
      BabyMFModel(m._1.toString.trim, JsonHelper.obj2JsonString(maps.asJava))
    })
    resultModel.toDS
  }


  //  override def writeDataset(dt: Dataset[BabyMFModel]): Unit = {
  //    dt.show(false)
  //
  //  }
}
