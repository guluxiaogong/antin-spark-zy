package com.antin.parser.db

import com.antin.parser.input.{InputResult, HbaseResult}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Scan, Result}
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{RegexStringComparator, SingleColumnValueFilter, FilterList}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableMapReduceUtil, TableInputFormat}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.slf4j.{LoggerFactory, Logger}

/**
  * Created by Administrator on 2017-11-03.
  */
class DataFromHBase extends DataFrom {
  private val log: Logger = LoggerFactory.getLogger(classOf[DataFromHBase])

  def loadData(dbMap: Map[String, Any], ss: SparkSession): RDD[InputResult] = {
    //链接hbase
    val hbaseConf = HBaseConfiguration.create()
    dbMap.get("file").foreach(f => hbaseConf.addResource(new Path(f.asInstanceOf[String])))
    dbMap.get("properties").
      map(_.asInstanceOf[Map[String, String]]).
      foreach {
        _.foreach { case (k, v) => hbaseConf.set(k, v) }
      }
    dbMap.get("family") match {
      case Some(v) => hbaseConf.set("hbase.mapreduce.scan.column.family", v.toString)
      case None => log.info(s"family is None")
    }
    hbaseConf.set("hbase.mapreduce.scan.column.family", "")
    //    val comp = new RegexStringComparator("2") //任意以2打头的值
    //    val filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL)
    //    val versionFilter = new SingleColumnValueFilter(
    //      Bytes.toBytes("i"),
    //      Bytes.toBytes("VERSION"),
    //      CompareOp.EQUAL,
    //      //Bytes.toBytes("2.0.0.0")
    //      comp
    //    )
    //    val catalogCodeFilter1 = new SingleColumnValueFilter(
    //      Bytes.toBytes("i"),
    //      Bytes.toBytes("VERSION"),
    //      CompareOp.EQUAL,
    //      Bytes.toBytes("0130")
    //    )
    //    val catalogCodeFilter2 = new SingleColumnValueFilter(
    //      Bytes.toBytes("i"),
    //      Bytes.toBytes("VERSION"),
    //      CompareOp.EQUAL,
    //      Bytes.toBytes("0231")
    //    )
    //    filterList.addFilter(versionFilter)
    //    filterList.addFilter(catalogCodeFilter1)
    //    filterList.addFilter(catalogCodeFilter2)
    //    val scan = new Scan
    //    scan.setFilter(filterList)
    //    // hbaseConf.set(TableInputFormat.SCAN, TableMapReduceUtil.convertScanToString(scan))
    //    //    byte[][] qualifiers = { Bytes.toBytes("name"), Bytes.toBytes("gender"),
    //    //      Bytes.toBytes("age"), Bytes.toBytes("address") };
    //    val qualifiers = Array(Bytes.toBytes("i:XMAN_ID"), Bytes.toBytes("i:EVENT"),
    //      Bytes.toBytes("i:CATALOG_CODE"), Bytes.toBytes("i:SERIAL"), Bytes.toBytes("i:XML"))
    //
    //    TableInputFormat.addColumns(scan, qualifiers)


    val scan = new Scan()
    val proto: ClientProtos.Scan = ProtobufUtil.toScan(scan)
    hbaseConf.set(TableInputFormat.SCAN, Base64.encodeBytes(proto.toByteArray))

  val sc = ss.sparkContext
  sc.newAPIHadoopRDD(hbaseConf,
    classOf[TableInputFormat],
    classOf[ImmutableBytesWritable],
    classOf[Result]).
    map { case (rowkey, result) =>
      new HbaseResult(Bytes.toString(rowkey.get), result, isOnlyLastVersion = true) //封装返回结果信息
    }
}

}
