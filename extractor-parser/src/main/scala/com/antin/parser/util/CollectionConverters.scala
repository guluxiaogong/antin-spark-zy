package com.antin.parser.util

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * 将java map转化成scala元组工具类
  */
object CollectionConverters {
  def recursivelyToScala[K](map: java.util.Map[K, _ <: Any]): Map[K, Any] = {
    map.asScala.toMap.map { case (k, v) =>
      (k, v match {
        case v: java.util.Map[K, Any] => recursivelyToScala(v)
        case v: java.util.List[Any] => recursivelyToScala(v)
        case _ => v
      })
    }
  }

  def recursivelyToScala(list: java.util.List[Any]): Seq[Any] = {
    list.asScala.toList.map(x =>
      x match {
        case v: java.util.Map[Any, Any] => recursivelyToScala(v)
        case v: java.util.List[Any] => recursivelyToScala(v)
        case _ => x
      }
    )
  }
}
