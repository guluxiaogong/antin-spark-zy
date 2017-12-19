package com.antin.recom.comme.method2

import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Created by Administrator on 2017/8/10.
  * 第三步：进行余弦相似度计算；获取每位医生的相似医生
  *
  * 输出:
  * 医院编码-医院科室-医生编号     相似医生s
  */
object CosinusCaluler {

  private[comme] var path: String = classOf[FetchFeatures].getClassLoader.getResource(".").toString.substring(6)

  val users = Set[String]()

  var source = Map[String, Map[String, Double]]()

  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("CosinusCaluler")
      .master("local[*]")
      .getOrCreate()

    getSource(spark) //初始化

    //正试输出用
    val result = users.map(u => {
      val user2users = users.filter(_.startsWith(u.split("-")(0))).map(user => {
        (u, user, getCollaborateSource(u, user))
      }).toList.sortWith(_._3 > _._3).map(x => {
        (x._1.split("-").tail.mkString("-") + " ", x._2.split("-").tail.mkString("-"))
      }).groupBy(_._1).values.head.map(x => {
        x._2
      })
      val uu = u.split("-").tail.mkString("-")
      uu + " " + user2users.dropWhile(_ == uu).take(100).mkString(",") //TODO
    })
    val resultRdd = spark.sparkContext.parallelize(result.toSeq)
    //resultRdd.saveAsTextFile("file:///" + path + "/医生与医生们关系")
    OutPut.ToOracle2(spark, resultRdd)

    println()
    spark.stop()
  }

  /**
    * 加载已经二值化的特征数据
    *
    * @param spark
    * @return
    */
  def getSource(spark: SparkSession): Map[String, Map[String, Double]] = {
    // implicit spark.implicits._
    val data = spark.read.textFile("file:///" + path + "/features-2/*.txt")
    data.foreach(x => {
      val lines = x.split(" ")
      users.+(lines(0))
      source += (lines(0) -> Map("org_id" -> lines(2).trim.toDouble, "name" -> lines(3).trim.toDouble, "s_name" -> lines(4).trim.toDouble, "tech_title" -> lines(5).trim.toDouble))
    })
    source
  }

  /**
    * 两两计算余弦相似性
    *
    * @param user1
    * @param user2
    * @return
    */
  def getCollaborateSource(user1: String, user2: String): Double = {
    val user1FilmSource = source.get(user1).get.values.toVector //获得第1个用户的
    val user2FilmSource = source.get(user2).get.values.toVector //获得第2个用户的
    val member = user1FilmSource.zip(user2FilmSource).map(d => d._1 * d._2).reduce(_ + _).toDouble //对公式分子部分进行计算
    val temp1 = math.sqrt(user1FilmSource.map(num => {
        //求出分母第1个变量值
        math.pow(num, 2) //数学计算
      }).reduce(_ + _)) //进行叠加
    //println("temp1:" + temp1)
    val temp2 = math.sqrt(user2FilmSource.map(num => {
        ////求出分母第2个变量值
        math.pow(num, 2) //数学计算
      }).reduce(_ + _)) //进行叠加
    val denominator = temp1 * temp2 //求出分母
    member / denominator //进行计算
  }

  def validate(spark: SparkSession, data: Dataset[String], name: String): String = {
    import spark.implicits._
    val value = data.select("value").map(x => {
      val v = x.getAs[String]("value").split(" ")
      (v(0), v.tail)
    }).filter(_._1 == name).map(_._2)
    value.first().mkString(" ")
  }
}
