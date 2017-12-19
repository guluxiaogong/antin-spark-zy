package com.antin.recom.comme.method3

import java.sql.{DriverManager, ResultSet}

import com.antin.recom.helper.IKHelper
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
  * Created by Administrator on 2017/9/4.
  * 将医生按标准科室分类输出文件（每个标准科室一个文件）
  */
class LoadFromOracle {
}

object LoadFromOracle {

  private[method3] var path: String = classOf[LoadFromOracle].getClassLoader.getResource(".").toString.substring(6)

  def main(args: Array[String]) {

    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val conf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("LoadFromOracle")
    val sc = new SparkContext(conf)

    val jdbcRDD = new JdbcRDD(
      sc,
      () => {
        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
        DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.146:1521:orcl", "urp", "urp")
      },
      str,
      1, 8564, 5, //8564
      r => {
        val buf = func2(r)
        (r.getString("s_code"), buf)

        //        import scala.collection.JavaConverters._
        //        val list = ResultHelper.queryAsJson(r).asScala
        //(r.getString("code"), list)
      }
    ).cache()

    //标准科室
    val standCode = jdbcRDD.map(_._1).distinct().collect()

    val deptParitioner = new PartitionHelper(standCode)

    val result = jdbcRDD.partitionBy(deptParitioner)
      .mapPartitions(func3)
    //.mapPartitions(_.map(_._2))

    result.saveAsTextFile("file:///" + path + "/dept2")

    //result.mapPartitionsWithIndex(func).foreach(println)
    //jdbcRDD.collect().foreach(println)
    println("============" + standCode.length)
    sc.stop()
  }

  /**
    * 所有字段
    *
    * @param result
    * @return
    */
  def func1(result: ResultSet): String = {
    val column = result.getMetaData.getColumnCount
    val buf = new StringBuilder
    for (c <- 1 to column) {
      buf ++= replaceChars(result.getString(c))
      buf ++= "\t"
    }
    buf.toString
  }

  /**
    * 所有字段
    * 对科室和描述分词
    *
    * @param result
    * @return
    */
  def func2(result: ResultSet): String = {
    val column = result.getMetaData.getColumnCount
    val buf = new StringBuilder
    for (c <- 1 to column) {
      var colu = replaceChars(result.getString(c))
      if (c == 4 || c == 14) {
        colu = IKHelper.ikAnalyzer(colu)
      }
      buf ++= colu
      buf ++= "\t"
    }
    buf.toString
  }

  /**
    * 取部分字段
    *
    * @param iter
    * @return
    */
  def func3(iter: Iterator[(String, String)]): Iterator[String] = {
    val list = new ListBuffer[String]
    while (iter.hasNext) {
      val buf = new StringBuilder
      val line = iter.next()
      val lines = line._2.split("\t")
      buf ++= lines.last
      buf ++= "\t"
      buf ++= lines(3).replaceAll("\\|", "\t")
      list.+=(buf.toString())
    }
    list.toIterator
  }


  def replaceChars(str: String): String = {
    if (str == null || "".equals(str))
      return str
    val res = str.replaceAll("(\0|\\s*|\r|\n)", "")
    res
  }

//  /**
//    * 决定了数据到哪个分区里面
//    *
//    * @param ins
//    */
//  class HostParitioner(ins: Array[String]) extends Partitioner {
//
//    val parMap = new mutable.HashMap[String, Int]()
//    var count = 0
//    for (i <- ins) {
//      parMap += (i -> count)
//      count += 1
//    }
//
//    override def numPartitions: Int = ins.length
//
//    override def getPartition(key: Any): Int = {
//      parMap.getOrElse(key.toString, 0)
//    }
//  }

  val str = "  select *\n    from (select rownum row_num,\n                 t4.code as org_code,\n                 t1.org_id,\n                 t2.name dept_name,\n                 t3.name as s_dept_name,\n                 t3.code as s_code,\n                 t1.sex,\n                 t1.degree,\n                 t1.reservation_num,\n                 t1.type_name,\n                 t1.avg_score,\n                 t1.address_id,\n                 t1.common_flag,\n    t1.begood,\n                 t1.dept_code,\n                 t1.code,\n                 t1.name,\n                 t1.tech_title,\n                 t2.standard_dept || '-' || t1.org_id || '-' || t1.dept_code || '-' ||\n                 t1.sex || '-' || t1.code as doctor\n            from urp_doctor t1\n            left join urp_dept t2\n              on t1.org_id = t2.org_id\n             and t1.dept_code = t2.code\n            left join urp_dept_standard t3\n              on t2.standard_dept = t3.code\n            left join rec_urp_org t4\n              on t1.org_id = t4.org_id\n           where t3.code is not null\n             and t1.status = 1)\n   where row_num >= ?\n     and row_num <= ?"
  val str1 =
    s"""  select *
        |    from (select rownum row_num,
        |                 t4.code as org_code,
        |                 t1.org_id,
        |                 t2.name dept_name,
        |                 t3.name as s_dept_name,
        |                 t3.code as s_code,
        |                 t1.sex,
        |                 t1.degree,
        |                 t1.reservation_num,
        |                 t1.type_name,
        |                 t1.avg_score,
        |                 t1.address_id,
        |                 t1.common_flag,
        |                 t1.begood,
        |                 t1.dept_code,
        |                 t1.code,
        |                 t1.name,
        |                 t1.tech_title,
        |                 t2.standard_dept || '-' || t1.org_id || '-' || t1.dept_code || '-' ||
        |                 t1.code as doctor
        |            from urp_doctor t1
        |            left join urp_dept t2
        |              on t1.org_id = t2.org_id
        |             and t1.dept_code = t2.code
        |            left join urp_dept_standard t3
        |              on t2.standard_dept = t3.code
        |            left join rec_urp_org t4
        |              on t1.org_id = t4.org_id
        |           where t3.code is  null
        |             and t1.status = 1)
        |   where row_num >= ?
        |     and row_num <= ?""".stripMargin.replaceAll("\n", " ")

}

//
//select distinct s_code from
//(
//select *
//from (select rownum row_num,
//t4.code as org_code,
//t1.org_id,
//t2.name dept_name,
//t3.name as s_dept_name,
//t3.code as s_code,
//t1.sex,
//t1.degree,
//t1.reservation_num,
//t1.type_name,
//t1.avg_score,
//t1.address_id,
//t1.common_flag,
//t1.begood,
//t1.dept_code,
//t1.code,
//t1.name,
//t1.tech_title,
//t2.standard_dept || '-' || t1.org_id || '-' || t1.dept_code || '-' ||
//t1.sex || '-' || t1.code as doctor
//from urp_doctor t1
//left join urp_dept t2
//on t1.org_id = t2.org_id
//and t1.dept_code = t2.code
//left join urp_dept_standard t3
//on t2.standard_dept = t3.code
//left join rec_urp_org t4
//on t1.org_id = t4.org_id
//where t3.code is not null
//and t1.status = 1)
//where row_num >= 1
//and row_num <= 8564
//)