package com.antin.extend.util;


import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jichangjin on 2017/9/26.
 */
public class StringUtil {

    public static void main(String[] args) {
//        System.out.println(StringUtil.replaceBlank("ykptjc 眼科检查  检查结果：\n" +
//                "    ⒈ 矫正&lt;左眼&gt;：5.1(1.2)\n" +
//                "    ⒉ 矫正&lt;右眼&gt;：5.1(1.2)  234001 234:左眼 无 2009-04-02 09:37:34"));
        System.out.println(isXML(xml3));
    }

    static String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<root version=\"1.0.0.7\"></root>";
    static String xml2 = "<?xml version=\"1.0\" encoding=\"utf-16\"?>\n" +
            "<ClinicalDocument>\n" +
            "  <version code=\"2.0.0.0\" date=\"2010-05-11\">根据卫生部标准第一次修订</version>\n" +
            "  <ehr code=\"0701\" codeSystem=\"STD_EHR\">预防接种</ehr>\n" +
            "  <title>预防接种</title>\n" +
            "  <org code=\"350211A1001\" codeSystem=\"STD_HEALTH_ORG\">厦门市第一医院</org>\n" +
            "  <id extension=\"HEBP_01\" eventno=\"HEBP_01\"/>\n" +
            "  <effectiveTime value=\"2012-1-9 0:00:00\"/>\n" +
            "  <recordTarget>\n" +
            "    <patient>\n" +
            "      <id extension=\"CL350247695\"/>\n" +
            "      <name></name>\n" +
            "      <birthDate>2012-1-9 12:17:00</birthDate>\n" +
            "      <sex code=\"1\" codeSystem=\"GB/T 2261.1-2003\">男性</sex>\n" +
            "      <address>\n" +
            "        <type code=\"09\" codeSystem=\"CV0300.01\">现住址</type>\n" +
            "        <adminDivision code=\"行政区划分代码\" codeSystem=\"GB/T 2260-2007\"/>\n" +
            "        <state>350000</state>\n" +
            "        <city>350200</city>\n" +
            "        <county>350203</county>\n" +
            "        <street></street>\n" +
            "        <village></village>\n" +
            "        <streetAddressLine>福建省连城县莒溪镇莒市村桐耕寮2号</streetAddressLine>\n" +
            "        <postalCode></postalCode>\n" +
            "      </address>\n" +
            "      <address>\n" +
            "        <type code=\"01\" codeSystem=\"CV0300.01\">福建省连城县莒溪镇莒市村桐耕寮2号</type>\n" +
            "        <adminDivision code=\"行政区划分代码\" codeSystem=\"GB/T 2260-2007\">行政区划分名称</adminDivision>\n" +
            "        <streetAddressLine>福建省连城县莒溪镇莒市村桐耕寮2号</streetAddressLine>\n" +
            "        <postalCode></postalCode>\n" +
            "      </address>\n" +
            "      <telephone>\n" +
            "        <type code=\"04\" codeSystem=\"CV0400.01\">家庭电话</type>\n" +
            "        <value></value>\n" +
            "      </telephone>\n" +
            "    </patient>\n" +
            "  </recordTarget>\n" +
            "  <component>\n" +
            "    <section>\n" +
            "      <code code=\"HRB03_01\" displayName=\"预防接种\">\n" +
            "        <entry>\n" +
            "          <certification displayName=\"出生医学证明编号\">L350247695</certification>\n" +
            "          <barcode_no displayName=\"条码号\"></barcode_no>\n" +
            "          <fathername displayName=\"父亲姓名\">江勇</fathername>\n" +
            "          <mothername displayName=\"母亲姓名\">沈爱萍</mothername>\n" +
            "          <doctor code=\"\" displayName=\"疫苗接种者姓名\">谢吟梅</doctor>\n" +
            "          <hospital code=\"350211A1001\" displayName=\"疫苗接种单位名称\">厦门市第一医院</hospital>\n" +
            "          <immunitydate displayName=\"疫苗接种日期\">2012-1-9 0:00:00</immunitydate>\n" +
            "          <bacterin displayName=\"疫苗-名称\">乙肝疫苗</bacterin>\n" +
            "          <times displayname=\"疫苗针次\"></times>\n" +
            "          <tabno displayName=\"疫苗-批号\">20101154-4</tabno>\n" +
            "          <validperiod displayName=\"疫苗有效期\"></validperiod>\n" +
            "          <injection_mode displayName=\"注射方式\">皮下</injection_mode>\n" +
            "          <injection_position displayName=\"注射部位\"></injection_position>\n" +
            "          <dose displayName=\"疫苗剂量\">5.000000</dose>\n" +
            "          <producer displayName=\"疫苗产商\">深圳生物制品有限公司</producer>\n" +
            "          <character displayName=\"疫苗性质\"></character>\n" +
            "          <remark displayName=\"疫苗备注\"></remark>\n" +
            "        </entry>\n" +
            "      </code>\n" +
            "    </section>\n" +
            "  </component>\n" +
            "</ClinicalDocument>";
static String xml3="<root>\n"+
            "  <PAT_MASTER_INDEX>\n"+
            "    <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "    <INP_NO>226004</INP_NO>\n"+
            "    <NAME>林秀珍</NAME>\n"+
            "    <NAME_PHONETIC>LIN XIU KAI</NAME_PHONETIC>\n"+
            "    <SEX>女</SEX>\n"+
            "    <DATE_OF_BIRTH>1941-11-7</DATE_OF_BIRTH>\n"+
            "    <BIRTH_PLACE>350625</BIRTH_PLACE>\n"+
            "    <CITIZENSHIP>CN</CITIZENSHIP>\n"+
            "    <NATION>汉族</NATION>\n"+
            "    <ID_NO />\n"+
            "    <IDENTITY>一般人员</IDENTITY>\n"+
            "    <CHARGE_TYPE>自费</CHARGE_TYPE>\n"+
            "    <UNIT_IN_CONTRACT />\n"+
            "    <MAILING_ADDRESS>后社黄晓松</MAILING_ADDRESS>\n"+
            "    <ZIP_CODE>361000</ZIP_CODE>\n"+
            "    <PHONE_NUMBER_HOME />\n"+
            "    <PHONE_NUMBER_BUSINESS />\n"+
            "    <NEXT_OF_KIN>林宝清</NEXT_OF_KIN>\n"+
            "    <RELATIONSHIP>7</RELATIONSHIP>\n"+
            "    <NEXT_OF_KIN_ADDR>厦门市大西洋海景城以C座23H</NEXT_OF_KIN_ADDR>\n"+
            "    <NEXT_OF_KIN_ZIP_CODE>361000</NEXT_OF_KIN_ZIP_CODE>\n"+
            "    <NEXT_OF_KIN_PHONE>13906960348</NEXT_OF_KIN_PHONE>\n"+
            "    <LAST_VISIT_DATE>2007-1-15 7:55:30</LAST_VISIT_DATE>\n"+
            "    <VIP_INDICATOR>2</VIP_INDICATOR>\n"+
            "    <CREATE_DATE />\n"+
            "    <OPERATOR>曾忆恋</OPERATOR>\n"+
            "    <CARD_NO />\n"+
            "    <ID />\n"+
            "    <ZYLSH />\n"+
            "    <CHECK_NO />\n"+
            "    <CEHR_PATIENT_ID>621eef43-ed9b-4db9-afb5-223ec45b256c</CEHR_PATIENT_ID>\n"+
            "    <CEHR_SSID>B07000979656</CEHR_SSID>\n"+
            "  </PAT_MASTER_INDEX>\n"+
            "  <ORDERSs>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>4</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>C</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>血常规</ORDER_TEXT>\n"+
            "      <ORDER_CODE>C020</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 10:56:51</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>5</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>C</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>凝血指标检测</ORDER_TEXT>\n"+
            "      <ORDER_CODE>C232</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 10:56:51</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>6</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>C</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>肝功能检验</ORDER_TEXT>\n"+
            "      <ORDER_CODE>C002</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 10:56:52</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>7</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>C</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>肌酐</ORDER_TEXT>\n"+
            "      <ORDER_CODE>c503</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 10:56:52</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>8</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>D</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>胸部正位检查</ORDER_TEXT>\n"+
            "      <ORDER_CODE>CT44</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 10:56:52</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>3</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>9</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>D</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>心电图检查</ORDER_TEXT>\n"+
            "      <ORDER_CODE>D41</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 10:56:52</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>3</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>10</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>C</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>血糖 (常规法)</ORDER_TEXT>\n"+
            "      <ORDER_CODE>C328</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 10:56:52</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>11</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>D</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>左下肢静脉彩超</ORDER_TEXT>\n"+
            "      <ORDER_CODE>D349</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 10:56:52</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>3</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>12</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>*盐酸左氧氟沙星注射液</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP01159</ORDER_CODE>\n"+
            "      <DOSAGE>100.0000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>ml</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>静滴</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 11:06:12</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME>2007-1-15 0:00:00</LAST_PERFORM_DATE_TIME>\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-15 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>13</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>*甲硝唑注射液</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP00371</ORDER_CODE>\n"+
            "      <DOSAGE>0.5000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>g</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>静滴</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>12:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 11:06:12</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME>2007-1-15 0:00:00</LAST_PERFORM_DATE_TIME>\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-15 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>14</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>*5%葡萄糖注射液</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP00010</ORDER_CODE>\n"+
            "      <DOSAGE>250.0000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>ml</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>静滴</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>13:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 11:06:13</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME>2007-1-15 0:00:00</LAST_PERFORM_DATE_TIME>\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-15 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>14</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>2</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>疏血通注射液</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP01374</ORDER_CODE>\n"+
            "      <DOSAGE>4.0000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>ml</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>静滴</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>13:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 11:06:13</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME>2007-1-15 0:00:00</LAST_PERFORM_DATE_TIME>\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-15 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>16</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>E</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>微波治疗</ORDER_TEXT>\n"+
            "      <ORDER_CODE>8804024</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-15 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-15 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>17:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-15 17:09:40</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-15 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>18</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>*盐酸左氧氟沙星注射液</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP01159</ORDER_CODE>\n"+
            "      <DOSAGE>100.0000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>ml</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>静滴</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:27:41</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME>2007-1-16 0:00:00</LAST_PERFORM_DATE_TIME>\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-16 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>19</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>*甲硝唑注射液</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP00371</ORDER_CODE>\n"+
            "      <DOSAGE>0.5000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>g</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>静滴</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>12:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:27:41</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME>2007-1-16 0:00:00</LAST_PERFORM_DATE_TIME>\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-16 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>20</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>I</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>明晨禁食</ORDER_TEXT>\n"+
            "      <ORDER_CODE>I78</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>21:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:27:41</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>2</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>21</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>E</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>普鲁卡因皮试( - )</ORDER_TEXT>\n"+
            "      <ORDER_CODE>8800081</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>09:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:27:41</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-16 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>22</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>*阿托品注射液</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP00324</ORDER_CODE>\n"+
            "      <DOSAGE>0.3000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>mg</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>肌肉注射</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE />\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:27:41</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>4</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>23</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>*鲁米那钠注射液</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP00104</ORDER_CODE>\n"+
            "      <DOSAGE>100.0000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>mg</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>肌肉注射</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 9:27:41</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 9:27:41</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE />\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:27:41</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>4</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>24</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>注射用白眉蛇毒血凝酶</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP01846</ORDER_CODE>\n"+
            "      <DOSAGE>1.0000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>ku</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>肌肉注射</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 9:27:41</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 9:27:41</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL>术前</FREQ_DETAIL>\n"+
            "      <PERFORM_SCHEDULE />\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:27:41</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>4</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>25</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>E</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>中换药</ORDER_TEXT>\n"+
            "      <ORDER_CODE>8800113</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:44:57</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-16 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>26</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>E</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>备皮</ORDER_TEXT>\n"+
            "      <ORDER_CODE>E168</ORDER_CODE>\n"+
            "      <DOSAGE />\n"+
            "      <DOSAGE_UNITS />\n"+
            "      <ADMINISTRATION />\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:45:02</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>3</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME />\n"+
            "      <LAST_ACCTING_DATE_TIME />\n"+
            "      <DRUG_BILLING_ATTR />\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>27</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>外用重组人表皮生长因子衍生物</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP01633</ORDER_CODE>\n"+
            "      <DOSAGE>15.0000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>ml</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>外用</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>11:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:53:00</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME>2007-1-16 0:00:00</LAST_PERFORM_DATE_TIME>\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-16 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>28</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>1</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <ORDER_TEXT>*生理盐水注射液</ORDER_TEXT>\n"+
            "      <ORDER_CODE>YP00072</ORDER_CODE>\n"+
            "      <DOSAGE>250.0000</DOSAGE>\n"+
            "      <DOSAGE_UNITS>ml</DOSAGE_UNITS>\n"+
            "      <ADMINISTRATION>静滴</ADMINISTRATION>\n"+
            "      <DURATION />\n"+
            "      <DURATION_UNITS />\n"+
            "      <START_DATE_TIME>2007-1-16 0:00:00</START_DATE_TIME>\n"+
            "      <STOP_DATE_TIME>2007-1-16 0:00:00</STOP_DATE_TIME>\n"+
            "      <FREQUENCY />\n"+
            "      <FREQ_COUNTER>1</FREQ_COUNTER>\n"+
            "      <FREQ_INTERVAL>1</FREQ_INTERVAL>\n"+
            "      <FREQ_INTERVAL_UNIT>日</FREQ_INTERVAL_UNIT>\n"+
            "      <FREQ_DETAIL />\n"+
            "      <PERFORM_SCHEDULE>13:00</PERFORM_SCHEDULE>\n"+
            "      <PERFORM_RESULT />\n"+
            "      <ORDERING_DEPT>D2</ORDERING_DEPT>\n"+
            "      <DOCTOR>陈战</DOCTOR>\n"+
            "      <STOP_DOCTOR />\n"+
            "      <NURSE>李丹</NURSE>\n"+
            "      <ENTER_DATE_TIME>2007-1-16 9:59:45</ENTER_DATE_TIME>\n"+
            "      <ORDER_STATUS>2</ORDER_STATUS>\n"+
            "      <BILLING_ATTR>0</BILLING_ATTR>\n"+
            "      <LAST_PERFORM_DATE_TIME>2007-1-16 0:00:00</LAST_PERFORM_DATE_TIME>\n"+
            "      <LAST_ACCTING_DATE_TIME>2007-1-16 0:00:00</LAST_ACCTING_DATE_TIME>\n"+
            "      <DRUG_BILLING_ATTR>0</DRUG_BILLING_ATTR>\n"+
            "      <STOP_ORDER_DATE_TIME />\n"+
            "      <STOP_NURSE />\n"+
            "    </ORDERS>\n"+
            "    <ORDERS>\n"+
            "      <PATIENT_ID>00906010</PATIENT_ID>\n"+
            "      <VISIT_ID>1</VISIT_ID>\n"+
            "      <ORDER_NO>28</ORDER_NO>\n"+
            "      <ORDER_SUB_NO>2</ORDER_SUB_NO>\n"+
            "      <REPEAT_INDICATOR>0</REPEAT_INDICATOR>\n"+
            "      <ORDER_CLASS>A</ORDER_CLASS>\n"+
            "      <";
    /**
     * 判断是否是xml结构
     */
    public static boolean isXML(String value) {
        try {
            DocumentHelper.parseText(value);
        } catch (DocumentException e) {
            return false;
        }
        return true;
    }

    /**
     * 去除空格、回车、换行
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            //Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * @param is 输入流
     * @return String 返回的字符串
     * @throws IOException
     */
    public static String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        is.close();
        String result = baos.toString();
        baos.close();
        return result;
    }

    /**
     * 转换为下划线
     *
     * @param camelCaseName
     * @return
     */
    public static String underscoreName(String camelCaseName) {
        StringBuilder result = new StringBuilder();
        if (camelCaseName != null && camelCaseName.length() > 0) {
            result.append(camelCaseName.substring(0, 1).toLowerCase());
            for (int i = 1; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    result.append("_");
                    result.append(Character.toLowerCase(ch));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }

    /**
     * 转换为驼峰
     *
     * @param underscoreName
     * @return
     */
    public static String camelCaseName(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }
}
