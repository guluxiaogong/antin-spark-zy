package com.antin.recom.comme.method3

import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Created by Administrator on 2017/8/10.
  * 第二步：将特征值二值化
  * <p>
  * 将文字转成数值
  */
object Word2Vec2 {
  private[comme] var path: String = classOf[LoadFromOracle].getClassLoader.getResource(".").toString.substring(6)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Word2Vec")
      .master("local")
      .getOrCreate()
    for (s <- 0 until 99) {
      var p = s.toString
      if (p.length == 1)
        p = "0" + p

      val df = spark.read.textFile("file:///" + path + "/dept2/part-000" + p) //00090//00022
      val resultRdd = myPartition(spark, df)
      //resultRdd.repartition(1)
      resultRdd.write.text("file:///" + path + "/dept3/" + p)
    }
    spark.stop()

  }

  def myPartition(spark: SparkSession, df: Dataset[String]): Dataset[String] = {
    //import spark.implicits._
    import spark.implicits._
    val featuresDF = df.map(_.split("\t")).map(Tuple1.apply).toDF("text")
    val word2Vec = new Word2Vec()
      .setInputCol("text")
      .setOutputCol("result")
      .setVectorSize(5)
      .setMinCount(0)
    val model = word2Vec.fit(featuresDF)

    val result = model.transform(featuresDF)

    val resultRdd = result.select("text", "result").map(x => {
      val arr = x.get(1).asInstanceOf[DenseVector].toArray
      val sb = new StringBuilder
      arr.foreach(a => {
        sb.append(a).append(" ")
      })
      // println(x.get(0).asInstanceOf[scala.collection.mutable.WrappedArray[Object]].array.head + " " + sb.toString())

      x.get(0).asInstanceOf[scala.collection.mutable.WrappedArray[Object]].array.head + " " + ((sb.substring(sb.indexOf(" ")).toString.trim))
    })
    resultRdd
  }
}
