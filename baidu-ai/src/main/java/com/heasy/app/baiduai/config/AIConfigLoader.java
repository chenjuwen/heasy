package com.heasy.app.baiduai.config;

import com.heasy.app.core.configuration.AbstractConfigLoader;
import com.heasy.app.core.utils.Dom4jUtil;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 百度接口参数
 */
public class AIConfigLoader extends AbstractConfigLoader<AIConfigBean> {
    private static Logger logger = LoggerFactory.getLogger(AIConfigLoader.class);

    @Override
    public AIConfigBean parseConfigFile(Document document) {
        try {
            String ROOT_PATH = "/root/";
            AIConfigBean configBean = new AIConfigBean();

            configBean.setTtsAppid(Dom4jUtil.getNodeText(document, ROOT_PATH + "tts/appid"));
            configBean.setTtsApikey(Dom4jUtil.getNodeText(document, ROOT_PATH + "tts/apikey"));
            configBean.setTtsSecretkey(Dom4jUtil.getNodeText(document, ROOT_PATH + "tts/secretkey"));
            configBean.setTtsPerson(Dom4jUtil.getNodeText(document, ROOT_PATH + "tts/person"));
            configBean.setTtsSpeed(Dom4jUtil.getNodeText(document, ROOT_PATH + "tts/speed"));

            configBean.setAsrAppid(Dom4jUtil.getNodeText(document, ROOT_PATH + "asr/appid"));
            configBean.setAsrApikey(Dom4jUtil.getNodeText(document, ROOT_PATH + "asr/apikey"));
            configBean.setAsrSecretkey(Dom4jUtil.getNodeText(document, ROOT_PATH + "asr/secretkey"));

            configBean.setTransAppid(Dom4jUtil.getNodeText(document, ROOT_PATH + "translate/appid"));
            configBean.setTransSecretkey(Dom4jUtil.getNodeText(document, ROOT_PATH + "translate/secretkey"));

            configBean.setFaceAppid(Dom4jUtil.getNodeText(document, ROOT_PATH + "face/appid"));
            configBean.setFaceApikey(Dom4jUtil.getNodeText(document, ROOT_PATH + "face/apikey"));
            configBean.setFaceSecretkey(Dom4jUtil.getNodeText(document, ROOT_PATH + "face/secretkey"));

            return configBean;

        }catch(Exception ex){
            logger.error("failed to execute AIConfigLoader", ex);
        }
        return null;
    }

}
