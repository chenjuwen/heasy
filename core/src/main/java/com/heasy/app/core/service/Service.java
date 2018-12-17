package com.heasy.app.core.service;

import com.heasy.app.core.HeasyContext;

/**
 * Created by Administrator on 2017/12/28.
 */
public interface Service {
    void init();
    boolean isInit();
    void unInit();
    HeasyContext getHeasyContext();
    void setHeasyContext(HeasyContext heasyContext);
}
