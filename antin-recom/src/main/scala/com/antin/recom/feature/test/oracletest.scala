package com.antin.recom.feature.test

import java.text.{DecimalFormat, SimpleDateFormat}
import java.util.Date

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by wds on 2017/11/10.
  */
object Jdbctest {


//  def getCoreTime(start_time: String, end_Time: String) = {
//    var df: SimpleDateFormat = new SimpleDateFormat("HH:mm:ss")
//    var begin: Date = df.parse(start_time)
//    var end: Date = df.parse(end_Time)
//    var between: Long = (end.getTime() - begin.getTime()) / 1000 //转化成秒
//    var hour: Float = between.toFloat / 3600
//    var decf: DecimalFormat = new DecimalFormat("#.00")
//    decf.format(hour)
//  }

  def main(args: Array[String]) {
    //Logger.getLogger("org").setLevel(Level.ERROR)
    val conf = new SparkConf().setAppName("Simple Application") //给Application命名
    conf.setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc);


    /*  //这种方法没成功
  val url = "jdbc:oracle:thin:@IP:1521:数据库?user=***&password=***";
      val prop = new Properties();
      val df = sqlContext.read.jdbc(url, "pat_master_index", prop);
       df.registerTempTable("patient")
       val res = sqlContext.sql("select  patient_id ,name from patient where petient_id like '9000220%'")
      res.show()
      */


    val jdbcMap = Map("url" -> "jdbc:oracle:thin:@//192.168.2.146:1521/orcl",
      "user" -> "urp",
      "password" -> "urp",
      "dbtable" -> "test3",
      "driver" -> "oracle.jdbc.driver.OracleDriver")
    val jdbcDF = sqlContext.read.options(jdbcMap).format("jdbc").load
    jdbcDF.registerTempTable("URP_RESERVATION_HISTORY")
    //val res = sqlContext.sql(" select * from (\n   select c.* ,rank() over(partition by c.a order by c.nums desc) rank from\n   (select a.a, a.b, count(b) nums from test a group by a, b ) c \n )where rank=1")
    val res = sqlContext.sql(" select a.org_id,a.dept_code,a.doctor_code ,count(1) nums from URP_RESERVATION_HISTORY a where \na.org_id is not null and \na.dept_code is not null and\na.doctor_code is not null\ngroup by org_id,dept_code,doctor_code");
    //  res.write.save("/user/wds/result")
    res.show()
    sc.stop()
  }
}
