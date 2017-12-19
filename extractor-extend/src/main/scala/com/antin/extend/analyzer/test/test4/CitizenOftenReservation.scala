package com.antin.extend.analyzer.test.test4

import java.io.File
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object CitizenOftenReservation {
  def main(args: Array[String]) {
    //    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    //    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
   // System.setProperty("HADOOP_USER_NAME", "hive")
    val warehouseLocation = new File("spark-warehouse").getAbsolutePath
    val spark = SparkSession
      .builder()
      .appName("oftenreservation")
      .master("local")
      .config("spark.sql.warehouse.dir", warehouseLocation)
      // .config("spark.sql.shuffle.partitions", 1000)
      .enableHiveSupport()
      .getOrCreate()

    import spark.sql
    sql(
      """
        |select a.id,a.id_no,a.name,b.index_code from
        |hadoop_recom.sehr_xman a
        |left join hadoop_recom.sehr_xman_index b on a.id=b.xman_id
      """.stripMargin).show(100, false)
    /*    sql(
          """
            |select a.id,a.id_no,a.name,b.index_code from
            |hadoop_recom.sehr_xman a
            |left join hadoop_recom.sehr_xman_index b on a.id=b.xman_id
          """.stripMargin).createTempView("urp_sehr_xman")
        sql(
          """
            |select id,org_id,dept_code,doctor_code from urp_sehr_xman a inner
            |join hadoop_recom.urp_reservation b on(
            |(a.id_no=b.id_card and a.name=b.name )or b.card_no=a.index_code )
          """.stripMargin).createTempView("urp_reservation_history")
        sql(
          """
            |select id,org_id,dept_code,doctor_code, count(1) as nums from
            |urp_reservation_history
            |group by id,org_id,dept_code,doctor_code
          """.stripMargin).createTempView("urp_res_num")
        sql(
          """
            |select id,org_id,dept_code,doctor_code, row_number() over(partition by id order by nums) as rank from
            |urp_res_num
          """.stripMargin).createTempView("urp_rank")

        sql(
          """select id,org_id,dept_code,doctor_code,rank from
            |urp_rank
            |where rank<4
          """.stripMargin).show(100, false)*/
    spark.stop()
  }
}
