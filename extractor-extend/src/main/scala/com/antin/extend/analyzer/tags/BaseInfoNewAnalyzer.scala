package com.antin.extend.analyzer.tags

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.input.HbaseResult
import com.antin.extend.model.BaseInfoModel
import com.antin.extend.util.StringUtil
import org.apache.commons.lang.StringUtils
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{SingleColumnValueFilter, RegexStringComparator, FilterList}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Created by jichangjin on 2017/9/19.
  */
class BaseInfoNewAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, BaseInfoModel](ss, ymlpath) {
  //shc要求的Schema
  override def getCatalog =
    s"""{
        |"table":{"namespace":"hadoop", "name":"ehr_r"},
        |"rowkey":"xmanId",
        |"columns":{
        |"xmanId":{"cf":"rowkey", "col":"xmanId", "type":"string"},
        |"name":{"cf":"p", "col":"NAME", "type":"string"},
        |"age":{"cf":"p", "col":"AGE", "type":"string"},
        |"sex":{"cf":"p", "col":"SEX", "type":"String"},
        |"weight":{"cf":"p", "col":"WEIGHT", "type":"String"},
        |"birthDate":{"cf":"p", "col":"BIRTH_DATE", "type":"string"},
        |"idNo":{"cf":"p", "col":"ID_NO", "type":"string"},
        |"address":{"cf":"p", "col":"ADDRESS", "type":"string"},
        |"marriage":{"cf":"p", "col":"MARRIAGE", "type":"string"},
        |"work":{"cf":"p", "col":"WORK", "type":"string"}
        |}
        |}""".stripMargin

  def processDataset: Dataset[BaseInfoModel] = {
    val filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL)
    val comp1 = new RegexStringComparator("2") //任意以2打头的值
    val versionFilter = new SingleColumnValueFilter(
        Bytes.toBytes("i"),
        Bytes.toBytes("VERSION"),
        CompareOp.EQUAL,
        comp1
      )
    val comp2 = new RegexStringComparator("^(0101)|(0201)$")
    val catalogCodeFilter1 = new SingleColumnValueFilter(
      Bytes.toBytes("i"),
      Bytes.toBytes("CATALOG_CODE"),
      CompareOp.EQUAL,
      comp2
    )
    filterList.addFilter(versionFilter)
    filterList.addFilter(catalogCodeFilter1)
    val scan = new Scan
    scan.setFilter(filterList)
    import ss.implicits._
    val resultDS = _input.dataset[BaseInfoModel](scan, {
      (kv: (String, HbaseResult)) => {
        //        val catalogCode = Bytes.toString(kv._2("i")("CATALOG_CODE"))
        //        val version = Bytes.toString(kv._2("i")("VERSION"))
        val isXML = kv._2("i").get("XML") match {
          case Some(v) => StringUtil.isXML(Bytes.toString(v))
          case None => false
        }
        // ("0101".equals(catalogCode)|"0201".equals(catalogCode)) && isXML
        //("0101".equals(catalogCode) || "0201".equals(catalogCode)) && ("2.0.0.0".equals(version) || "2.0.0.1".equals(version)) && isXML
        isXML
      }
    })
    val resultModel = resultDS.rdd.filter(x => x.xmanId != null).groupBy(x => x.xmanId).map(m => {

      try {
        val bim = BaseInfoModel(null, null, null, null, null, null, null, null, null, null)
        for (p <- m._2) {
          if (StringUtils.isEmpty(bim.xmanId))
            bim.xmanId = p.xmanId
          if (StringUtils.isEmpty(bim.name))
            bim.name = p.name
          if (StringUtils.isEmpty(bim.age))
            bim.age = p.age
          if (StringUtils.isEmpty(bim.sex))
            bim.sex = p.sex
          if (StringUtils.isEmpty(bim.weight))
            bim.weight = p.weight
          if (StringUtils.isEmpty(bim.birthDate)) {
            bim.birthDate = p.birthDate.trim.replaceAll("/", "-")
          }
          if (StringUtils.isEmpty(bim.idNo))
            bim.idNo = p.idNo
          if (StringUtils.isEmpty(bim.address))
            bim.address = p.address
          if (StringUtils.isEmpty(bim.marriage))
            bim.marriage = p.marriage
          if (StringUtils.isEmpty(bim.work))
            bim.work = p.work
        }
        bim
      } catch {
        case e: Exception => {
          e.printStackTrace()
          null
        }
      }
    }).filter(_ != null)

    // resultModel.toDF().write.save()
    // resultModel.saveAsTextFile("hdfs://hadoop-cluster/user/jcj/portrait/temp/baseInfo")
    resultModel.toDS
  }

  //  override def writeDataset(dt: Dataset[BaseInfoModel]): Unit = {
  //    dt.show(200, false)
  //  }
}
