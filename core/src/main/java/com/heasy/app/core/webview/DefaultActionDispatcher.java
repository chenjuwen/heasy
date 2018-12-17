package com.heasy.app.core.webview;

import com.heasy.app.core.HeasyContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/12/20.
 */
public class DefaultActionDispatcher extends AbstractActionDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DefaultActionDispatcher.class);

    @Override
    public String dispatch(HeasyContext heasyContext, String actionName, String jsonData, String extend) {
        logger.debug(">> actionName=" + actionName + ", jsonData=" + jsonData + ", extend=" + extend);

        Action action = getAction(actionName);
        if(action != null){
            return action.execute(heasyContext, jsonData, extend);
        }else{
            logger.error("actionName[" + actionName + "] not found!");
            return null;
        }
    }

}
