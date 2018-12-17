package com.heasy.app.core.service;

import com.heasy.app.core.configuration.ConfigBean;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface ConfigurationService extends Service {
    ConfigBean getConfigBean();
}
