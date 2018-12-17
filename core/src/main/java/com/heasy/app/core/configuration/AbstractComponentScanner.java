package com.heasy.app.core.configuration;

import android.content.Context;

/**
 * Created by cjm on 2017/6/27.
 */
public abstract class AbstractComponentScanner<T> implements ComponentScanner<T> {
    private Context context;
    private String basePackages;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String basePackages) {
        this.basePackages = basePackages;
    }

}
