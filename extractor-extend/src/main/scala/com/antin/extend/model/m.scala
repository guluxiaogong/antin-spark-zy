package com.antin.extend.model

/**
  * Created by Administrator on 2017/6/23.
  */
/*
 * 指导数据
 */
//用药
case class Pyhsic(xmanId: String, event: String, catalogCode: String, serial: String, orgCode: String, startDate: Seq[String], freq: Seq[String], days: Seq[String], code: Seq[String], name: Seq[String], customeCode: Seq[String], customeName: Seq[String])

case class OutPyhsic2HBase(rowKey: String, xmanId: String, event: String, catalogCode: String, serial: String, orgCode: String, startDate: String, freq: String, days: String, code: String, name: String, customeCode: String, customeName: String)

/*
 * 标签实体
 */
//基本信息
case class BaseInfoModel(var xmanId: String, var name: String, var age: String,
                         var sex: String, var weight: String, var birthDate: String,
                         var idNo: String, var address: String, var marriage: String, var work: String)

//年龄段
case class AgeGroupModel(xmanId: String, ageGroup: String)

//糖尿病
case class DiabetesModel(xmanId: String, diabetes: String)

//高血压
case class HypertensionModel(xmanId: String, hypertension: String)

//孕产妇
case class PregnantModel(xmanId: String, pregnant: String)

//宝爸、宝妈
case class BabyMFModel(xmanId: String, babyMF: String)

//case class TestInModel(key: String, orgCode: String, code: String, classCode: String, observationCode: Seq[String], observationDisplayName: Seq[String], observationValue: Seq[String], observationUnit: Seq[String], observationNotes: Seq[String], observationInterpretationCode: Seq[String])

case class TestOutModel(key: String, xmanId: String, event: String, catalogCode: String, serial: String, commitTime: String,
                        orgCode: String, title: String, code: String, classCode: String, observationCode: String,
                        observationDisplayName: String, observationValue: String, observationUnit: String,
                        observationNotes: String, observationInterpretationCode: String)

case class TestMedOutModel(key: String, xmanId: String, event: String, catalogCode: String, serial: String, commitTime: String,
                           orgCode: String, title: String, customeCode: String, custome: String, spec: String,
                           totalUnit: String, totalQuantity: String, doseUnit: String, doseQuantity: String)