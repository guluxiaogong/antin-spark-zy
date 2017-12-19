package com.antin.base.util

import org.xml.sax.InputSource
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import javax.xml.namespace.QName
import javax.xml.xpath.XPathConstants

object XpathTester {
  def printHelp = println("usage: scala -cp xx.jar com.antin.base.util.XpathTester xpath < file.xml")

  def main(args: Array[String]): Unit = {
    if (args.size < 1) {
      printHelp
      System.exit(1)
    }

    val docBuilder = DocumentBuilderFactory.newInstance.newDocumentBuilder
    val doc = docBuilder.parse(new InputSource(System.in))
    val xpathFactory = XPathFactory.newInstance
    val xpath = xpathFactory.newXPath.compile(args(0))

    println("================ parse as string ================")
    println(xpath.evaluate(doc))
    println("================ parse as array ================")
    val nodes = xpath.evaluate(doc, XPathConstants.NODESET).asInstanceOf[NodeList]
    (0 until nodes.getLength).
      map { i => nodes.item(i).getTextContent }.
      foreach { t => println(s"- $t") }
  }
}
