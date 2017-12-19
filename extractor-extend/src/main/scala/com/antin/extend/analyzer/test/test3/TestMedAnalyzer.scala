package com.antin.extend.analyzer.test.test3

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.input.HbaseResult
import com.antin.extend.model._
import com.antin.extend.util.StringUtil
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{FilterList, RegexStringComparator, SingleColumnValueFilter}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ListBuffer

/**
  * Created by jichangjin on 2017/9/18.
  * 抽取 用药 数据
  */
class TestMedAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, TestMedOutModel](ss, ymlpath) {
  private val log: Logger = LoggerFactory.getLogger(classOf[TestMedAnalyzer])

  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"test", "name":"jcj_temp_med2"},
        |"rowkey":"key",
        |"columns": {
        |"key":{"cf":"rowkey", "col":"key","type":"string"},
        |"xmanId":{"cf":"i", "col":"XMAN_ID", "type": "string"},
        |"event":{"cf":"i", "col":"EVENT", "type": "string"},
        |"catalogCode":{"cf":"i", "col":"CATALOG_CODE", "type": "string"},
        |"serial":{"cf":"i", "col":"SERIAL", "type": "string"},
        |"commitTime":{"cf":"i", "col":"COMMIT_TIME", "type": "string"},
        |"orgCode":{"cf":"i", "col":"ORG_CODE", "type": "string"},
        |"title":{"cf":"i", "col":"TITLE", "type": "string"},
        |"customeCode":{"cf":"i", "col":"CUSTOME_CODE", "type": "string"},
        |"custome":{"cf":"i", "col":"CUSTOME", "type": "string"},
        |"spec":{"cf":"i", "col":"SPEC", "type": "string"},
        |"totalUnit":{"cf":"i", "col":"TOTAL_UNIT", "type": "string"},
        |"totalQuantity":{"cf":"i", "col":"TOTAL_QUANTITY", "type": "string"},
        |"doseUnit":{"cf":"i", "col":"DOSE_UNIT", "type": "string"},
        |"doseQuantity":{"cf":"i", "col":"DOSE_QUANTITY", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[TestMedOutModel] = {
    val filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL)
    val comp1 = new RegexStringComparator("2") //任意以2打头的值
    val versionFilter = new SingleColumnValueFilter(
        Bytes.toBytes("i"),
        Bytes.toBytes("VERSION"),
        CompareOp.EQUAL,
        comp1
      )
    val comp2 = new RegexStringComparator("^(0141)|(0241)$")
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
    val resultSet = _input.dataframe(scan, {
      (kv: (String, HbaseResult)) => {
        // val catalogCode = Bytes.toString(kv._2("i")("CATALOG_CODE"))
        // val version = Bytes.toString(kv._2("i")("VERSION"))
        val isXML = kv._2("i").get("XML") match {
          case Some(v) => StringUtil.isXML(Bytes.toString(v))
          case None => false
        }
        // ("0141".equals(catalogCode) || "0241".equals(catalogCode)) && version.startsWith("2") && isXML
        isXML
      }
    })
    //resultSet.repartition(1000)
   // resultSet.createTempView("sehr_xman_ehr_0141_0241")

    val pregnancyIndex = new PregnancySmjkindexAnalyzer(ss, "/yml/hbase/pregnancy_smjkindex.yml")
    val pregnancyIndexDF = pregnancyIndex.processDataset
    val broadcastVar = ss.sparkContext.broadcast(pregnancyIndexDF.rdd.map(x => x.getAs[String]("_key")).collect())

    //pregnancyIndexDF.repartition(7)
   // pregnancyIndexDF.createTempView("temp_tys_pregnancy_smjkindex")

  //  val resultDF = ss.sql("select t1.* from sehr_xman_ehr_0141_0241 t1,temp_tys_pregnancy_smjkindex t2 where t1.xmanId =t2._key")
  val resultRDD = resultSet.rdd.filter(x => {
    val targetKeys = broadcastVar.value
    targetKeys.contains(x.getAs[String]("xmanId"))
  })

    resultRDD.map(x => {
      val customeCode = x.getAs[Seq[String]]("customeCode")
      val list = new ListBuffer[TestMedOutModel]()
      for (i <- customeCode.indices) {
        try {
          list += TestMedOutModel(x.getAs[String]("_key") + "_" + i,
            x.getAs[String]("xmanId"),
            x.getAs[String]("event"),
            x.getAs[String]("catalogCode"),
            x.getAs[String]("serial"),
            x.getAs[String]("commitTime"),
            x.getAs[String]("orgCode"),
            x.getAs[String]("title"),
            customeCode(i),
            x.getAs[Seq[String]]("custome")(i),
            x.getAs[Seq[String]]("spec")(i),
            x.getAs[Seq[String]]("totalUnit")(i),
            x.getAs[Seq[String]]("totalQuantity")(i),
            x.getAs[Seq[String]]("doseUnit")(i),
            x.getAs[Seq[String]]("doseQuantity")(i)
          )
        } catch {
          case e: Exception => log.warn("unexpect xml,rowkey is => " + x.getAs[String]("_key"))
        }

      }
      list
    }).flatMap(f => f).toDS()
  }

  //    override def writeDataset(dt: Dataset[TestOutModel]): Unit = {
  //      dt.show(false)
  //    }

}
