package com.antin.base.model

/**
  * Created by Administrator on 2017/6/23.
  */

case class Fund(xmanid: String, codes: Seq[String])
case class RFund(xmanid: String, payway: String)


case class ChronicDisease(xmanid: String, idc_codes: Seq[String])
case class RChronicDisease(xmanid: String, chronic: String, compliace: Boolean)


case class MedicalOrg(xmanid: String, org_code:String)
case class RMedicalOrg(xmanid: String, orgs: String)