package com.antin.base.analyzer

import com.antin.base.input.{HbaseResult, InputFactory, InputConfig}
import com.antin.base.model.{RFund, Fund}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql
import org.apache.spark.sql.{Dataset, SparkSession}



class FundAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Fund, RFund](ss, ymlpath) {

  def getCatalog:String =  s"""{
                              |"table":{"namespace":"default", "name":"EHR_R"},
                              |"rowkey":"xmanid",
                              |"columns": {
                              |"xmanid":{"cf":"rowkey", "col":"xmanid","type":"string"},
                              |"payway":{"cf":"r", "col":"payway", "type": "string"}
                              |}
                              |}
        """.stripMargin

   def processDataset: Dataset[RFund] = {
     import ss.implicits._
     val d = _input
         .dataset[Fund](
       (kv:(String, HbaseResult)) => {
         Bytes.toString(kv._2("i")("CATALOG_CODE")) == "0102"
       }

     ).filter { (f: Fund) =>
       !f.codes.forall {
         _ == ""
       }
     }.map { f =>
       Fund(f.xmanid, f.codes.filter(_ != ""))
     }.groupByKey(x => x.xmanid).mapGroups {
       case (key: String, vs: Iterator[Fund]) => {
         val flatten_codes: Seq[String] = vs.map { v => v.codes }.flatten.toSeq
         val codes_number = flatten_codes.groupBy(identity).mapValues {
           _.size
         }
         val size = flatten_codes.size.toFloat
         val target_codes = codes_number.filter {
           _._2.toFloat / size > 0.5
         }.map{_._1}
         RFund(key, target_codes.mkString(","))
       }
     }
     d
   }



}

