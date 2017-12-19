package com.antin.search.util

import java.sql.DriverManager

import oracle.sql.BLOB
import org.apache.spark.SparkContext
import org.apache.spark.rdd.JdbcRDD

import scala.collection.mutable

/**
  * Created by jichangjin on 2017/10/10.
  */
object JdbcRDDHelper {

  def query(sc: SparkContext, url: String, username: String, password: String, queryString: String): JdbcRDD[mutable.HashMap[String, Any]] = {
    query(sc, url, username, password, queryString, 0, 1000, 1)

  }


  def query(sc: SparkContext, url: String, username: String, password: String, queryString: String, lowerBound: Long,
            upperBound: Long,
            numPartitions: Int): JdbcRDD[mutable.HashMap[String, Any]] = {
    val connection = () => {
      Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
      DriverManager.getConnection(url, username, password)
    }
    val jdbcRDD = new JdbcRDD(
      sc,
      connection,
      queryString,
      lowerBound, upperBound, numPartitions,
      r => {
        val column = r.getMetaData.getColumnCount
        val map = new mutable.HashMap[String, Any]
        for (i <- 1 to column) {
          val resultSetMetaData = r.getMetaData
          val label = resultSetMetaData.getColumnLabel(i)
          //val columnName = resultSetMetaData.getColumnType(i)
          val columnType = resultSetMetaData.getColumnTypeName(i)
          val name = StringUtil.camelCaseName(label.toLowerCase)
          columnType match {
            case "CLOB" => map.put(name, r.getString(i))
            case "BLOB" => {
              try {
                val vlaue = r.getBlob(i)
                if (vlaue == null)
                  map.put(name, null)
                else {
                  val blob = vlaue.asInstanceOf[BLOB]
                  // 得到数据库的输出流
                  val stream = blob.getBinaryStream
                  val content = StringUtil.streamToBytes(stream)
                  // val content = StringUtil.readFromStream(stream)
                  map.put(name, content)
                }
              } catch {
                case e: Exception => {
                  map.put(name, null)
                  // printf("==============================================>" + e.getMessage)
                  e.printStackTrace()
                }
              }

            }
            case _ => map.put(name, r.getObject(i))
          }
        }
        map
      }
    )
    jdbcRDD
  }
}
