package com.antin.extend.analyzer.tags

import java.util

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.model.DiabetesModel
import com.antin.extend.util.JsonHelper
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.collection.JavaConverters._

/**
  * Created by jichangjin on 2017/9/19.
  * 糖尿病
  */
class DiabetesAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, DiabetesModel](ss, ymlpath) {
  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"hadoop", "name":"ehr_r"},
        |"rowkey":"xmanId",
        |"columns": {
        |"xmanId":{"cf":"rowkey", "col":"xmanId","type":"string"},
        |"diabetes":{"cf":"h", "col":"DIABETES", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[DiabetesModel] = {
    import ss.implicits._
    val dt = _input.dataframe
    dt.createOrReplaceTempView("sehr_chronic_register_rpt")
    val resultDF = ss.sql("select xmanId,category,registrationDate,lasttime from sehr_chronic_register_rpt where xmanId is not null  and xmanId <> '' and  category='糖尿病' and idNo is not null and idNo <> ''")
      .toDF("id", "name", "startTime", "endTime")

    val resultModel = resultDF.rdd.groupBy(_ (0)).map(m => {
      val maps = m._2.map(r => {
        val map: java.util.Map[String, String] = new util.HashMap[String, String]
        //val map= new mutable.HashMap[String, String]
        map.put("name", r(1).toString)
        map.put("type", "1") //糖尿病(1)//高血压(0)
        map.put("parentCode", "chronic")
        map.put("startTime", r(2).toString)
        map.put("endTime", r(3).toString)
        map.put("code", "E14.900")
        map
      })
      DiabetesModel(m._1.toString, JsonHelper.obj2JsonString(maps.asJava))
    })
    resultModel.toDS
  }


//    override def writeDataset(dt: Dataset[DiabetesModel]): Unit = {
//      //dt.show(false)
//    }
}
//put 'hpor:ehr_r','c4eb24e1-d297-4b8b-9ba1-d1d4dd4898f7',"health:diabetes","[{'name':'糖尿病','type':'1','parentCode':'chronic','startTime':'2017-01-01','endTime':'2017-11-15'}]"
//put 'hpor:ehr_r','c4eb24e1-d297-4b8b-9ba1-d1d4dd4898f7',"health:diabetes","[{'name':'高血压','type':'0','parentCode':'chronic','startTime':'2017-01-01','endTime':'2017-11-15'}]"

//269条
//select count(*)
//from (select t1.id, t1.id_no
//from sehr_xman t1, sehr_chronic_register_rpt t2
//where t1.id is not null
//and t1.id_no is not null
//and t1.id_no = t2.id_no)

