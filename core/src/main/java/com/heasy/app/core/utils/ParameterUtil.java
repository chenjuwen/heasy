package com.heasy.app.core.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/10.
 */
public class ParameterUtil {
    public static String toParamString(Map<String, String> params, boolean encodeValue){
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for(Iterator<String> it=params.keySet().iterator(); it.hasNext();){
            String key = it.next();
            String value = params.get(key);

            if(index > 0){
                sb.append("&");
            }

            sb.append(key);
            sb.append("=");

            if(encodeValue) {
                try {
                    if(StringUtil.isNotEmpty(value)){
                        sb.append(URLEncoder.encode(value, "utf-8"));
                    }else{
                        sb.append("");
                    }
                } catch (UnsupportedEncodingException e) {
                    sb.append(value);
                }
            }else{
                sb.append(value);
            }

            ++index;
        }
        return sb.toString();
    }

    public static String toParamString(String key, String value){
        StringBuilder sb = new StringBuilder();
        sb.append(key + "=" + value);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2, String key3, String value3){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        sb.append("&" + key3 + "=" + value3);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        sb.append("&" + key3 + "=" + value3);
        sb.append("&" + key4 + "=" + value4);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4, String key5, String value5){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        sb.append("&" + key3 + "=" + value3);
        sb.append("&" + key4 + "=" + value4);
        sb.append("&" + key5 + "=" + value5);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4, String key5, String value5, String key6, String value6){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        sb.append("&" + key3 + "=" + value3);
        sb.append("&" + key4 + "=" + value4);
        sb.append("&" + key5 + "=" + value5);
        sb.append("&" + key6 + "=" + value6);
        return sb.toString();
    }

}
