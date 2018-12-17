package com.heasy.app.core.configuration;

import android.content.Context;

/**
 * 配置文件加载器
 */
public interface ConfigLoader<T> {
    /**
     * 从文件系统加载配置文件
     * @param filepath 文件的绝对路径
     * @return
     */
    public T load(String filepath);

    /**
     * 从assets目录中加载配置文件
     * @param context
     * @param filepath 文件在assets目录中的相对路径
     * @return
     */
    public T loadFromAssets(Context context, String filepath);

}
