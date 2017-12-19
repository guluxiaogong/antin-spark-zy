package com.antin.parser2.input

import com.antin.parser2.source.HBaseResult
import org.apache.hadoop.hbase.util.Bytes

import scala.util.Try

case class Yield(name: String, typ: String, collection: Option[String] = None)

abstract class Field(val config: Map[String, Any]) extends Serializable {
  val column = config("column").asInstanceOf[String]

  def yields: Seq[Yield]
  def apply(x: HBaseResult): Seq[Any]
}

abstract class HbaseField(override val config: Map[String, Any]) extends Field(config) {
  var family = config.get("family").asInstanceOf[Option[String]]

  def setDefaultFamily(f: Option[String]) = family = family.orElse(f)//TODO

  override def apply(result: HBaseResult): Seq[Any] = {
    if (family.isEmpty) throw new NoSuchElementException("family")

    val value = for {
      family <- result.get(family.get)//列族
      column <- family.get(column)//列
    } yield column.asInstanceOf[Array[Byte]]
    Seq(value.getOrElse(null))
  }
}

class SimpleField(override val config: Map[String, Any]) extends HbaseField(config) {
  val typ = config.getOrElse("type", "string").asInstanceOf[String]//默认为string类型
  val alias = config.get("alias").asInstanceOf[Option[String]]
  val name = alias.getOrElse(column)

  override def yields = Seq(Yield(name, typ))

  override def apply(result: HBaseResult): Seq[Any] = {
    val cell = super.apply(result).head.asInstanceOf[Array[Byte]] 
    val value = typ match {
      case "byte" => cell
      case "int" => Bytes.toInt(cell)
      case "long" => Bytes.toLong(cell)
      case "float" => Bytes.toFloat(cell)
      case "double" => Bytes.toDouble(cell)
      case "string" => Bytes.toString(cell)
      case other => throw new UnsupportedOperationException(s"unknow type: $other")
    }
    Seq(value)
  }
}

abstract class CodecField(override val config: Map[String, Any]) extends HbaseField(config) {
  case class Extract(xpath: String, typ: String, alias: Option[String], collection: Option[String])

  val codec = config("codec").asInstanceOf[String]
  val extracts = config.get("extracts").asInstanceOf[Option[Seq[Map[String, String]]]].
    getOrElse(Nil).
    map { x => Extract(
      x("xpath"),
      x.getOrElse("type", "string"),
      x.get("alias"),
      x.get("collection"))
    }

  override def yields = extracts.map { e => Yield(e.alias.getOrElse(e.xpath), e.typ, e.collection) }

  override def apply(result: HBaseResult): Seq[Any] = {
    super.apply(result).map(b => Bytes.toString(b.asInstanceOf[Array[Byte]]))
  }
}

import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.{XPathConstants, XPathFactory}

import org.w3c.dom.NodeList
import org.xml.sax.InputSource

class XmlField(override val config: Map[String, Any]) extends CodecField(config) {
  private def castType(value: String, typ: String): Any = typ match {
    case "byte" => Bytes.toBytes(value)
    case "int" => Try(value.toInt).toOption
    case "long" => Try(value.toLong).toOption
    case "float" => Try(value.toFloat).toOption
    case "double" => Try(value.toDouble).toOption
    case "string" => value
    case other => throw new UnsupportedOperationException(s"Unknow type: $other")
  }

  private def processNodeList(nodes: NodeList, typ: String): Seq[Any] =
    (0 until nodes.getLength).
      map { i => nodes.item(i).getTextContent }.
      map { v => castType(v, typ) }

  override def apply(result: HBaseResult): Seq[Any] = {
    val docBuilder = DocumentBuilderFactory.newInstance.newDocumentBuilder
    val xpathFactory = XPathFactory.newInstance
    val xpaths = extracts.map { x => xpathFactory.newXPath.compile(x.xpath) }

    val cell = super.apply(result).head.asInstanceOf[String]
    val doc = docBuilder.parse(new InputSource(new StringReader(cell)))
    xpaths.zip(yields).map { case (xpath, y) =>
      y.collection match {
        case None => castType(xpath.evaluate(doc), y.typ)
        case Some("array") => processNodeList(xpath.evaluate(doc, XPathConstants.NODESET).asInstanceOf[NodeList], y.typ)
        case Some(other) => throw new UnsupportedOperationException(s"Unknow collection: $other")
      }
    }
  }
}

//字段模式匹配，普通字段与xml字段分开处理
object Field {
  def load(config: Map[String, Any]): Field = config.get("codec") match {
    case Some("xml") => new XmlField(config)//xml字段
    case None => new SimpleField(config)//普通字段
    case Some(other) => throw new IllegalArgumentException(s"Unknow codec: $other")
  }
}
