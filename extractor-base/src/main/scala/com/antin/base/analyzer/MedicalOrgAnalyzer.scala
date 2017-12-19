package com.antin.base.analyzer

import com.antin.base.input.HbaseResult
import com.antin.base.model.{MedicalOrg, RMedicalOrg}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Created by Administrator on 2017/6/27.
  */
class MedicalOrgAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[MedicalOrg, RMedicalOrg](ss, ymlpath) {
  def getCatalog: String =
    s"""{
        |"table":{"namespace":"default", "name":"EHR_R"},
        |"rowkey":"xmanid",
        |"columns": {
        |"xmanid":{"cf":"rowkey", "col":"xmanid","type":"string"},
        |"orgs":{"cf":"r", "col":"orgs", "type": "string"}
        |}
        |}
        """.stripMargin

  def processDataset: Dataset[RMedicalOrg] = {
    import ss.implicits._
    _input.dataset[MedicalOrg] {
      (kv: (String, HbaseResult)) => {
        Bytes.toString(kv._2("i")("CATALOG_CODE")) == "0101"
      }
    }.groupByKey(x => x.xmanid)
      .mapGroups {
        case (xmanid, it_orgs) => {
          val orgs =it_orgs.toSeq.groupBy(identity).mapValues(_.size).toArray.sortBy(_._2)
            .take(3)
            .map(_._1.org_code)
            .mkString(",")
          RMedicalOrg(xmanid, orgs)
        }
      }
  }
}
