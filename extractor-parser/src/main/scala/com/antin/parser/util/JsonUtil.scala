package com.antin.parser.util

import java.util

import net.minidev.json.{JSONObject}
import net.minidev.json.parser.JSONParser
import scala.collection.{mutable, immutable}
import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.JavaConversions.mutableMapAsJavaMap

/**
  * Created by Administrator on 2017-11-08.
  */
object JsonUtil {
  /**
    * 将map转为json
    *
    * @param map 输入格式 mutable.Map[String,Object]
    * @return
    **/
  def map2Json(map: Map[String, Any]): String = {
    //TODO
    val suitMap: mutable.Map[String, Any] = map match {
      case m: mutable.Map[String, Any] => {
        m
      }
      case m: immutable.Map[String, Any] => {
        import collection.{mutable, breakOut}
        val converMap: mutable.Map[String, Any] = map.map(identity)(breakOut)
        converMap
      }
    }
    val myMap: util.Map[String, Any] = mutableMapAsJavaMap(suitMap)
    val jsonString = JsonHelper.obj2JsonString(myMap)
    //val jsonString = JSONObject.toJSONString()
    println("======================================" + suitMap.toBuffer.toString())
    // jsonString
    ""
  }


  /**
    * 将json转化为Map
    *
    * @param json 输入json字符串
    * @return
    **/
  def json2Map(json: String): mutable.HashMap[String, Object] = {

    val map: mutable.HashMap[String, Object] = mutable.HashMap()

    val jsonParser = new JSONParser()

    //将string转化为jsonObject
    val jsonObj: JSONObject = jsonParser.parse(json).asInstanceOf[JSONObject]

    //获取所有键
    val jsonKey = jsonObj.keySet()

    val iter = jsonKey.iterator()

    while (iter.hasNext) {
      val field = iter.next()
      val value = jsonObj.get(field).toString

      if (value.startsWith("{") && value.endsWith("}")) {
        val value = mapAsScalaMap(jsonObj.get(field).asInstanceOf[util.HashMap[String, String]])
        map.put(field, value)
      } else {
        map.put(field, value)
      }
    }
    map
  }
}
