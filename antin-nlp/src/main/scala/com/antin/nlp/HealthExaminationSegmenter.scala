package com.antin.nlp

import java.util.Properties

object HealthExaminationSegmenter extends App {
  case class Record(observation: Seq[String])

  def initSegmenter: CRFClassifier[CoreLabel] = {
    val props = new Properties
    val basedir = "/home/smy/software/stanford-segmenter-2016-10-31/data"
    props.setProperty("sighanCorporaDict", basedir)
    props.setProperty("serDictionary", "/home/smy/software/stanford-segmenter-2016-10-31/dict.ser.gz")
    props.setProperty("inputEncoding", "UTF-8")
    props.setProperty("sighanPostProcessing", "true")
    val segmenter = new CRFClassifier[CoreLabel](props)
    segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props)
    segmenter
  }

  val segmenter = initSegmenter
  val inputConfig = InputConfig.load("/yml/hbase/health-examination-segmenter.yml")
  val sparkConf = new SparkConf().setMaster("local[2]")

  open[SparkSession, Unit](SparkSession.builder().config(sparkConf).getOrCreate()) { ss =>
    val input = InputFactory.getInput(inputConfig, ss)
    val in = input.dataset[Record] { args: (String, HbaseResult) =>
      Bytes.toString(args._2("i")("CATALOG_CODE")) == "0301"
    }

    in.flatMap { record =>
      val _segmenter = segmenter
      record.observation.filterNot { s =>
        s.length < 4 || """^[\d.]+$""".r.findFirstIn(s).isDefined
      }.map { case obsv: String =>
        (obsv +: segmenter.segmentString(obsv).asScala.toSeq).mkString("|")
      }
    }.write.text("health-examination-output")

  }
}
