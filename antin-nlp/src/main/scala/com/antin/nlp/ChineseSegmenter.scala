package com.antin.nlp

import java.util.Properties

import edu.stanford.nlp.ie.crf.CRFClassifier
import edu.stanford.nlp.ling.CoreLabel

class ChineseSegmenter {
}

object ChineseSegmenter extends App {
  val props = new Properties
  val basedir = "data"
  props.setProperty("sighanCorporaDict", basedir)
  props.setProperty("serDictionary", "/home/smy/software/stanford-segmenter-2016-10-31/dict.ser.gz")
  props.setProperty("inputEncoding", "UTF-8")
  props.setProperty("sighanPostProcessing", "true")

  val segmenter = new CRFClassifier[CoreLabel](props)
  segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props)
  val sample = "胸廓对称，气管居中。双肺野未见实质性病变。双肺门影未见增浓、增大，双膈面光滑、肋膈角锐利。心影大致正常。侧位像：纵隔、肺门未见异"
  val segmented = segmenter.segmentString(sample)
  println(segmented)
}