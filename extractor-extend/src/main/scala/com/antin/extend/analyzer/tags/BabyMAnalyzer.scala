package com.antin.extend.analyzer.tags

import java.text.SimpleDateFormat
import java.util
import java.util.regex.{Matcher, Pattern}

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.model.BabyMFModel
import com.antin.extend.util.{JsonHelper, DateHelper}
import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import scala.collection.JavaConverters._

/**
  * Created by jichangjin on 2017/9/19.
  * 宝妈和宝爸
  */
class BabyMAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, BabyMFModel](ss, ymlpath) {
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
    //import ss.implicits._
    val dt = _input.dataframe


    val sehrXman = new SehrXmanAnalyzer(ss, "/yml/hbase/sehr-xman.yml")
    val sehrXmanDT = sehrXman.processDataset

    sehrXmanDT.filter(x => regexMatch(x.idNo)).toDF().createOrReplaceTempView("sehr_xman")

    dt.createOrReplaceTempView("etl_xman_birthcertificate")
    // val resultFDF = ss.sql("select t1.birthTime,t2.xmanId,t1.fatherIdNo,t1.xmanId from etl_xman_birthcertificate t1,sehr_xman t2 where t1.fatherIdNo is not null and t1.fatherIdNo <>'' and t1.fatherIdNo=t2.idNo")
    // val resultMDF = ss.sql("select t1.birthTime,t2.xmanId,t1.motherIdNo,t1.xmanId from etl_xman_birthcertificate t1,sehr_xman t2 where t1.motherIdNo is not null and t1.motherIdNo <>'' and t1.motherIdNo=t2.idNo") //TODO
    val resultMDF = ss.sql("select t1.birthTime,t2.xmanId,t1.motherIdNo from etl_xman_birthcertificate t1,sehr_xman t2 where t1.motherIdNo is not null and t1.motherIdNo <>'' and t1.motherIdNo=t2.idNo") //TODO

    motherAndFather(resultMDF, "宝妈", "9")
    // motherAndFather(resultFDF, "宝爸", "10")
  }

  def motherAndFather(dataset: DataFrame, name: String, types: String): Dataset[BabyMFModel] = {
    import ss.implicits._
    val resultModel = dataset.rdd.groupBy(_ (1)).map(m => {
      val maps = m._2.map(r => {
        //一个人可能有多个孩子
        val map: java.util.Map[String, String] = new util.HashMap[String, String]
        try {
          map.put("name", name)
          map.put("type", types)
          map.put("parentCode", null)
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val startTime = DateHelper.converToFormat(r(0).toString.split(" ")(0))
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
      BabyMFModel(m._1.toString, JsonHelper.obj2JsonString(maps.asJava))
    })
    resultModel.toDS
  }

  def regexMatch(checkValue: String): Boolean = {
    if (StringUtils.isEmpty(checkValue))
      false
    else {
      val regex: String = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}[0-9Xx]$)"
      val p: Pattern = Pattern.compile(regex)
      val m: Matcher = p.matcher(checkValue)
      m.matches
    }

  }

  /*  override def writeDataset(dt: Dataset[BabyMFModel]): Unit = {
      dt.show(false)
    }*/
}
