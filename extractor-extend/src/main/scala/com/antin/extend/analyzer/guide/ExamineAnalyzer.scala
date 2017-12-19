package com.antin.extend.analyzer.guide

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.input.HbaseResult
import com.antin.extend.util.{Md5, StringUtil, ConfigHelper}
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{SingleColumnValueFilter, RegexStringComparator, FilterList}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql._
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types._

/**
  * Created by jichangjin on 2017/9/14.
  * 统计检查
  */
class ExamineAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, Row](ss, ymlpath) {
  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"hadoop", "name":"sehr_xman_ehr_guide"},
        |"rowkey":"rowKey",
        |"columns": {
        |"rowKey":{"cf":"rowkey", "col":"rowKey","type":"string"},
        |"xmanId":{"cf":"e", "col":"XMAN_ID", "type": "string"},
        |"event":{"cf":"e", "col":"EVENT", "type": "string"},
        |"catalogCode":{"cf":"e", "col":"CATALOG_CODE", "type": "string"},
        |"serial":{"cf":"e", "col":"SERIAL", "type": "string"},
        |"orgCode":{"cf":"e", "col":"ORG_CODE", "type": "string"},
        |"displayName":{"cf":"e", "col":"DISPLAY_NAME", "type": "string"},
        |"code":{"cf":"e", "col":"CODE", "type": "string"},
        |"examCode":{"cf":"e", "col":"EXAM_CODE", "type": "string"},
        |"eCode":{"cf":"e", "col":"E_CODE", "type": "string"},
        |"eName":{"cf":"e", "col":"E_NAME", "type": "string"},
        |"time":{"cf":"e", "col":"TIME", "type": "string"}
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
    val comp2 = new RegexStringComparator("^(0131)|(0231)$")
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
    _input.dataframe(scan, {
      (kv: (String, HbaseResult)) => {
        //        val catalogCode = Bytes.toString(kv._2("i")("CATALOG_CODE"))
        //        val version = Bytes.toString(kv._2("i")("VERSION"))
        val isXML = kv._2("i").get("XML") match {
          case Some(v) => StringUtil.isXML(Bytes.toString(v))
          case None => false
        }
        //(catalogCode == "0131" || catalogCode == "0231") && isXML
        //("0131".equals(catalogCode) || "0231".equals(catalogCode)) && ("2.0.0.0".equals(version) || "2.0.0.1".equals(version)) && isXML
        isXML
      }
    })
  }

  override def writeDataset(dt: DataFrame): Unit = {
    val map = ConfigHelper.loadProperties("/properties/examine.properties")

    val broadcastVar = ss.sparkContext.broadcast(map)

    val distValue = dt.rdd.map(m => {
      val xmanId = m.getAs[String]("xmanId")
      val event = m.getAs[String]("event")
      val catalogCode = m.getAs[String]("catalogCode")
      val serial = m.getAs[String]("serial")

      val orgCode = m.getAs[String]("orgCode")
      val code = m.getAs[String]("code")
      val displayName = m.getAs[String]("displayName")
      val examCode = m.getAs[String]("examCode")
      val time = m.getAs[String]("time")

      val broadMap = broadcastVar.value
      val value = broadMap.map(b => {
        val tuple = b._2.filter(f => displayName.contains(f)).map(f => {
          //市民唯一标识，事件唯一标识，ehr类型，编号，检查分类的编码，检查分类的名称，检查项目的编码，检查类别编码，检查名称关键字
          //  xman_id,    event,catalog_code,serial,displayName,   code,      examCode,     eCode,        eName
          (xmanId, event, catalogCode, serial, orgCode, displayName, code, examCode, b._1, f, time)
        })
        tuple
      }).filter(f => f.nonEmpty).map(m => m.head)
      value
    }).filter(f => f.nonEmpty)

    val schemaString = "rowKey,xmanId,event,catalogCode,serial,orgCode,displayName,code,examCode,eCode,eName,time"

    // Generate the schema based on the string of schema
    val fields = schemaString.split(",")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)

    val rowRDD = distValue.map(m => {
      //rowKey保持与药品一致
      val attributes = m.head
      Row(attributes._1 + attributes._2 + attributes._3 + attributes._4 + 0, attributes._1, attributes._2, attributes._3, attributes._4, attributes._5, attributes._6, attributes._7, attributes._8, attributes._9, attributes._10, attributes._11)
    })
    // Apply the schema to the RDD
    val examDF = ss.createDataFrame(rowRDD, schema)

    //examDF.show(false)
    // import ss.implicits._
    examDF.write.options(Map(HBaseTableCatalog.tableCatalog -> getCatalog, HBaseTableCatalog.newTable -> "5")) //你需要额外传递给驱动的参数
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .save




    //    //#######################################################
    //    distValue.take(200).foreach(println)
    //    //    ArrayBuffer((00099637-dc40-41b4-9a59-55ba665ca20d;b4d7aef0-ac7c-4314-b203-b261da1d79f7;0221;1,血清,2,血), (00099637-dc40-41b4-9a59-55ba665ca20d;b4d7aef0-ac7c-4314-b203-b261da1d79f7;0221;1,血清,3,血清))
    //    //    ArrayBuffer((00099637-dc40-41b4-9a59-55ba665ca20d;b4d7aef0-ac7c-4314-b203-b261da1d79f7;0221;10,全血,2,全血))
    //
    //    val hdfsRdd = distValue.map(m => {
    //      val rowkey = m.head._1.split(";")
    //      rowkey(0) + " || " + rowkey(1) + " || " + rowkey(2) + " || " + rowkey(3) + " || " + m.head._2 + " || " + m.head._3 + " || " + m.head._4 + " || " + m.head._5 + " || " + m.head._6
    //    })
    //    hdfsRdd.repartition(1).saveAsTextFile("hdfs://hadoop-cluster/user/jcj/examine_check/examine")
    //#######################################################

  }

  //  def writeToHDFS(distValue: RDD[scala.collection.mutable.Iterable[(String, String, String, String, String, String)]]): Unit = {
  //    //
  //    distValue.take(200).foreach(println)
  //    //    ArrayBuffer((00099637-dc40-41b4-9a59-55ba665ca20d;b4d7aef0-ac7c-4314-b203-b261da1d79f7;0221;1,血清,2,血), (00099637-dc40-41b4-9a59-55ba665ca20d;b4d7aef0-ac7c-4314-b203-b261da1d79f7;0221;1,血清,3,血清))
  //    //    ArrayBuffer((00099637-dc40-41b4-9a59-55ba665ca20d;b4d7aef0-ac7c-4314-b203-b261da1d79f7;0221;10,全血,2,全血))
  //
  //    val hdfsRdd = distValue.map(m => {
  //      val rowkey = m.head._1.split(";")
  //      rowkey(0) + " || " + rowkey(1) + " || " + rowkey(2) + " || " + rowkey(3) + " || " + m.head._2 + " || " + m.head._3 + " || " + m.head._4 + " || " + m.head._5 + " || " + m.head._6
  //    })
  //    hdfsRdd.repartition(1).saveAsTextFile("hdfs://hadoop-cluster/user/jcj/examine_check/examine")
  //  }
}
