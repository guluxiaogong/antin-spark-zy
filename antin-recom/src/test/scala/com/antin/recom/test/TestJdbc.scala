package com.antin.recom.test

import java.util.Properties

/**
  * Created by Administrator on 2017/8/15.
  */
object TestJdbc {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("TestJdbc").setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    //通过并行化创建RDD
    val personRDD = sc.parallelize(Array("1 tom 5", "2 jerry 3", "3 kitty 6")).map(_.split(" "))
    //通过StructType直接指定每个字段的schema
    val schema = StructType(
      List(
        StructField("id", StringType, true),
        StructField("codes", StringType, true)
      )
    )
    //将RDD映射到rowRDD
    val rowRDD = personRDD.map(p => Row(p(0).trim, p(1).trim))
    //将schema信息应用到rowRDD上
    val personDataFrame = sqlContext.createDataFrame(rowRDD, schema)
    val prop = new Properties()
    prop.put("user", "urp")
    prop.put("password", "urp")
    personDataFrame.write.mode("append").jdbc("jdbc:oracle:thin:@192.168.2.146:1521:orcl", "rec_pre_doctor_doctorss", prop)
    //停止SparkContext
    sc.stop()
  }
}
