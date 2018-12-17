package com.heasy.app.core.configuration;

import com.heasy.app.core.service.DefaultConfigurationService;
import com.heasy.app.core.utils.Dom4jUtil;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2018/12/13.
 */
public class DefaultConfigLoader extends AbstractConfigLoader<ConfigBean> {
    private static Logger logger = LoggerFactory.getLogger(DefaultConfigurationService.class);

    @Override
    protected ConfigBean parseConfigFile(Document document) {
        try {
            String ROOT_PATH = "/root/";
            ConfigBean configBean = new ConfigBean();

            configBean.setSdcardRootPath(Dom4jUtil.getNodeText(document, ROOT_PATH + "sdcardRootPath"));

            configBean.setDbName(Dom4jUtil.getNodeText(document, ROOT_PATH + "dbName"));
            configBean.setDbPassword(Dom4jUtil.getNodeText(document, ROOT_PATH + "dbPassword"));
            configBean.setDbVersion(Dom4jUtil.getNodeText(document, ROOT_PATH + "dbVersion"));

            String countDownInterval = Dom4jUtil.getNodeText(document, ROOT_PATH + "countDownInterval");
            configBean.setCountDownInterval(Integer.parseInt(countDownInterval));

            configBean.setCountDownTimeoutPage(Dom4jUtil.getNodeText(document, ROOT_PATH + "countDownTimeoutPage"));

            configBean.setActionBasePackage(Dom4jUtil.getNodeText(document, ROOT_PATH + "actionBasePackage"));
            configBean.setServiceBasePackage(Dom4jUtil.getNodeText(document, ROOT_PATH + "serviceBasePackage"));

            configBean.setWebviewLoadBasePath(Dom4jUtil.getNodeText(document, ROOT_PATH + "webviewLoadBasePath"));
            configBean.setWebviewMainPage(Dom4jUtil.getNodeText(document, ROOT_PATH + "webviewMainPage"));

            return configBean;

        }catch(Exception ex){
            logger.error("failed to execute DefaultConfigLoader", ex);
        }
        return null;
    }

}
