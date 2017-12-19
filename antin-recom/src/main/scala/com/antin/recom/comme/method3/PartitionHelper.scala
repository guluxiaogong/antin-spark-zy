package com.antin.recom.comme.method3

import org.apache.spark.Partitioner

/**
  * Created by Administrator on 2017/9/6.
  */
class PartitionHelper(ins: Array[String]) extends Partitioner {

  val parMap = new scala.collection.mutable.HashMap[String, Int]()
  var count = 0
  for (i <- ins) {
    parMap += (i -> count)
    count += 1
  }

  override def numPartitions: Int = ins.length

  override def getPartition(key: Any): Int = {
    parMap.getOrElse(key.toString, 0)
  }
}
