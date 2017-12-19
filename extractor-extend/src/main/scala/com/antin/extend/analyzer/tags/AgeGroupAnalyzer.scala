package com.antin.extend.analyzer.tags

import java.text.SimpleDateFormat
import java.util

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.input.HbaseResult
import com.antin.extend.model.{BaseInfoModel, AgeGroupModel}
import com.antin.extend.util.{StringUtil, DateHelper, JsonHelper}
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{SingleColumnValueFilter, RegexStringComparator, FilterList}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Created by jichangjin on 2017/9/19.
  * 年龄段
  */
class AgeGroupAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, AgeGroupModel](ss, ymlpath) {
  override def getCatalog: String =
    s"""{
        |"table":{"namespace":"hadoop", "name":"ehr_r"},
        |"rowkey":"xmanId",
        |"columns": {
        |"xmanId":{"cf":"rowkey", "col":"xmanId","type":"string"},
        |"ageGroup":{"cf":"h", "col":"AGE_GROUP", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[AgeGroupModel] = {
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
    val resultDS = _input.dataframe(scan, {
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
    import ss.implicits._
    // resultDS.printSchema()
    //resultDS.select($"xmanid", $"birthDate").show(false)
    val resultDF = resultDS.select($"xmanId", $"birthDate")
    // import ss.implicits._
    // val dt = _input.dataframe
    //dt.createOrReplaceTempView("sehr_xman")
    // val resultDF = ss.sql("select id,birthday from sehr_xman where birthday is not null")
    //import ss.implicits._
    val resultModel = resultDF.rdd.map(p => {
      try {
        //每个人
        val sdf = new SimpleDateFormat("yyyy-MM-dd")
        // val sdf2 = new SimpleDateFormat("yyyy/MM/dd")
        val dateString = p(1).toString.split(" ")(0)

        val pattern1 = "^\\d{4}-\\d{1,2}-\\d{1,2}$"
        val pattern2 = "^\\d{4}/\\d{1,2}/\\d{1,2}$"
        val emailRegex1 = pattern1.r
        val emailRegex2 = pattern2.r

        val stdDateString = if (emailRegex1.pattern.matcher(dateString).matches)
          dateString
        else if (emailRegex2.pattern.matcher(dateString).matches)
          dateString.replaceAll("/", "-")
        else
          throw new RuntimeException(s"$dateString  时间格式不匹配....")

        val birthday = sdf.parse(stdDateString)
        val age = DateHelper.getAge(birthday)

        val sixDate = DateHelper.devYear(birthday, 6)
        val seventeenDate = DateHelper.devYear(birthday, 17)
        val fortyDate = DateHelper.devYear(birthday, 40)
        val sixtyFiveDate = DateHelper.devYear(birthday, 65)

        var ageGroup = 0
        if (0 <= age && age <= 6)
          ageGroup = 0
        else if (7 <= age && age <= 17)
          ageGroup = 1
        else if (18 <= age && age <= 40)
          ageGroup = 2
        else if (41 <= age && age <= 65)
          ageGroup = 3
        else if (65 < age)
          ageGroup = 4

        val list: java.util.ArrayList[util.Map[String, String]] = new util.ArrayList()

        while (ageGroup >= 0) {
          val map: java.util.Map[String, String] = new util.HashMap[String, String]
          map.put("parentCode", null)
          if (ageGroup == 0) {
            map.put("name", "儿童")
            map.put("type", "4")
            map.put("startTime", stdDateString)
            map.put("endTime", sdf.format(sixDate))
            list.add(map)
          } else if (ageGroup == 1) {
            map.put("name", "少年")
            map.put("type", "5")
            map.put("startTime", sdf.format(DateHelper.plusDate(sixDate, 1)))
            map.put("endTime", sdf.format(seventeenDate))
            list.add(map)
          } else if (ageGroup == 2) {
            map.put("name", "青年")
            map.put("type", "6")
            map.put("startTime", sdf.format(DateHelper.plusDate(seventeenDate, 1)))
            map.put("endTime", sdf.format(fortyDate))
            list.add(map)
          } else if (ageGroup == 3) {
            map.put("name", "中年")
            map.put("type", "7")
            map.put("startTime", sdf.format(DateHelper.plusDate(fortyDate, 1)))
            map.put("endTime", sdf.format(sixtyFiveDate))
            list.add(map)
          } else if (ageGroup == 4) {
            map.put("name", "老年")
            map.put("type", "8")
            map.put("startTime", sdf.format(DateHelper.plusDate(sixtyFiveDate, 1)))
            map.put("endTime", null)
            list.add(map)
          }
          ageGroup -= 1
        }
        (p(0).toString, JsonHelper.obj2JsonString(list))
        AgeGroupModel(p(0).toString, JsonHelper.obj2JsonString(list))
      } catch {
        case e: Exception => {
          e.printStackTrace()
          null
        }
      }
    }).filter(f => f != null) //.map(m=> AgeGroupModel(m._1,m._2)

   // resultModel.toDF().show(false)
    resultModel.toDS
  }

//  override def writeDataset(dt: Dataset[AgeGroupModel]): Unit = {
//    // dt.show(false)
//    dt.take(1000).foreach(println)
//  }
}
