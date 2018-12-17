package com.heasy.app.baiduai.config;

import android.content.Context;

/**
 * Created by Administrator on 2018/12/13.
 */
public class AIConfigFactory {
    private static AIConfigBean configBean = null;

    public static AIConfigBean getAIConfigBean(Context context){
        if(configBean == null){
            synchronized (AIConfigFactory.class){
                if(configBean == null){
                    AIConfigLoader loader = new AIConfigLoader();
                    configBean = loader.loadFromAssets(context, AIConfigBean.AICONFIG_FILE_NAME);
                }
            }
        }

        return configBean;
    }

}
