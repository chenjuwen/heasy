package com.heasy.app.core.webview;

import com.heasy.app.core.configuration.ComponentScanner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/21.
 */
public abstract class AbstractActionDispatcher implements ActionDispatcher {
    private ComponentScanner<Map<String, Action>> actionScanner;
    private Map<String, Action> actionMap = new HashMap<String, Action>();

    @Override
    public void init() {
        if(actionScanner != null){
            actionMap = actionScanner.scan();
        }
    }

    public Action getAction(String actionName) {
        if(actionMap.containsKey(actionName)){
            return actionMap.get(actionName);
        }
        return null;
    }

    public void setActionScanner(ComponentScanner<Map<String, Action>> actionScanner) {
        this.actionScanner = actionScanner;
    }

}
