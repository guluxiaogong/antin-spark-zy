package com.antin.extend.test.xml2

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.{XPathConstants, XPathFactory}

import org.w3c.dom.NodeList
import org.xml.sax.InputSource

/**
  * Created by win7 on 2017/11/11.
  */
object TestXml {


  def main(args: Array[String]): Unit = {

    //调用 DocumentBuilderFactory.newInstance() 方法得到创建 DOM 解析器的工厂
    //调用工厂对象的 newDocumentBuilder方法得到 DOM 解析器对象
    val docBuilder = DocumentBuilderFactory.newInstance.newDocumentBuilder

    //解析指定的文件
    val is = this.getClass().getClassLoader().getResourceAsStream("xml.xml")

    //val doc = docBuilder.parse(new InputSource(System.in))
    val doc = docBuilder.parse(new InputSource(is))

    val xpathFactory = XPathFactory.newInstance
    val xpath = xpathFactory.newXPath.compile("//component//section//code/@code")
    // val xpath = xpathFactory.newXPath.compile("//component//section//entry//observation//code/@code")

    println("================ parse as string ================")
    println(xpath.evaluate(doc))
    println("================ parse as array ================")
    val nodes = xpath.evaluate(doc, XPathConstants.NODESET).asInstanceOf[NodeList]
    (0 until nodes.getLength).
      map { i => nodes.item(i).getTextContent }.
      foreach { t => {
        println(s"- $t")

      }

      }
    println("==========================")
  }
}
