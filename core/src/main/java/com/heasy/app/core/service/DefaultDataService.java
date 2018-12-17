package com.heasy.app.core.service;

import com.heasy.app.core.configuration.ConfigBean;
import com.heasy.app.core.datastorage.DataCache;
import com.heasy.app.core.datastorage.MemoryDataCache;
import com.heasy.app.core.datastorage.SQLCipherManager;
import com.heasy.app.core.datastorage.SQLCipherManagerImpl;
import com.heasy.app.core.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Administrator on 2018/2/7.
 */
public class DefaultDataService extends AbstractService implements DataService {
    private static Logger logger = LoggerFactory.getLogger(DefaultDataService.class);
    private SQLCipherManager sqlCipherManager;
    private DataCache globalMemoryDataCache;

    @Override
    public void init() {
        copyDBFile2SDCard();

        ConfigBean configBean = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean();
        sqlCipherManager = SQLCipherManagerImpl.getInstance(getHeasyContext().getServiceEngine().getAndroidContext(), configBean);
        globalMemoryDataCache = new MemoryDataCache();

        successInit = true;
    }

    @Override
    public boolean isInit() {
        return successInit;
    }

    private void copyDBFile2SDCard(){
        ConfigBean configBean = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean();

        //db文件从assets复制到sdcard
        String dbFilePath = configBean.getDBFilePath();
        File file = new File(dbFilePath);

        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        FileUtil.copyFromAssetsToSDCard(getHeasyContext().getServiceEngine().getAndroidContext(), configBean.getDbName(), dbFilePath);
        logger.debug("db file copy to: " + dbFilePath);
    }

    @Override
    public void unInit() {
        sqlCipherManager = null;

        if(globalMemoryDataCache != null) {
            globalMemoryDataCache.clear();
            globalMemoryDataCache = null;
        }

        successInit = false;
    }

    @Override
    public SQLCipherManager getSQLCipherManager() {
        return sqlCipherManager;
    }

    @Override
    public DataCache getGlobalMemoryDataCache() {
        return globalMemoryDataCache;
    }

}
