package com.antin.parser2.source

import com.antin.parser2.input.{HBaseInputConfig, InputConfig, OracleInputConfig}
import com.antin.parser2.util.CollectionConverters
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.yaml.snakeyaml.Yaml

/**
  * Created by Administrator on 2017-11-24.
  */
class DataFromHBase(dbConfig: Map[String, Any],ymlpath: String) extends DataFrom(ymlpath) {


  def loadData(ss: SparkSession): RDD[DataResult] = {
    //链接hbase
    val hbaseConf = HBaseConfiguration.create()

//        dbConfig.get("config").
//          map(_.asInstanceOf[Map[String, String]]).
//          foreach {
//            _.foreach { case (k, v) => hbaseConf.set(k, v) }
//          }
    dbConfig .foreach {f=> hbaseConf.set(f._1.toString, f._2.toString)}
    //    hbaseConf.set(TableInputFormat.INPUT_TABLE, "student")
    //    hbaseConf.set(TableInputFormat.SCAN_COLUMN_FAMILY, "i")

    val sc = ss.sparkContext
    sc.newAPIHadoopRDD(hbaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]).
      map { case (_, result) =>
        new HBaseResult(result, isOnlyLastVersion = true) //封装返回结果信息
      }
  }

  //加载yaml文件，将信息封装成inputConfig对象
  def parserConfig(paths: String): List[InputConfig] = {
    val yml = new Yaml()
    val files = paths.split(",").map(_.trim).toList
    val nodesMaps = files.map(f => {
      val inputStream = this.getClass.getResourceAsStream(f)
      val nodesMap = CollectionConverters.recursivelyToScala(
        yml.load(inputStream).asInstanceOf[java.util.LinkedHashMap[String, Any]]
      )
      nodesMap
    })
    val inputConfigs = nodesMaps.map(f => new HBaseInputConfig(f))
    inputConfigs
  }

}
