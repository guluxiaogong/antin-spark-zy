package com.antin.extend.input

import org.apache.hadoop.hbase.client.Scan
import org.apache.spark.sql.{DataFrame, Dataset}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag


trait Input {

  val config: InputConfig

  //过滤结果集
  def dataset[T <: Product : ClassTag](filter: ((String, HbaseResult)) => Boolean)(implicit tag: TypeTag[T]): Dataset[T] =
    throw new RuntimeException("Do not implement...")

  def dataset[T <: Product : ClassTag](scan: Scan, filter: ((String, HbaseResult)) => Boolean)(implicit tag: TypeTag[T]): Dataset[T] =
    throw new RuntimeException("Do not implement...")

  def dataset[T <: Product : ClassTag](implicit tag: TypeTag[T]): Dataset[T]

  //过滤结果集
  def dataframe(filter: ((String, HbaseResult)) => Boolean): DataFrame =
    throw new RuntimeException("Do not implement...")

  def dataframe(scan: Scan, filter: ((String, HbaseResult)) => Boolean): DataFrame =
    throw new RuntimeException("Do not implement...")

  def dataframe: DataFrame
}
