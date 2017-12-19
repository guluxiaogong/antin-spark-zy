package com.antin.parser.input

import org.apache.spark.sql.{DataFrame, Dataset}

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag


trait Input {

  val config: InputConfig

  def dataframe(filter: ((String, HbaseResult)) => Boolean): (String, DataFrame) =
    throw new RuntimeException("Do not implement...")

  def dataframe(filter: ((String, HbaseResult)) => Boolean, isExpansion: Boolean): (String, DataFrame) =
    throw new RuntimeException("Do not implement...")


  def dataframe: (String, DataFrame)


}
