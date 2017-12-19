package com.antin.extend.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jichangjin on 2017/9/19.
 */
public class DateHelper {

    //出生日期字符串转化成Date对象
    public static Date parse(String strDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(strDate);
    }

    //由出生日期获得年龄
    public static int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age;
    }

    public static Date devYear(Date birthDay, int year) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(birthDay);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + year);
        return cal.getTime();
    }

    public static Date plusDate(Date date, int d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + d);
        return cal.getTime();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void main(String[] args) {
        System.out.println(converToFormat("2017.11.12"));
    }

    //日期格式化成（yyyy-MM-dd HH:mm:ss.S）
    //校验是否是日期格式
    public static String converToFormat(String dateString) {
//        String regex1 =  "^\\d{4}-\\d{1,2}-\\d{1,2}$";
//        String regex2 =  "^\\d{4}/\\d{1,2}/\\d{1,2}$";
//        String regex3 =  "^\\d{4}_\\d{1,2}_\\d{1,2}$";
        try {
            String checkValue = dateString.split(" ")[0];
            if (regexMatch(checkValue))
                return converTostd(checkValue.replaceAll("/", "-").replaceAll("\\_", "-").replaceAll("\\.", "-"));
           // return checkValue.replaceAll("/", "-").replaceAll("\\_", "-").replaceAll("\\.", "-");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String converTostd(String pstd) {
        String[] pstds = pstd.split("-");
        if (pstds.length != 3)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pstds.length; i++) {
            if (i == 0)
                sb.append(pstds[i]).append("-");
            else {
                if (pstds[i].length() == 1)
                    sb.append("0");
                sb.append(pstds[i]).append("-");
            }
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    //    YYYY-MM-DD
    //    YYYY/MM/DD
    //    YYYY_MM_DD
    //    YYYY.MM.DD的形式
    //
    //    match : 2008-2-29 2008/02/29
    private static boolean regexMatch(String checkValue) {
        String regex = "((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(checkValue);
        return m.matches();
    }
}
