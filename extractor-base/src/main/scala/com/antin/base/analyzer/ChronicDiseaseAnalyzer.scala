package com.antin.base.analyzer

import com.antin.base.input.HbaseResult
import com.antin.base.model.{Fund, ChronicDisease, RChronicDisease}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Created by Administrator on 2017/6/27.
  */
class ChronicDiseaseAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[ChronicDisease, RChronicDisease](ss, ymlpath) {

  def getCatalog: String =
    s"""{
        |"table":{"namespace":"default", "name":"EHR_R"},
        |"rowkey":"xmanid",
        |"columns": {
        |"xmanid":{"cf":"rowkey", "col":"xmanid","type":"string"},
        |"chronic":{"cf":"r", "col":"chronic", "type": "string"},
        |"compliace":{"cf":"r","col":"compliace","type":"boolean"}
        |}
        |}
        """.stripMargin


  def processDataset: Dataset[RChronicDisease] = {
    import ss.implicits._
    val TG_DISEASE_CODE = Seq("I10", "E10-E14", "G40", "M10")

    val d = _input.dataset[ChronicDisease] {
      (kv: (String, HbaseResult)) =>
        Bytes.toString(kv._2("i")("CATALOG_CODE")) == "0101"
    }.filter {
      _.idc_codes.exists {
        c => TG_DISEASE_CODE.exists(c.contains(_))
      }
    }.groupByKey(_.xmanid)
      .mapGroups {
        case (xmanid: String, vs: Iterator[ChronicDisease]) => {
          val result_disease = vs.map {
            _.idc_codes
          }.flatten.filter(c => TG_DISEASE_CODE.exists(c.contains(_))).toSet
          RChronicDisease(xmanid, result_disease.mkString(","), result_disease.nonEmpty)
        }
      }
    d
  }

}
