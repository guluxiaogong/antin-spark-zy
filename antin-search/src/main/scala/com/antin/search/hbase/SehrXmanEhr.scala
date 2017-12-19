package com.antin.search.hbase

import java.sql.DriverManager

import com.antin.search.util.StringUtil
import oracle.sql.BLOB
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.mapreduce._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job

/**
  * Created by jichangjin on 2017/9/27.
  * nohup /usr/hdp/2.6.1.0-129/spark2/bin/spark-submit --class com.antin.es.SehrXmanEhr --master yarn --deploy-mode client --driver-memory 6g --executor-memory 6g  --executor-cores 3 --num-executors 6 /home/jcj/runJar/antin-test.jar >/dev/null 2>1&1 &
  * nohup spark-submit testSpark.jar >/dev/null 2>1&1 &
  */
object SehrXmanEhr {
  def main(args: Array[String]) {
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    System.setProperty("HADOOP_USER_NAME", "jcj")
    System.setProperty("user.name", "jcj")

    //val url = "jdbc:oracle:thin:@192.168.2.146:1521:orcl"
    val url = "jdbc:oracle:thin:@192.168.0.91:1521:xmhealth"
    val username = "sehr_hadoop"
    val password = "sehr_hadoop"
    //val query = "select * from (select rownum row_num,xman_id, event, catalog_code, serial, content, t.xml.getclobval() xml, compression, encryption, status, version, title, commit_time from sehr_v_xman_ehr_total t) where row_num >=? and row_num <=?"
    val query = "select * from (select rownum row_num,xman_id, event, catalog_code, serial, t.xml.getclobval() xml, compression, encryption, status, version, title, commit_time from sehr_xman_ehr_0 t order by xman_id, event, catalog_code, serial) where row_num >=? and row_num <=?"

    val conf = new SparkConf()
      .setAppName("SehrXmanEhr")
      .setMaster("local[5]")
    //.setMaster("yarn")

    val sc = new SparkContext(conf)

    val connection = () => {
      Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
      DriverManager.getConnection(url, username, password)
    }

    val jdbcRDD = new JdbcRDD(
      sc,
      connection,
      query,
      0, 20, 5, //26484203//30000000//26484207//93283
      r => {
        val column = r.getMetaData.getColumnCount
        val map = new mutable.HashMap[String, Any]
        for (i <- 1 to column) {
          val resultSetMetaData = r.getMetaData
          val label = resultSetMetaData.getColumnLabel(i)
          //val columnName = resultSetMetaData.getColumnType(i)
          val columnType = resultSetMetaData.getColumnTypeName(i)
          val name = StringUtil.camelCaseName(label.toLowerCase)
          columnType match {
            case "CLOB" => map.put(name, r.getString(i))
            case "BLOB" => {
              try {
                val vlaue = r.getBlob(i)
                if (vlaue != null)
                  map.put(name, null)
                else {
                  val blob = r.getBlob(i).asInstanceOf[BLOB]
                  // 得到数据库的输出流
                  val stream = blob.getBinaryStream
                  val content = StringUtil.readFromStream(stream)
                  map.put(name, content)
                }
              } catch {
                case e: Exception => {
                  map.put(name, null)
                  // printf("==============================================>" + e.getMessage)
                  e.printStackTrace()
                }
              }
            }
            case _ => map.put(name, r.getObject(i))
          }
        }
        map
      }
    )

    val hConf = HBaseConfiguration.create()
    val tableName = "hpor:sehr_xman_ehr"
    val table = new HTable(hConf, tableName)
    // val hbaseConn = ConnectionFactory.createConnection(conf)
    // val table = hbaseConn.getTable(TableName.valueOf("jcj_word"))

    conf.set(TableOutputFormat.OUTPUT_TABLE, tableName)

    val job = Job.getInstance(hConf)
    job.setMapOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setMapOutputValueClass(classOf[KeyValue])

    HFileOutputFormat.configureIncrementalLoad(job, table)

    //    val rdd = jdbcRDD.map(m => {
    //      val ibw = m.keySet.toList.sortBy(x => x)/*.map(k => {
    //        val key = m.getOrElse("xmanId", "").toString + ";" + m.getOrElse("event", "").toString + ";" + m.getOrElse("catalogCode", "").toString + ";" + m.getOrElse("serial", "").toString
    //        (key, k, m.getOrElse(k, "").toString)
    //      }) /*.sortBy(_._1)*/ .filter(f => f._2 == "xml").map(t => {
    //        val kv: KeyValue = new KeyValue(Bytes.toBytes(t._1), "i".getBytes(), t._2.getBytes(), t._3.getBytes())
    //        (new ImmutableBytesWritable(Bytes.toBytes(t._1)), kv)
    //      })*/
    //        .map(k => {
    //
    //              val key = m.getOrElse("xmanId", "").toString + ";" + m.getOrElse("event", "").toString + ";" + m.getOrElse("catalogCode", "").toString + ";" + m.getOrElse("serial", "").toString
    //
    //              val kv: KeyValue = new KeyValue(Bytes.toBytes(key), "i".getBytes(), /*k.getBytes()*/ "title".getBytes() , m.getOrElse("title", "").toString.getBytes() /*xml.getBytes()*/)
    //              (new ImmutableBytesWritable(Bytes.toBytes(key)), kv)
    //            })
    //      ibw
    //
    //    }).flatMap(x => x)
    //TODO
    /**
      * 数据量大时出现异常未处理
      */
    val rdd = jdbcRDD.map(m => {
      val key = m.getOrElse("xmanId", "").toString + ";" + m.getOrElse("event", "").toString + ";" + m.getOrElse("catalogCode", "").toString + ";" + m.getOrElse("serial", "").toString
      val kv: KeyValue = new KeyValue(Bytes.toBytes(key), "i".getBytes(), "title".getBytes(), m.getOrElse("title", "").toString.getBytes())
      (new ImmutableBytesWritable(Bytes.toBytes(key)), kv)

    })

    // Save Hfiles on HDFS
    rdd.saveAsNewAPIHadoopFile("/user/jcj/sehr_xman_ehr", classOf[ImmutableBytesWritable], classOf[KeyValue], classOf[HFileOutputFormat], hConf)

    // Bulk load Hfiles to Hbase
    val bulkLoader = new LoadIncrementalHFiles(hConf)
    bulkLoader.doBulkLoad(new Path("/user/jcj/sehr_xman_ehr"), table)

  }

  val xml =
    """<?xml version="1.0" encoding="utf-16"?>
      |<ClinicalDocument>
      |  <version code="2.0.0.0" date="2009-12-01">根据卫生部标准第一次修订</version>
      |  <ehr code="0101" codeSystem="STD_EHR">门诊基本诊疗信息</ehr>
      |  <title>门诊基本诊疗信息</title>
      |  <org code="350100A1008" codeSystem="STD_HEALTH_ORG">福建省中医药大学附属人民医院</org>
      |  <id extension="24330146" eventno="24330146"/>
      |  <effectiveTime value="2017-05-06 06:00:09"/>
      |  <recordTarget>
      |    <patient>
      |      <id extension="K50925381"/>
      |      <name>许开展</name>
      |      <sex code="1" codeSystem="GB/T2261.1-2003">男</sex>
      |      <birthDate>1951/5/16 0:00:00</birthDate>
      |      <marriage code="90" codeSystem="GB/T 2261.2-2003">未说明的婚姻状况</marriage>
      |    </patient>
      |  </recordTarget>
      |  <component>
      |    <section>
      |      <code code="common" codeSystem="" displayName="门诊基本诊疗信息"/>
      |      <entry>
      |        <onsetTime></onsetTime>
      |        <treatTime>2017/5/5 0:00:00</treatTime>
      |        <diagnosisDate>2017/5/5 0:00:00</diagnosisDate>
      |        <reg>
      |          <value>24330146</value>
      |          <type code="99" codeSystem="STD_REG_TYPE">便民门诊号</type>
      |        </reg>
      |        <sec>
      |          <value>350103195111300012</value>
      |          <type code="01" codeSystem="STD_SEC_TYPE">省医保</type>
      |        </sec>
      |        <dept code="" codeSystem="GB/T 17538-1998">便民门诊</dept>
      |        <doctor>
      |          <id extension="2531"/>
      |          <name>李金桂</name>
      |          <title code="233" codeSystem="GB/T 8561-1988">主治医师</title>
      |        </doctor>
      |        <symptom/>
      |        <diagnosis>
      |          <item>
      |            <icd code="F41.101" codeSystem="ICD-10">焦虑状态</icd>
      |            <result code="" codeSystem="CV5501.11"></result>
      |            <prop code="1" codeSystem="STD_DIAGNOSIS_PROP">确诊</prop>
      |          </item>
      |          <item>
      |            <icd code="E78.500" codeSystem="ICD-10">高脂血症</icd>
      |            <result code="" codeSystem="CV5501.11"></result>
      |            <prop code="1" codeSystem="STD_DIAGNOSIS_PROP">确诊</prop>
      |          </item>
      |          <item>
      |            <icd code="I10.x00" codeSystem="ICD-10">高血压病</icd>
      |            <result code="" codeSystem="CV5501.11"></result>
      |            <prop code="1" codeSystem="STD_DIAGNOSIS_PROP">确诊</prop>
      |          </item>
      |          <item>
      |            <icd code="I25.103" codeSystem="ICD-10">冠状动脉粥样硬化性心脏病</icd>
      |            <result code="" codeSystem="CV5501.11"></result>
      |            <prop code="1" codeSystem="STD_DIAGNOSIS_PROP">确诊</prop>
      |          </item>
      |          <item>
      |            <icd code="K29.500" codeSystem="ICD-10">慢性胃炎</icd>
      |            <result code="" codeSystem="CV5501.11"></result>
      |            <prop code="1" codeSystem="STD_DIAGNOSIS_PROP">确诊</prop>
      |          </item>
      |          <item>
      |            <icd code="BNS150" codeSystem="ICD-10">腰痛病</icd>
      |            <result code="" codeSystem="CV5501.11"></result>
      |            <prop code="1" codeSystem="STD_DIAGNOSIS_PROP">确诊</prop>
      |          </item>
      |          <item>
      |            <icd code="ZZSV10" codeSystem="ICD-10">肾气不足证</icd>
      |            <result code="" codeSystem="CV5501.11"></result>
      |            <prop code="1" codeSystem="STD_DIAGNOSIS_PROP">确诊</prop>
      |          </item>
      |        </diagnosis>
      |      </entry>
      |    </section>
      |  </component>
      |</ClinicalDocument>""".stripMargin
}
