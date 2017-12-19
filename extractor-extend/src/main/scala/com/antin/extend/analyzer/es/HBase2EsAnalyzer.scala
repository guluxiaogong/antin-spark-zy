package com.antin.extend.analyzer.es

import com.antin.extend.analyzer.Analyzer
import com.antin.extend.util.{CollectionConverters, JsonHelper}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}


/**
  * Created by Administrator on 2017-11-22.
  */
class HBase2EsAnalyzer(ss: SparkSession, ymlpath: String,esIndex:String) extends Analyzer[Product, Row](ss, ymlpath) {

  def processDataset: DataFrame = {
    _input.dataframe

  }

  override def writeDataset(df: DataFrame): Unit = {
    import org.elasticsearch.spark._
    //    val mapRdd = df.rdd.map(row => {
    //      val map = new mutable.HashMap[String, Any]
    //      for (i <- (0 until row.size))
    //        map ++= row.getMap(i)
    //      map
    //    })

    //df.show(false)

        import org.elasticsearch.spark._
        val mapRdd = df.toJSON.rdd.map(json => {
          CollectionConverters.recursivelyToScala(JsonHelper.json2Map(json))
        })
        mapRdd.saveToEs(esIndex)
  }
}
