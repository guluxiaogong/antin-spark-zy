package com.antin.recom.comme.method1;


import com.antin.recom.helper.JdbcHelper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/10.
 * 第一步：医生特征提取
 * <p>
 * 特征：医院位置、医院编码、医院科室、标准科室、医生职称、医生标签、号源；
 * 现阶段只提取特征：医院编码、医院科室、标准科室、医生职称
 * 对医生描述进行分词
 */
public class FetchFeatures {

    static String path = FetchFeatures.class.getClassLoader().getResource(".").toString().substring(6);

    static FileWriter fw = null;

    static int count = 1;

    public static void main(String[] args) {

        queryFeatures();

    }

    /**
     * 将分好的词写入文件中
     *
     * @param text
     * @throws Exception
     */
    public static void writeToFile(String text) throws Exception {
        if (fw == null)
            fw = new FileWriter(path + "features.txt", true);
        fw.write(text + System.lineSeparator());
        if (count > 1000) {
            fw.flush();
            count = 1;
        }
        count++;
    }

    /**
     * 从oracle中获取医生介绍
     * 提取医生特征：医院编码、医院科室、标准科室、医生职称
     * 第一个字段为：标准科室-医院编码-医院科室-医生编码   拼接
     *
     * @return
     */
    public static void queryFeatures() {

        JdbcHelper jdbcHelper = new JdbcHelper();
        String sql = "  select t4.code as org_code,\n" +
                "         t1.org_id,\n" +
                "         t2.name dept_name,\n" +
                "         t3.name as s_dept_name,\n" +
                "         t3.code as s_code,\n" +
                "         t1.dept_code,\n" +
                "         t1.code,\n" +
                "         t1.name,\n" +
                "         t1.tech_title,\n" +
                "         t2.standard_dept || '-' || t4.code || '-' || t1.dept_code || '-' ||\n" +
                "         t1.code as doctor\n" +
                "    from urp_doctor t1\n" +
                "    left join urp_dept t2\n" +
                "      on t1.org_id = t2.org_id\n" +
                "     and t1.dept_code = t2.code\n" +
                "    left join urp_dept_standard t3\n" +
                "      on t2.standard_dept = t3.code\n" +
                "    left join urp_org t4\n" +
                "      on t1.org_id = t4.org_id\n" +
                "   where t2.name is not null\n" +
                "     and t2.standard_dept is not null\n";
        try {
            List<Map<String, Object>> result = jdbcHelper.findResult(sql, null);
            System.out.println("......................................>" + result.size());
            result.forEach(r -> {
                try {
                    writeToFile(r.get("doctor".toUpperCase()) + " " + r.get("code".toUpperCase()) + " " + r.get("dept_name".toUpperCase()) + " " + r.get("s_dept_name".toUpperCase()) + " " + r.get("tech_title".toUpperCase()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // map.put(r.get("doctor".toUpperCase()) + "", r.get("org_id".toUpperCase()) + " " + r.get("name".toUpperCase()) + " " + r.get("s_name".toUpperCase()) + " " + r.get("tech_title".toUpperCase()));
            });

        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }


}
