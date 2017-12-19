package com.antin.parser2.input

import com.antin.parser2.source.HBaseResult
import org.apache.spark.sql.{DataFrame, Dataset}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag


trait Input {

  val config: InputConfig

  //过滤结果集
  def dataset[T <: Product : ClassTag](filter: ((String, HBaseResult)) => Boolean)(implicit tag: TypeTag[T]): Dataset[T] =
    throw new RuntimeException("Do not implement...")

  def dataset[T <: Product : ClassTag](implicit tag: TypeTag[T]): Dataset[T]

  //过滤结果集
  def dataframe(filter: ((String, HBaseResult)) => Boolean): DataFrame =
    throw new RuntimeException("Do not implement...")


  def dataframe: DataFrame
}
