package com.heasy.app.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/13.
 */
public class SharedPreferencesUtil {
    public static String FILE_NAME = "config";

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
    }

    /***
     * get方法
     */
    public String getString(Context context, String key){
        return getSharedPreferences(context).getString(key, "");
    }

    public String getString(Context context, String key, String defaultValue){
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    public boolean getBoolean(Context context, String key, boolean defaultValue){
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public float getFloat(Context context, String key, float defaultValue){
        return getSharedPreferences(context).getFloat(key, defaultValue);
    }

    public int getInt(Context context, String key, int defaultValue){
        return getSharedPreferences(context).getInt(key, defaultValue);
    }

    public long getLong(Context context, String key, long defaultValue){
        return getSharedPreferences(context).getLong(key, defaultValue);
    }


    /***
     * put方法
     */
    public void putString(Context context, String key, String value){
        getSharedPreferences(context).edit().putString(key, value).commit();
    }

    public void putBoolean(Context context, String key, boolean value){
        getSharedPreferences(context).edit().putBoolean(key, value).commit();
    }

    public void putFloat(Context context, String key, float value){
        getSharedPreferences(context).edit().putFloat(key, value).commit();
    }

    public void putInt(Context context, String key, int value){
        getSharedPreferences(context).edit().putInt(key, value).commit();
    }

    public void putLong(Context context, String key, long value){
        getSharedPreferences(context).edit().putLong(key, value).commit();
    }

    public void put(Context context, Map<String, Object> map){
        if(map != null){
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            for(Iterator<String> it = map.keySet().iterator(); it.hasNext();){
                String key = it.next();
                Object value = map.get(key);
                editor.putString(key, String.valueOf(value));
            }
            editor.commit();
        }
    }


    /***
     * remove方法
     */
    public void remove(Context context, String...keys){
        if(keys != null && keys.length > 0){
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            for(String key : keys) {
                editor.remove(key);
            }
            editor.commit();
        }
    }


    /***
     * clear方法
     */
    public void clear(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.commit();
    }

}
