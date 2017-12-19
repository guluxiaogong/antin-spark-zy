package com.antin.recom.comme.method2

import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017/8/10.
  * 第二步：将特征值二值化
  * <p>
  * 将文字转成数值
  */
object Word2Vec {
  private[comme] var path: String = classOf[FetchFeatures].getClassLoader.getResource(".").toString.substring(6)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Word2Vec")
      .master("local")
      .getOrCreate()

    val df = spark.read.textFile("file:///" + path + "/features.txt")
    //import spark.implicits._
    import spark.implicits._
    val featuresDF = df.map(_.split(" ")).map(Tuple1.apply).toDF("text")
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

      x.get(0).asInstanceOf[scala.collection.mutable.WrappedArray[Object]].array.head + " " + sb.toString()
    })
    //resultRdd.repartition(1)
    resultRdd.write.text("file:///" + path + "/features-2")
    spark.stop()
  }
}
