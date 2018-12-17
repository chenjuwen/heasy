package com.heasy.app.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/14.
 */
public class SQLiteUtil {
    public static Map<String, Object> toMap(String key, String value){
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static Map<String, Object> toMap(String key1, String value1, String key2, String value2){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    public static Map<String, Object> toMap(String key1, String value1, String key2, String value2, String key3, String value3){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    public static Map<String, Object> toMap(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return map;
    }

    public static Map<String, Object> toMap(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4, String key5, String value5){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        return map;
    }

}
