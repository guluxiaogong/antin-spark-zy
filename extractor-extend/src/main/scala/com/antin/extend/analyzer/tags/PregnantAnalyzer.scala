package com.antin.extend.analyzer.tags

import java.text.SimpleDateFormat
import java.util

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.model.PregnantModel
import com.antin.extend.util.{DateHelper, StringUtil, JsonHelper}
import org.apache.commons.lang.StringUtils
import org.apache.hadoop.hbase.client.{HTable, Result}
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.collection.JavaConverters._

case class SehrXmanModel(xmanId: String, idNo: String)

/**
  * Created by jichangjin on 2017/9/19.
  */
class PregnantAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, PregnantModel](ss, ymlpath) {
  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"hadoop", "name":"ehr_r"},
        |"rowkey":"xmanId",
        |"columns": {
        |"xmanId":{"cf":"rowkey", "col":"xmanId","type":"string"},
        |"pregnant":{"cf":"h", "col":"PREGNANT", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[PregnantModel] = {

    import ss.implicits._
    val dt = _input.dataframe
    dt.createOrReplaceTempView("woman_prefirstvisit")

    val sehrXman = new SehrXmanAnalyzer(ss, "/yml/hbase/sehr-xman.yml")
    val sehrXmanDT = sehrXman.processDataset

    sehrXmanDT.toDF().createOrReplaceTempView("sehr_xman")

    val resultDF = ss.sql("select t2.xmanId,t1.updateDtime1,t1.startTime,t1.expectedChildbirthDate,t1.updateDtime2,t1.gestationTime,t1.motherIdNo,t1.fatherIdNo,t2.xmanId from woman_prefirstvisit t1,sehr_xman t2 where t1.motherIdNo=t2.idNo and t2.xmanId is not null and t2.xmanId <>'' and t2.idNo is not null and t2.idNo<>''")
    // t1.patient_id

    val resultModel = resultDF.rdd.groupBy(_ (0)).map(m => {
      //每个人的记录
      val maps = m._2.map(p => {
        //每个人每次记录
        val map: java.util.Map[String, String] = new util.HashMap[String, String]
        try {
          map.put("name", "孕产妇")
          map.put("type", "2")
          map.put("parentCode", null)
          val starTime = DateHelper.converToFormat(p(2).toString)
          map.put("startTime", starTime)
          if (!StringUtils.isEmpty(p(5).toString)) {
            val sdf = new SimpleDateFormat("yyyy-MM-dd")
            //有妊娠结局时间 按 妊娠结局时间（妊娠结局时间要在怀孕其间，不能是上次怀孕时间或其他时间）
            val endTime = DateHelper.converToFormat(p(5).toString)
            val sdf1 = sdf.parse(endTime) //妊娠结局时间
            val sdf2 = sdf.parse(starTime) //开始怀孕时间
            val preTime = DateHelper.converToFormat(p(3).toString)
            val sdf3 = sdf.parse(preTime) //预产期时间
            if (sdf1.after(sdf2) && sdf1.before(sdf3))
              map.put("endTime", endTime)
          } else
          //没有妊娠结局时间
            map.put("endTime", DateHelper.converToFormat(p(3).toString))
          map
        } catch {
          case e: Exception => {
            e.printStackTrace()
            null
          }
        }
        //map
      }).filter(f => f != null)
      PregnantModel(m._1.toString, JsonHelper.obj2JsonString(maps.asJava))
    })
    resultModel.toDS
    //  null
  }

  //
  //  override def writeDataset(dt: Dataset[PregnantModel]): Unit = {
  //    dt.show(1000, false)
  //
  //  }
}

//+------------------------------------+---------------------------------------------------------------+
//|xmanid                              |pregnant                                                       |
//+------------------------------------+---------------------------------------------------------------+
//|e2228195-cf7a-4d7f-bf5d-3252303f4f87|[{"name":"孕产妇","startTime":"2017-05-04 11:41:04.0","type":"2"}]|
//+------------------------------------+---------------------------------------------------------------+