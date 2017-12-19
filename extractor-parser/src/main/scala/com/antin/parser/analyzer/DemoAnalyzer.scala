package com.antin.parser.analyzer

import com.antin.parser.input.HbaseResult
import com.antin.parser.util.StringUtil
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

import scala.collection.mutable

/**
  * Created by jichangjin on 2017/9/18.
  * 抽取 用药 数据
  */
class DemoAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer(ss, ymlpath) {

  def processDataset: List[(String, DataFrame)] = {
    inputs.map(input => {
      val filter = (kv: (String, HbaseResult)) => {
        kv._2("i").get("XML") match {
          case Some(v) => StringUtil.isXML(Bytes.toString(v)) //验证否为xml结构
          case None => false
        }
      }
      input.dataframe(filter,true)
    })
  }

  override def writeDataset(dts: List[(String, DataFrame)]): Unit = {
    //dt.repartition(1).toDF().write.json("hdfs://hadoop-cluster/user/jcj/check_test/pyhsic.json")
    dts.foreach(dt => {
      println("=================================")
      println()
      //      val resultDT = dt._2
      //      resultDT.schema
      //      resultDT.map(x=>{
      //        //x.isInstanceOf[mutable.Seq]
      //        x
      //      })

      dt._2.show(false)
    })
  }
}
