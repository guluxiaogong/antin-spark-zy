package com.antin.extend.analyzer.guide

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.input.HbaseResult
import com.antin.extend.model._
import com.antin.extend.util.{Md5, StringUtil}
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{SingleColumnValueFilter, RegexStringComparator, FilterList}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.{Encoder, Dataset, SparkSession}
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.Encoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by jichangjin on 2017/9/18.
  * 抽取 用药 数据
  */
class PyhsicAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Pyhsic, Pyhsic](ss, ymlpath) {
  //val log = LoggerFactory.getLogger(classOf[PyhsicAnalyzer])

  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"hadoop", "name":"sehr_xman_ehr_guide"},
        |"rowkey":"rowKey",
        |"columns": {
        |"rowKey":{"cf":"rowkey", "col":"rowKey","type":"string"},
        |"xmanId":{"cf":"p", "col":"XMAN_ID", "type": "string"},
        |"event":{"cf":"p", "col":"EVENT", "type": "string"},
        |"catalogCode":{"cf":"p", "col":"CATALOG_CODE", "type": "string"},
        |"serial":{"cf":"p", "col":"SERIAL", "type": "string"},
        |"orgCode":{"cf":"p", "col":"ORG_CODE", "type": "string"},
        |"startDate":{"cf":"p", "col":"START_DATE", "type": "string"},
        |"freq":{"cf":"p", "col":"FREQ", "type": "string"},
        |"days":{"cf":"p", "col":"DAYS", "type": "string"},
        |"code":{"cf":"p", "col":"CODE", "type": "string"},
        |"name":{"cf":"p", "col":"NAME", "type": "string"},
        |"customeCode":{"cf":"p", "col":"CUSTOME_CODE", "type": "string"},
        |"customeName":{"cf":"p", "col":"CUSTOME_NAME", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[Pyhsic] = {
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
    //import ss.implicits._
    import ss.implicits._
    _input.dataset[Pyhsic](scan, {
      (kv: (String, HbaseResult)) => {
        //        val catalogCode = Bytes.toString(kv._2("i")("CATALOG_CODE"))
        //        val version = Bytes.toString(kv._2("i")("VERSION"))
        // val xmanid = kv._2("i").getOrElse("XMAN_ID", null)

        val isXML = kv._2("i").get("XML") match {
          case Some(v) => StringUtil.isXML(Bytes.toString(v))
          case None => false
        }

        // val xml = kv._2("i").getOrElse("XML", null)
        //        if (xml != null && !StringUtil.isXML(Bytes.toString(xml)))
        //          println(Bytes.toString(xmanid) + "====================================================" + StringUtil.isXML(Bytes.toString(xml)))
        //(catalogCode == "0141" | catalogCode == "0241") && xmanid != null && Bytes.toString(xmanid) != "" && xml != null && StringUtil.isXML(Bytes.toString(xml))
        //(catalogCode == "0141" | catalogCode == "0241") && xml != null && StringUtil.isXML(Bytes.toString(xml))
        //(catalogCode == "0141" || catalogCode == "0241")&&(version == "2.0.0.0" || version == "2.0.0.1") && isXML
        //("0141".equals(catalogCode) || "0241".equals(catalogCode)) && ("2.0.0.0".equals(version) || "2.0.0.1".equals(version)) && isXML
        isXML
      }
    }).map(x => {
      Pyhsic(x.xmanId, x.event, x.catalogCode, x.serial, x.orgCode, x.startDate, x.freq, x.days, x.code, x.name, x.customeCode, x.customeName)
    })
  }

  override def writeDataset(dt: Dataset[Pyhsic]): Unit = {
    //dt.show(false)
    //dt.repartition(1).toDF().write.json("hdfs://hadoop-cluster/user/jcj/check_test/pyhsic.json")
    //dt.repartition(1).rdd.saveAsTextFile("hdfs://hadoop-cluster/user/jcj/pyhsic/pyhsic.txt")
    // import ss.implicits._
    //    implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Pyhsic]
    //    // Primitive types and case classes can be also defined as
    //    implicit val stringIntMapEncoder: Encoder[Pyhsic] = ExpressionEncoder()

    val rdd = dt.rdd.map(x => {

      val freqMap = mutable.Map[String, Double](
        "qd" -> 1,
        "bid" -> 2,
        "tid" -> 3,
        "qid" -> 1,
        "qod" -> 1 / 2,
        "qw" -> 1 / 7,
        "biw" -> 2 / 7,
        "tiw" -> 3 / 7,
        "qow" -> 1 / 14,
        "2w" -> 1 / 14,
        "3w" -> 1 / 21,
        "4w" -> 1 / 28,
        "q1/2h" -> 48,
        "qh" -> 24,
        "q2h" -> 12,
        "q3h" -> 8,
        "q4h" -> 6,
        "q6h" -> 4,
        "q8h" -> 3,
        "q12h" -> 2,
        "st" -> 9999,
        "qn" -> 1,
        "once" -> 1,
        "2/日" -> 2,
        "3/日" -> 3,
        "晚餐前" -> 1,
        "早餐前" -> 1,
        "午餐前" -> 1
      )
      val list = new ListBuffer[OutPyhsic2HBase]()
      for (i <- x.startDate.indices) {
        try {
          //list += OutPyhsic(x.xmanid, x.event, x.catalogCode, x.serial, x.startDate(i), x.code(i), x.name(i), x.customeCode(i), x.customeName(i))
          //TODO
          // list += OutPyhsic2HBase(Md5.generateUUID(x.xmanId + x.event + x.catalogCode + x.serial) + i, x.xmanId, x.event, x.catalogCode, x.serial, x.startDate(i), x.code(i), x.name(i), x.customeCode(i), x.customeName(i))
          list += OutPyhsic2HBase(x.xmanId + x.event + x.catalogCode + x.serial + i, x.xmanId, x.event, x.catalogCode, x.serial, x.orgCode, x.startDate(i), freqMap.getOrElse(x.freq(i).replaceAll("\\.", "").trim, x.freq(i)).toString, x.days(i), x.code(i), x.name(i), x.customeCode(i), x.customeName(i))

          // list += OutPyhsic2HBase(Md5.generateUUID(x.xmanid + x.event + x.catalogCode + x.serial), x.startDate(i), x.code(i), x.name(i), x.customeCode(i), x.customeName(i))
        } catch {
          case e: Exception => {
            //log.error(x.xmanid + "_" + x.event + "_" + x.catalogCode + "_" + x.serial + System.lineSeparator() + e.getStackTrace)
            e.printStackTrace()
            //list += OutPyhsic(x.xmanid, x.event, x.catalogCode, x.serial, "", "", "", "", "")
            //// list += OutPyhsic2HBase(Md5.generateUUID(x.xmanId + x.event + x.catalogCode + x.serial) + i, x.xmanId, x.event, x.catalogCode, x.serial, "", "", "", "", "", "", "", "")

            //list += OutPyhsic2HBase(Md5.generateUUID(x.xmanid + x.event + x.catalogCode + x.serial), "", "", "", "", "")
          }
        }
        //list += OutPyhsic2(x.xmanid, x.event)
      }
      list
    }).flatMap(f => f)
    //    rdd.foreach(f => {
    //      println(s"${f.xmanid}\t${f.event}\t${f.catalogCode}\t${f.serial}\t${f.startDate}\t${f.code}\t${f.name}\t${f.customeCode}\t${f.customeName}")
    //    })
    //    rdd.map(f => {
    //      s"${f.xmanid}\t${f.event}\t${f.catalogCode}\t${f.serial}\t${f.startDate}\t${f.code}\t${f.name}\t${f.customeCode}\t${f.customeName}"
    //    }).saveAsTextFile("hdfs://hadoop-cluster/user/jcj/pyhsic/")
    import ss.implicits._
    rdd.toDF.write.options(Map(HBaseTableCatalog.tableCatalog -> getCatalog, HBaseTableCatalog.newTable -> "5")) //你需要额外传递给驱动的参数
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .save
  }

}
