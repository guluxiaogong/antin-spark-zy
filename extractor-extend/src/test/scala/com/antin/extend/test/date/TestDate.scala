package com.antin.extend.test.date

import java.text.SimpleDateFormat
import java.util.regex.{Matcher, Pattern}

import com.antin.extend.util.DateHelper

/**
  * Created by Administrator on 2017-12-06.
  */
object TestDate {
  //  def main(args: Array[String]) {
  //    val sdf = new SimpleDateFormat("yyyy-MM-dd")
  //    val startTime = DateHelper.converToFormat("2017.1-1")
  //    val endTime = sdf.format(DateHelper.devYear(sdf.parse(startTime), 6))
  //    println(startTime)
  //    println(endTime)
  //  }
  def main(args: Array[String]) {
    val string="350212199502261583"
   // val string = "000000000000000000"
    println(regexMatch(string))
  }

  def regexMatch(checkValue: String): Boolean = {
    val regex: String = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}[0-9Xx]$)"
    val p: Pattern = Pattern.compile(regex)
    val m: Matcher = p.matcher(checkValue)
    m.matches
  }
}
