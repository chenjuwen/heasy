package com.heasy.app.core;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.heasy.app.core.service.ServiceEngineFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/9/14.
 */
public class HeasyApplication extends MultiDexApplication {
    private static HeasyApplication instance;
    private ConcurrentHashMap<String, Activity> activities = new ConcurrentHashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        exit();
    }

    public void exit(){
        finishAllActivity();

        try {
            ServiceEngineFactory.getServiceEngine().close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        System.exit(0);
    }

    public static HeasyApplication getInstance(){
        return instance;
    }

    public void addActivity(Activity activity){
        activities.put(activity.getClass().getSimpleName(), activity);
    }

    public Activity getMainActivity(){
        return activities.get("MainActivity");
    }

    public void finishActivity(Activity activity){
        if (activity != null) {
            this.activities.remove(activity.getClass().getSimpleName());
            activity.finish();
        }
    }

    public void finishAllActivity(){
        for (Activity activity : activities.values()) {
            if (null != activity) {
                activity.finish();
            }
        }
        activities.clear();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
