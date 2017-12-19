package com.antin.extend.analyzer.test.test5_1

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
class TestAnalyzer(ss: SparkSession, ymlpath: String) extends Analyzer[Product, Row](ss, ymlpath) {
  //private val log: Logger = LoggerFactory.getLogger(classOf[TestAnalyzer])

  def processDataset: DataFrame = {


    //import ss.implicits._
    val resultSet = _input.dataframe
    resultSet.createOrReplaceTempView("outPatient_diag")
    val resultDF = ss.sql("select outpat_diag_name,outpat_diag_code,count(outpat_diag_code) as counts from outPatient_diag group by outpat_diag_name,outpat_diag_code order by desc")
    resultDF
  }

  override def writeDataset(dt: Dataset[Row]): Unit = {
    dt.rdd.top(50)
  }

}
