package com.antin.extend.analyzer.guide

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.input.HbaseResult
import com.antin.extend.util.{Md5, StringUtil, ConfigHelper}
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{SingleColumnValueFilter, RegexStringComparator, FilterList}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.types.{StructType, StringType, StructField}
import org.apache.spark.sql.{DataFrame, Row, Dataset, SparkSession}

/**
  * Created by jichangjin on 2017/10/17.
  * 检验
  */
class CheckoutAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, Row](ss, ymlpath) {
  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"hadoop", "name":"sehr_xman_ehr_guide"},
        |"rowkey":"rowKey",
        |"columns": {
        |"rowKey":{"cf":"rowkey", "col":"rowKey","type":"string"},
        |"xmanId":{"cf":"c", "col":"XMAN_ID", "type": "string"},
        |"event":{"cf":"c", "col":"EVENT", "type": "string"},
        |"catalogCode":{"cf":"c", "col":"CATALOG_CODE", "type": "string"},
        |"serial":{"cf":"e", "col":"SERIAL", "type": "string"},
        |"orgCode":{"cf":"c", "col":"ORG_CODE", "type": "string"},
        |"classCode":{"cf":"c", "col":"CLASS_CODE", "type": "string"},
        |"code":{"cf":"c", "col":"CODE", "type": "string"},
        |"subCode":{"cf":"c", "col":"SUB_CODE", "type": "string"},
        |"subName":{"cf":"c", "col":"SUB_NAME", "type": "string"},
        |"checkoutCode":{"cf":"c", "col":"CHECKOUT_CODE", "type": "string"},
        |"checkoutName":{"cf":"c", "col":"CHECKOUT_NAME", "type": "string"},
        |"time":{"cf":"c", "col":"TIME", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[Row] = {
    val filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL)
    val comp1 = new RegexStringComparator("2") //任意以2打头的值
    val versionFilter = new SingleColumnValueFilter(
        Bytes.toBytes("i"),
        Bytes.toBytes("VERSION"),
        CompareOp.EQUAL,
        comp1
      )
    val comp2 = new RegexStringComparator("^(0121)|(0221)$")
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

    //import ss.implicits._
    val dt = _input.dataframe(scan, {
      (kv: (String, HbaseResult)) => {
//        val catalogCode = Bytes.toString(kv._2("i")("CATALOG_CODE"))
//        val version = Bytes.toString(kv._2("i")("VERSION"))
        val isXML = kv._2("i").get("XML") match {
          case Some(v) => StringUtil.isXML(Bytes.toString(v))
          case None => false
        }
        //(catalogCode == "0121" || catalogCode == "0221") && isXML //检验
        //("0121".equals(catalogCode) || "0221".equals(catalogCode)) && ("2.0.0.0".equals(version) || "2.0.0.1".equals(version)) && isXML
        isXML
      }
    })
    val map = ConfigHelper.loadProperties("/properties/checkout.properties")

    val broadcastVar = ss.sparkContext.broadcast(map)

    //结果集
    val distValue = dt.rdd.map(m => {
      //val rowkey = m.getAs[String]("_key")
      val xmanId = m.getAs[String]("xmanId")
      val event = m.getAs[String]("event")
      val catalogCode = m.getAs[String]("catalogCode")
      val serial = m.getAs[String]("serial")

      val orgCode = m.getAs[String]("orgCode")
      val classCode = m.getAs[String]("classCode")
      val code = m.getAs[String]("code")
      val time = m.getAs[String]("time")

      val subCodes = m.getAs[Seq[String]]("subCode")
      val subNames = m.getAs[Seq[String]]("subName")

      val broadMap = broadcastVar.value
      val value = broadMap.map(b => {
        val tuple = b._2.filter(f => classCode.contains(f)).map(f => {
          //市民唯一标识，事件唯一标识，ehr类型，编号，标本类型，检验的专业类型编码，检验类别编码(excel)，检验名称关键字(excel)  子项的LOINC编码  子项的LOINC名称
          // xman_id,     event, catalog_code,serial,classCode,code,     checkoutCode,checkoutName
          // (rowkey, classCode, code, b._1, f)
          (xmanId, event, catalogCode, serial, orgCode, classCode, code, b._1, f, subCodes, subNames, time)
        })
        tuple
      }) /*flatMap(f=>f).*/ .filter(f => f.nonEmpty).map(m => m.head)
      value
    }).filter(f => f.nonEmpty)

    val exclued = distValue.map(m => {
      if (m.size >= 2) {
        val arr = m.filter(x => x._4 != "2") //过滤掉血中含有血清项
        arr
      } else {
        m
      }
    })

    val schemaString = "rowKey,xmanId,event,catalogCode,serial,orgCode,classCode,code,checkoutCode,checkoutName,subCode,subName,time"

    // Generate the schema based on the string of schema
    val fields = schemaString.split(",")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)

    val rowRDD = exclued.filter(_.nonEmpty).map(m => {
      //rowKey保持与药品一致（+0）
      val attributes = m.head
      // Row(Md5.generateUUID(attributes._1 + attributes._2 + attributes._3 + attributes._4) + 0, attributes._1, attributes._2, attributes._3, attributes._4, attributes._5, attributes._6, attributes._7, attributes._8)
      //Row(attributes._1 + attributes._2 + attributes._3 + attributes._4 + 0, attributes._1, attributes._2, attributes._3, attributes._4, attributes._5, attributes._6, attributes._7, attributes._8, attributes._9, attributes._10, attributes._11)
      attributes._10.indices.map(index => Row(attributes._1 + attributes._2 + attributes._3 + attributes._4 + index, attributes._1, attributes._2, attributes._3, attributes._4, attributes._5, attributes._6, attributes._7, attributes._8, attributes._9, attributes._10(index), attributes._11(index), attributes._12))

    }).flatMap(f => f)
    // Apply the schema to the RDD
    val checkDF = ss.createDataFrame(rowRDD, schema)
    checkDF
  }

  //  override def writeDataset(dt: DataFrame): Unit = {
  //    dt.show(false)
  //    //import ss.implicits._
  //    //    checkDF.write.options(Map(HBaseTableCatalog.tableCatalog -> getCatalog, HBaseTableCatalog.newTable -> "5")) //你需要额外传递给驱动的参数
  //    //      .format("org.apache.spark.sql.execution.datasources.hbase")
  //    //      .save
  //
  //  }

}
