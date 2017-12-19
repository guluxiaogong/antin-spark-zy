package com.antin.parser2.source

import com.antin.parser2.input.Field
import com.antin.parser2.util.CollectionConverters
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes

/**
  * Created by Administrator on 2017-11-24.
  */
object HBaseResult {
  type Value = Array[Byte]
  type Cell = Map[Long, Value]
  type Family = Map[String, Value]
  type Row = Map[String, Family]
}

abstract class DataResult extends Serializable

class HBaseResult( result: Result, val familyName: Option[String] = None, val isOnlyLastVersion: Boolean = false) extends DataResult {

  import HBaseResult._

  val row = processResult(result)

  def this(result: Result, familyName: String) = this(result, Some(familyName))

  def this(result: Result, familyName: String, isOnlyLastVersion: Boolean) = this(result, Some(familyName), isOnlyLastVersion)

  def apply(f: String): Family = row(f)

  def get(f: String): Option[Family] = row.get(f)

  def getFields(fields: Seq[Field]): Seq[Tuple2[String, Map[Long, Any]]] = ???

  def getLastVersionCell(family: String, column: String): Value = getCellLastVersion(row(family)(column).asInstanceOf[Map[Long, Value]])

  private def getCellLastVersion(cell: Map[Long, Value]): Value =
    cell.toSeq.sortBy {
      _._1
    }.last._2

  private def processResult(result: Result): Row = {
    CollectionConverters.recursivelyToScala[Value](result.getMap).map { case (fname, f) =>
      (
        Bytes.toString(fname),
        f.asInstanceOf[Map[Value, Any]].map { case (cname, c) =>
          (Bytes.toString(cname),
            isOnlyLastVersion match {
              case false => c
              case true => getCellLastVersion(c.asInstanceOf[Map[Long, Value]])
            }
            )
        }
        )
    }.asInstanceOf[Row].
      filterKeys { k => familyName.getOrElse(k) == k }.map(identity)
  }
}
