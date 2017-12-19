package com.antin.extend.analyzer.test.test2

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.analyzer.test.test1.PregnancySmjkindexAnalyzer
import com.antin.extend.input.HbaseResult
import com.antin.extend.model._
import com.antin.extend.util.StringUtil
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{MultipleColumnPrefixFilter, FilterList, RegexStringComparator, SingleColumnValueFilter}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.slf4j.{LoggerFactory, Logger}

import scala.collection.mutable.ListBuffer

/**
  * Created by jichangjin on 2017/9/18.
  * 抽取 用药 数据
  */
class TestAnalyzer2(ss: SparkSession, ymlpath: String) extends Analyzer[Product, TestOutModel](ss, ymlpath) {
  private val log: Logger = LoggerFactory.getLogger(classOf[TestAnalyzer2])

  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"test", "name":"jcj_temp_p"},
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
        |"code":{"cf":"i", "col":"CODE", "type": "string"},
        |"classCode":{"cf":"i", "col":"CLASS_CODE", "type": "string"},
        |"observationCode":{"cf":"i", "col":"OBSERVATION_CODE", "type": "string"},
        |"observationDisplayName":{"cf":"i", "col":"OBSERVATION_DISPLAY_NAME", "type": "string"},
        |"observationValue":{"cf":"i", "col":"OBSERVATION_VALUE", "type": "string"},
        |"observationUnit":{"cf":"i", "col":"OBSERVATION_UNIT", "type": "string"},
        |"observationNotes":{"cf":"i", "col":"OBSERVATION_NOTES", "type": "string"},
        |"observationInterpretationCode":{"cf":"i", "col":"OBSERVATION_INTERPRETATION_CODE", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[TestOutModel] = {

    import ss.implicits._
    val filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL)

    val comp1 = new RegexStringComparator("2") //任意以2打头的值
    //
    val versionFilter = new SingleColumnValueFilter(
        Bytes.toBytes("i"),
        Bytes.toBytes("VERSION"),
        CompareOp.EQUAL,
        //Bytes.toBytes("2.0.0.0")
        comp1
      )

    //val comp2 = new RegexStringComparator("^0[1|2]21$")
    val comp2 = new RegexStringComparator("^(0121)|(0221)$")
    val catalogCodeFilter1 = new SingleColumnValueFilter(
      Bytes.toBytes("i"),
      Bytes.toBytes("CATALOG_CODE"),
      CompareOp.EQUAL,
      // Bytes.toBytes("0121")
      comp2
    )
    filterList.addFilter(versionFilter)
    filterList.addFilter(catalogCodeFilter1)
    val scan = new Scan
    scan.setFilter(filterList)

    // val qualifiers = Array(Bytes.toBytes("i:XMAN_ID"), Bytes.toBytes("i:EVENT"),
    // Bytes.toBytes("i:CATALOG_CODE"), Bytes.toBytes("i:SERIAL"), Bytes.toBytes("i:XML"), Bytes.toBytes("i:COMMIT_TIME"))

    val resultSet = _input.dataframe(scan, {
      (kv: (String, HbaseResult)) => {
        val isXML = kv._2("i").get("XML") match {
          case Some(v) => StringUtil.isXML(Bytes.toString(v))
          case None => false
        }
        isXML
      }
    })
    resultSet.createTempView("sehr_xman_ehr_0121_0221")

    //    val pregnancyIndex = new PregnancySmjkindexAnalyzer(ss, "/yml/hbase/pregnancy_smjkindex.yml")
    //    val pregnancyIndexDF = pregnancyIndex.processDataset

    //   pregnancyIndexDF.createTempView("temp_tys_pregnancy_smjkindex")

    // val resultDF = ss.sql("select t1.* from sehr_xman_ehr_0121_0221 t1,temp_tys_pregnancy_smjkindex t2 where t1.xmanId =t2._key")
    val resultDF = ss.sql("select t1.* from sehr_xman_ehr_0121_0221 t1")
    resultDF.rdd.map(x => {
      val observationCode = x.getAs[Seq[String]]("observationCode")
      val list = new ListBuffer[TestOutModel]()
      try {
        for (i <- observationCode.indices) {
          list += TestOutModel(x.getAs[String]("_key") + "_" + i,
            x.getAs[String]("xmanId"),
            x.getAs[String]("event"),
            x.getAs[String]("catalogCode"),
            x.getAs[String]("serial"),
            x.getAs[String]("commitTime"),
            x.getAs[String]("orgCode"),
            x.getAs[String]("title"),
            x.getAs[String]("code"),
            x.getAs[String]("classCode"),
            observationCode(i),
            x.getAs[Seq[String]]("observationCode")(i),
            x.getAs[Seq[String]]("observationDisplayName")(i),
            x.getAs[Seq[String]]("observationUnit")(i),
            x.getAs[Seq[String]]("observationNotes")(i),
            x.getAs[Seq[String]]("observationInterpretationCode")(i)
          )
        }
      } catch {
        case e: Exception => log.warn("unexpect xml,rowkey is => " + x.getAs[String]("_key"))
      }
      list
    }).flatMap(f => f).toDS()
  }

  override def writeDataset(dt: Dataset[TestOutModel]): Unit = {
    dt.show(100000)
  }

}
