package com.antin.base.input
import reflect.runtime.universe.TypeTag
import reflect.ClassTag
import org.apache.hadoop.mapreduce.InputFormat
import org.apache.spark.sql.{Dataset, DataFrame}
import org.apache.spark.rdd.RDD

trait Input {
  val config: InputConfig

  def dataset[T <: Product : ClassTag](filter: ((String, HbaseResult)) => Boolean)(implicit tag: TypeTag[T]): Dataset[T]
  def dataset[T <: Product : ClassTag](implicit tag: TypeTag[T]): Dataset[T]
  def dataframe(filter: ((String, HbaseResult)) => Boolean): DataFrame
  def dataframe: DataFrame
}
