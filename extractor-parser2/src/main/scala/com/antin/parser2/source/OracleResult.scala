package com.antin.parser2.source

import java.sql.ResultSet

import com.antin.parser2.util.StringUtil
import oracle.sql.BLOB

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


/**
  * Created by Administrator on 2017-11-24.
  */
class OracleResult(result:ResultSet) extends DataResult {

  private def queryAsJson(rs: ResultSet): (ListBuffer[String], ListBuffer[String], mutable.HashMap[String, Any]) = {
    // import scala.collection.mutable.ListBuffer
    // val array = new ArrayBuffer[mutable.HashMap[String, Any]]
    val labels = new ListBuffer[String]
    val types = new ListBuffer[String]
    val column = rs.getMetaData.getColumnCount
    val map = new mutable.HashMap[String, Any]
    for (i <- 1 to column) {
      val resultSetMetaData = rs.getMetaData
      val label = resultSetMetaData.getColumnLabel(i)
      val columnNme = StringUtil.camelCaseName(label.toLowerCase)
      labels += columnNme
      val typeName = resultSetMetaData.getColumnTypeName(i)
      types += typeName
      typeName match {
        case "CLOB" => map.put(columnNme, rs.getString(i))
        case "BLOB" => {
          val blob = rs.getBlob(i).asInstanceOf[BLOB]
          // 得到数据库的输出流
          val stream = blob.getBinaryStream
          val content = StringUtil.readFromStream(stream)
          map.put(columnNme, content)
        }
        case _ => map.put(columnNme, rs.getObject(i))
      }
    }
    (labels, types, map)
  }
}
