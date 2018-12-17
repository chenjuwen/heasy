package com.heasy.app.core.utils;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/12/3.
 */
public class JsonUtil {
    public static double getDoubleValue(JSONObject obj, String key){
        try {
            if(obj.has(key)){
                Object value = obj.get(key);
                if(value != null){
                    if(value instanceof Integer){
                        return ((Integer)value).intValue();
                    }else{
                        return ((Double)value).doubleValue();
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    public static int getIntegerValue(JSONObject obj, String key){
        try {
            if(obj.has(key)){
                Object value = obj.get(key);
                if(value != null){
                    return ((Integer)value).intValue();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    public static String getValue(JSONObject obj, String key){
        try {
            if(obj.has(key)){
                Object value = obj.get(key);
                if(value != null){
                    return value.toString();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public static double getDoubleValue(JSONObject mainObject, String firstKey, String secondKey){
        try {
            if(mainObject.has(firstKey)){
                JSONObject firstObject = mainObject.getJSONObject(firstKey);
                if(firstObject != null){
                    return getDoubleValue(firstObject, secondKey);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    public static int getIntegerValue(JSONObject mainObject, String firstKey, String secondKey){
        try {
            if(mainObject.has(firstKey)){
                JSONObject firstObject = mainObject.getJSONObject(firstKey);
                if(firstObject != null){
                    return getIntegerValue(firstObject, secondKey);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    public static String getValue(JSONObject mainObject, String firstKey, String secondKey){
        try {
            if (mainObject.has(firstKey)) {
                JSONObject firstObject = mainObject.getJSONObject(firstKey);
                if (firstObject != null) {
                    return getValue(firstObject, secondKey);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

}
