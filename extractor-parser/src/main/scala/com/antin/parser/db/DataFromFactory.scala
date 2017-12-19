package com.antin.parser.db

/**
  * Created by Administrator on 2017-11-03.
  */
object DataFromFactory {
  def dataFromHBase: DataFrom = new DataFromHBase

  def dDataFromOracle(): DataFrom = new DataFromOracle
}
