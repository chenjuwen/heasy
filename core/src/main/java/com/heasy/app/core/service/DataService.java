package com.heasy.app.core.service;

import com.heasy.app.core.datastorage.DataCache;
import com.heasy.app.core.datastorage.SQLCipherManager;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface DataService extends Service {
    SQLCipherManager getSQLCipherManager();
    DataCache getGlobalMemoryDataCache();
}
