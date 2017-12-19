package com.antin.search.hbase

import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, KeyValue}
import org.apache.hadoop.mapreduce.Job
import org.apache.log4j.{Level, Logger}
import org.apache.spark._

/**
  * Created by Administrator on 2017/7/18.
  * 直接Bulk Load数据到Hbase
  * 这种方法不需要事先在HDFS上生成Hfiles，而是直接将数据批量导入到Hbase中。与上面的例子相比只有微小的差别，具体如下：
  * 将
  * rdd.saveAsNewAPIHadoopFile("/tmp/iteblog", classOf[ImmutableBytesWritable], classOf[KeyValue], classOf[HFileOutputFormat], conf)
  * 修改成：
  * rdd.saveAsNewAPIHadoopFile("/tmp/iteblog", classOf[ImmutableBytesWritable], classOf[KeyValue], classOf[HFileOutputFormat], job.getConfiguration())
  */
object Demo3 {
  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    System.setProperty("HADOOP_USER_NAME", "jcj")
    System.setProperty("user.name", "jcj")
    val sc = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("Demo2"))

    val conf = HBaseConfiguration.create()
    val tableName = "sehr_xman_ehr"
    val table = new HTable(conf, tableName)

    conf.set(TableOutputFormat.OUTPUT_TABLE, tableName)

    val job = Job.getInstance(conf)
    job.setMapOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setMapOutputValueClass(classOf[KeyValue])

    HFileOutputFormat.configureIncrementalLoad(job, table)

    // Generate 10 sample data:
    val num = sc.parallelize(1 until 10)
    val rdd = num.map(x => {
      val kv: KeyValue = new KeyValue(Bytes.toBytes(x), "i".getBytes(), "c1".getBytes(), "value_xxx".getBytes())
      (new ImmutableBytesWritable(Bytes.toBytes(x)), kv)
    })

    //以下两种方法没测试成功！//TODO
    //方法一:
    // Directly bulk load to Hbase/MapRDB tables.
    rdd.saveAsNewAPIHadoopFile("/user/jcj/tmp/jcj_word_0", classOf[ImmutableBytesWritable], classOf[KeyValue], classOf[HFileOutputFormat], job.getConfiguration())
    //方法二：
    //    job.getConfiguration.set("mapred.output.dir", "/user/jcj/tmp/jcj_word")
    //    rdd.saveAsNewAPIHadoopDataset(job.getConfiguration)
  }

}
