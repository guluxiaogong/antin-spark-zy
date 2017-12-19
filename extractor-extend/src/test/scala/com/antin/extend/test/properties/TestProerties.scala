package com.antin.extend.test.properties

import com.antin.extend.util.ConfigHelper

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

/**
  * Created by jichangjin on 2017/10/18.
  */
object TestProerties {

  def main(args: Array[String]) {
    val pro = ConfigHelper.loadProperties("properties/checkout.properties")
//    val names = pro.propertyNames()
    //    val map = new HashMap[String, List[String]]
    //    while (names.hasMoreElements) {
    //      val key = names.nextElement().asInstanceOf[String]
    //      val value = new String(pro.getProperty(key).getBytes("ISO-8859-1"), "UTF-8")
    //      val values = value.split(",")
    //      map += (key -> values.toList)
    //      // println(key + " :: " + new String(pro.getProperty(key).getBytes("ISO-8859-1"), "UTF-8"))
    //    }

  }

}
