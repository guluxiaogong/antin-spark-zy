package com.antin.extend.analyzer


import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2017/6/23.
  * 执行计算用户画像标签
  */
object AnalyzerTester {
  def main(args: Array[String]) {


    //sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

    System.setProperty("_USER_NAME", "jcj") //本地运行

    // val sparkConf= new SparkConf().setMaster("local[*]").setConfig("","")

    //创建sparkSession
    val ss = SparkSession.builder()
       .master("local[*]") //本地运行
     // .master("yarn") //集群运行
      .appName("AnalyzerTester")

      //SparkSession连接ES失败//TODO  sparkSession连接es连不上，要用SparkContext可以
      // .config("es.nodes", "hadoop-007,hadoop-008,hadoop-009,hadoop-010")
      //.config("es.port", "9200")
      //.config("es.index.auto.create", "true")

      //.config("hbase.client.keyvalue.maxsize", "524288000") //最大500m
      //.config("spark.local.dir", "E:\\Workspace\\Idea\\temp\\hadoop-spark\\hadoop-portrait\\src\\main\\resources\\\\")
      // .config("_CONF_DIR", "E:\\Workspace\\Idea\\temp\\hadoop-spark\\hadoop-portrait\\src\\main\\resources\\\\*")
      .getOrCreate()



    try {
      //解析yml文件
      // val analyzer = new MedicalOrgAnalyzer(ss, "/yml/hbase/MedicalOrg.yml")//连接hbase测试

      /*
       * 清洗检验检查数据，标准化类别
       */
      // val analyzer = new ExamineAnalyzer(ss, "/yml/hbase/examine.yml")//检查
      // val analyzer = new CheckoutAnalyzer(ss, "/yml/hbase/checkout.yml") //检验
      //val analyzer = new PyhExamAnalyzer(ss, "/yml/hbase/pyhExam.yml") //体检

      /*
       *清洗药品数据
       */
     // val analyzer = new PyhsicAnalyzer(ss, "/yml/hbase/physic.yml") //药品
      // val analyzer = new PyhsicDictAnalyzer(ss, "/yml/oracle/pyhsicDict.yml")//药品字典

      /*
       * 用户画像标签计算
       */
      //val analyzer = new DiabetesAnalyzer(ss, "/yml/oracle/chronic.yml") //糖尿病
      //val analyzer = new HypertensionAnalyzer(ss, "/yml/oracle/chronic.yml") //高血压
      //val analyzer = new OracleTestAnalyzer(ss, "/yml/oracle/oracleTest.yml")//连接oracle测试
      //val analyzer = new PregnantAnalyzer(ss, "/yml/oracle/pregnant.yml") //孕产妇==>依赖【连接oracle测试】
      //val analyzer = new AgeGroupAnalyzer(ss, "/yml/oracle/ageGroup.yml") //年龄段
      //val analyzer = new BabyMotherAnalyzer(ss, "/yml/oracle/babyMother.yml") //宝妈_01
      // val analyzer = new BabyMFAnalyzer(ss, "/yml/oracle/babyM.yml") //宝妈_02,宝爸
      // val analyzer = new BaseInfoNewAnalyzer(ss, "/yml/hbase/baseInfo.yml")//基本信息

      /*
       * 将归档数据导入HBase
       */
      //val analyzer = new SehrXmanEhr_HBase(ss, "/yml/oracle/sehrXmanEhr.yml")
      //val analyzer = new SehrXmanEhr_HBase2(ss, "/yml/oracle/sehrXmanEhr.yml") //TODO 增加xmanid字段，事实上好像没必要加个字段

      //val dt = analyzer.processDataset

     // analyzer.writeDataset(dt)

    } catch {
      case e: Exception => e.printStackTrace()
    }
    finally {
      ss.close
    }
  }
}
