package com.antin.extend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017-11-01.
 */
public class Md5 {
    public static String generateUUID(String id) {

        //MD5 id
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //AdContentId + schemaId 做MD5
        byte[] bytes = md5.digest(id.toString().getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            //缩短字符串长度：对每一位取后4位，做16进制处理
            int val = (int) b & 0xf;
            sb.append(Integer.toHexString(val));
        }
        //Hash String to Long
        return sb.toString();
    }
}
