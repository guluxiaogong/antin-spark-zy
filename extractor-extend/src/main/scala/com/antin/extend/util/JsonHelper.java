package com.antin.extend.util;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jichangjin on 2017/9/19.
 */
public class JsonHelper {
    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(String json) {
        return JSON.parseObject(json, Map.class);
    }

    public static String obj2JsonString(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static void main(String[] args) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map1 = new HashMap<>();
        map1.put("aa", "aaaaaaaa");
        map1.put("bb", "bbbbbbbb");
        Map<String, String> map2 = new HashMap<>();
        map2.put("aa", "aaaaaaaa");
        map2.put("bb", "bbbbbbbb");
        list.add(map1);
        list.add(map2);
        System.out.println(obj2JsonString(list));
    }
}
