package com.heasy.app.core.configuration;

import android.content.Context;

import com.heasy.app.core.utils.Dom4jUtil;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigLoader<T> implements ConfigLoader<T> {
    private static Logger logger = LoggerFactory.getLogger(AbstractConfigLoader.class);

    @Override
    public T load(String filepath) {
        try {
            logger.debug("Load config file from path: " + filepath);
            Document document = Dom4jUtil.read(filepath);
            return parseConfigFile(document);
        }catch(Exception ex){
            logger.error("", ex);
        }
        return null;
    }

    @Override
    public T loadFromAssets(Context context, String filepath) {
        try {
            logger.debug("Load config file from assets path: " + filepath);
            Document document = Dom4jUtil.readFromAssets(context, filepath);
            return parseConfigFile(document);
        }catch(Exception ex){
            logger.error("", ex);
        }
        return null;
    }

    protected abstract T parseConfigFile(Document document);

}
