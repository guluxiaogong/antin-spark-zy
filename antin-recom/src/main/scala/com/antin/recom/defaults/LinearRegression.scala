package com.antin.recom.defaults

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Created by Administrator on 2017/9/8.
  */
object LinearRegression {
  def main(args: Array[String]) {

    System.setProperty("HADOOP_USER_NAME", "jcj")

    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    // counts 预约量,
    // --dept_code 部门编码,
    // --doctor_code 医生编码,
    // --org_id 机构id,
    // time_range 时间段 ,
    // temperature 气温
    // pressure 气压
    // humidity 湿度
    // wind 风速
    // visibility 可见度
    val spark = SparkSession.builder()
      .appName("Spark Linear Regression")
      .master("local[*]")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    /*
     * 导入样本数据
     */
    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._
    val dataList: RDD[(Double, Double, Double, Double, Double, Double, Double)] = convertTimeRange(spark)
    val data = dataList.toDF("counts", "time_range", "temperature", "pressure", "humidity", "wind", "visibility")
    val colArray = Array("time_range", "temperature", "pressure", "humidity", "wind", "visibility")

    val assembler = new VectorAssembler().setInputCols(colArray).setOutputCol("features")
    val vecDF: DataFrame = assembler.transform(data)

    //val myDF: DataFrame = assembler.transform(List((3615, 3624, 2.1, 69.05, 35.1, 41.3, 20, 50708)).toDF("Population", "Income", "Illiteracy", "LifeExp", "Murder", "HSGrad", "Frost", "Area"))

    val Array(trainingDF, testDF) = vecDF.randomSplit(Array(0.9, 0.1), seed = 12345)

    // 建立模型，预测预约数counts，设置线性回归参数
    val lr = new LinearRegression().setFeaturesCol("features").setLabelCol("counts").fit(trainingDF)

    // 设置管道
    val pipeline = new Pipeline().setStages(Array(lr))

    // 建立参数网格
    val paramGrid = new ParamGridBuilder().addGrid(lr.fitIntercept)
      .addGrid(lr.elasticNetParam, Array(0.0, 0.5, 1.0)).addGrid(lr.maxIter, Array(10, 100)).build()

    // 选择(prediction, true label)，计算测试误差。
    // 注意RegEvaluator.isLargerBetter，评估的度量值是大的好，还是小的好，系统会自动识别
    val RegEvaluator = new RegressionEvaluator().setLabelCol(lr.getLabelCol).setPredictionCol(lr.getPredictionCol).setMetricName("rmse")

    val trainValidationSplit = new TrainValidationSplit().setEstimator(pipeline)
      .setEvaluator(RegEvaluator).setEstimatorParamMaps(paramGrid).setTrainRatio(0.8) // 数据分割比例

    // Run train validation split, and choose the best set of parameters.
    val tvModel = trainValidationSplit.fit(trainingDF)

    // 查看模型全部参数
    tvModel.extractParamMap()

    tvModel.getEstimatorParamMaps.length
    tvModel.getEstimatorParamMaps.foreach {
      println
    } // 参数组合的集合

    tvModel.getEvaluator.extractParamMap() // 评估的参数

    tvModel.getEvaluator.isLargerBetter // 评估的度量值是大的好，还是小的好

    tvModel.getTrainRatio

    // 用最好的参数组合，做出预测
    //tvModel.transform(testDF).select("features", "counts", "prediction").show()
    tvModel.transform(testDF).select("features", "counts", "prediction").toDF().createOrReplaceTempView("hadoop_result")
    //.write.mode("overwrite").json("hdfs://hadoop-cluster/user/jcj/rec/prediction.json")
    //      .rdd.map(m => {
    //      (Row(1), Row(2))
    //    }).saveAsTextFile("hdfs://hadoop-cluster/user/jcj/rec/prediction")
    import spark.sql
    //sql("select * from hadoop_result where ABS(counts-prediction)<1 order by counts desc").show(100, false)
    sql("select features from hadoop_result where ABS(counts-prediction)<1 order by counts desc").rdd.map(m=>{
      m(0).toString.replaceAll("\\[|\\]", "")
    }).saveAsTextFile("hdfs://hadoop-cluster/user/jcj/rec/hadoop_result")
    //tvModel.transform(myDF).select("features", "Murder", "prediction").show()
  }

  def convertTimeRange(spark: SparkSession): RDD[(Double, Double, Double, Double, Double, Double, Double)] = {
    import spark.sql
    val analyzerDF = spark.read.json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_weather")
    analyzerDF.createOrReplaceTempView("hadoop_reservation_weather")

    val list = sql("select counts,org_id,dept_code,doctor_code,time_range,temperature,pressure,humidity,wind,visibility from hadoop_reservation_weather order by counts desc").
      rdd.map(m => {
      val time = m(4).toString.split(" ")(1).split(":")(0)
      (convetToDouble(m(0)), convetToDouble(time), convetToDouble(m(5)), convetToDouble(m(6)), convetToDouble(m(7)), convetToDouble(m(8)), convetToDouble(m(9)))
    })
    // .write.mode("overwrite").json("hdfs://hadoop-cluster/user/jcj/rec/hadoop_reservation_weather")
    //.show(false)
    // list.take(30).foreach(println)
    list
  }

  def convetToDouble(param: Any): Double = {
    if (param == null || param.toString.trim == "")
      0.0
    else
      param.toString.toDouble
  }

}
