package com.antin.extend.util

import java.util.Properties

import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{SingleColumnValueFilter, FilterList, RegexStringComparator}
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.HashMap

/**
  * Created by jichangjin on 2017/10/18.
  */
object ConfigHelper {
  def loadProperties(fileName: String): HashMap[String, List[String]] = {
    val properties = new Properties()
    // val path = Thread.currentThread().getContextClassLoader.getResource(fileName).getPath //文件要放到resource文件夹下
    val inputStream = this.getClass.getResourceAsStream(fileName)
    // properties.load(new FileInputStream(path))
    properties.load(inputStream)

    val names = properties.propertyNames()
    val map = new HashMap[String, List[String]]
    while (names.hasMoreElements) {
      val key = names.nextElement().asInstanceOf[String]
      val value = new String(properties.getProperty(key).getBytes("ISO-8859-1"), "UTF-8")
      val values = value.split(",")
      map += (key -> values.toList)
      // println(key + " :: " + new String(pro.getProperty(key).getBytes("ISO-8859-1"), "UTF-8"))
    }
    map
  }

  def buildScan(map: Map[String, Any]): Scan = {
    map.keys.foreach(f => {
      map.get(f) match {
        case Some(v) => {
          v match {
            case seq: Seq[Any] => ""
            case s: String => ""
            case None => ""
          }
        }
      }
    })

    val comp = new RegexStringComparator("2") //任意以2打头的值
    val filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL)
    val versionFilter = new SingleColumnValueFilter(
      Bytes.toBytes("i"),
      Bytes.toBytes("VERSION"),
      CompareOp.EQUAL,
      //Bytes.toBytes("2.0.0.0")
      comp
    )
    val catalogCodeFilter1 = new SingleColumnValueFilter(
      Bytes.toBytes("i"),
      Bytes.toBytes("VERSION"),
      CompareOp.EQUAL,
      Bytes.toBytes("0121")
    )
    val catalogCodeFilter2 = new SingleColumnValueFilter(
      Bytes.toBytes("i"),
      Bytes.toBytes("VERSION"),
      CompareOp.EQUAL,
      Bytes.toBytes("0221")
    )
    filterList.addFilter(versionFilter)
    filterList.addFilter(catalogCodeFilter1)
    filterList.addFilter(catalogCodeFilter2)
    val scan = new Scan
    scan.setFilter(filterList)
    scan
  }

}
