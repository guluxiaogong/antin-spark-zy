package com.antin.recom.defaults

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created by Administrator on 2017/9/8.
  */
case class WeatherModel(date: Date, temperature: Float, pressure: Float, humidity: Float, wind: Float, visibility: Float)

object SqlAnalyzer {

  def main(args: Array[String]) {
    System.setProperty("HADOOP_USER_NAME", "jcj")

    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val spark = SparkSession
      .builder()
      .appName("ReadFromOracle")
      .master("local[1]")
      //.master("yarn")
      .getOrCreate()

    //cleanoutWeather(spark)
    cleanoutReservation(spark)

  }

  def cleanoutWeather(spark: SparkSession): Unit = {
    val weather = spark.read.text("hdfs://hadoop-cluster/user/jcj/rec/weather.text/")
    //val wSchemaString = "时间 温度 气压 湿度 风速 能见度"
    val wSchemaString = "time_range temperature pressure humidity wind visibility"

    // Generate the schema based on the string of schema
    val wfields = wSchemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val wschema = StructType(wfields)
    val weatherRdd = weather.rdd
      .map(_.getString(0).split("\t"))
      .map(m => {
        //        val dateStr = m(0).trim.replaceAll("\"", "")
        //        val date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse(dateStr)
        //时间；温度；气压；湿度；风速;能见度
        //Row(date, m(1).trim.replaceAll("\"", "").toFloat, m(2).trim.replaceAll("\"", "").toFloat, m(5).trim.replaceAll("\"", "").toFloat, m(7).trim.replaceAll("\"", "").toFloat, m(21).trim.replaceAll("\"", "").toFloat)
        Row(m(0).trim.replaceAll("\"", ""), m(1).trim.replaceAll("\"", ""), m(2).trim.replaceAll("\"", ""), m(5).trim.replaceAll("\"", ""), m(7).trim.replaceAll("\"", ""), m(21).trim.replaceAll("\"", ""))
      })

    val weatherDF = spark.createDataFrame(weatherRdd, wschema)

    //import spark.sql
    import spark.sql

    weatherDF.createOrReplaceTempView("hadoop_weather")


    sql("select * from hadoop_weather")
      .write.json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_weather.json")

  }

  def cleanoutReservationJson(spark: SparkSession): Unit = {
    val reservationDF = spark.read.text("hdfs://hadoop-cluster/user/jcj/rec/urp_reservation_history.json")
    reservationDF.createOrReplaceTempView("hadoop_reservation")

    val rSchemaString = "citizen_id org_id res_date doctor_code doctor_name dept_code dept_name register_time status start_time end_time source res_callin number_id name card_no id_card res_id local_source wechatordevice_no time_range"

    val rfields = rSchemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val rschema = StructType(rfields)

    import spark.sql
    val reservation = sql("select citizen_id org_id res_date doctor_code doctor_name dept_code dept_name register_time status start_time end_time source res_callin number_id name card_no id_card res_id local_source wechatordevice_no from hadoop_reservation")
      .rdd.map(m => {
      val startTime = m(9).toString

      Row(m(0), m(1), m(2), m(3), m(4), m(5), m(6), m(7), m(8), m(9), m(10), m(11), m(12), m(13), m(14), m(15), m(16), m(17), m(18), m(19), convertTime(startTime))
    })

    val reservationClDF = spark.createDataFrame(reservation, rschema)
    reservationClDF.createOrReplaceTempView("hadoop_reservation")

    import spark.sql
    sql("select org_id,dept_code,dept_name,doctor_name,doctor_code,time_range from hadoop_reservation")
      .write.mode("overwrite").json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation.json")
    // .show(false)
  }

  def cleanoutReservation(spark: SparkSession): Unit = {
    // val rSchemaString = "市民id 机构id 预约日期 医生编码 医生姓名 科室编码 科室名称 登记时间 状态 开始时间 结束时间 来源 呼入电话 排班明细表id 姓名 医保卡号 身份证号 主键 预约来源 微信号或设备号 时间段"
    val rSchemaString = "citizen_id org_id res_date doctor_code doctor_name dept_code dept_name register_time status start_time end_time source res_callin number_id name card_no id_card res_id local_source wechatordevice_no time_range"

    val reservation = spark.read.text("hdfs://hadoop-cluster/user/jcj/rec/urp_reservation_history.text/")

    val rfields = rSchemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val rschema = StructType(rfields)
    val reservationRdd = reservation.rdd
      .map(_.getString(0).split("\t"))
      .filter(_.size == 38)
      .map(m => {
        val startTime = m(9).trim.replaceAll("\"", "")

        Row(m(0).trim.replaceAll("\"", ""), m(1).trim.replaceAll("\"", ""), m(2).trim.replaceAll("\"", ""), m(3).trim.replaceAll("\"", ""),
          m(4).trim.replaceAll("\"", ""), m(5).trim.replaceAll("\"", ""), m(6).trim.replaceAll("\"", ""), m(7).trim.replaceAll("\"", "")
          , m(8).trim.replaceAll("\"", ""), m(9).trim.replaceAll("\"", ""), m(10).trim.replaceAll("\"", ""), m(11).trim.replaceAll("\"", "")
          , m(14).trim.replaceAll("\"", ""), m(20).trim.replaceAll("\"", ""), m(21).trim.replaceAll("\"", ""), m(22).trim.replaceAll("\"", "")
          , m(23).trim.replaceAll("\"", ""), m(24).trim.replaceAll("\"", ""), m(31).trim.replaceAll("\"", ""), m(37).trim.replaceAll("\"", ""), convertTime(startTime))
      })
    val reservationDF = spark.createDataFrame(reservationRdd, rschema)
    reservationDF.createOrReplaceTempView("hadoop_reservation")

    import spark.sql
    sql("select org_id,dept_code,dept_name,doctor_name,doctor_code,time_range from hadoop_reservation")
      .write.mode("overwrite").json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation.json")
    // .show(false)

  }


  val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  //非线程安全的
  val sdf2 = new SimpleDateFormat("dd.MM.yyyy HH:mm") //非线程安全的

  def convertTime(timeStr: String): String = {
    val date = sdf.parse(timeStr)
    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    if (hour >= 0 && hour < 3) {
      calendar.set(Calendar.HOUR_OF_DAY, 2)
    } else if (hour >= 3 && hour < 6) {
      calendar.set(Calendar.HOUR_OF_DAY, 5)
    } else if (hour >= 6 && hour < 9) {
      calendar.set(Calendar.HOUR_OF_DAY, 8)
    } else if (hour >= 9 && hour < 12) {
      calendar.set(Calendar.HOUR_OF_DAY, 11)
    } else if (hour >= 12 && hour < 15) {
      calendar.set(Calendar.HOUR_OF_DAY, 14)
    } else if (hour >= 15 && hour < 18) {
      calendar.set(Calendar.HOUR_OF_DAY, 17)
    } else if (hour >= 18 && hour < 21) {
      calendar.set(Calendar.HOUR_OF_DAY, 20)
    } else if (hour >= 21 && hour < 24) {
      calendar.set(Calendar.HOUR_OF_DAY, 23)
    }

    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    sdf2.format(calendar.getTime)
  }

}
