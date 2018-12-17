package com.heasy.app.action.common;

import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.action.ActionNames;
import com.heasy.app.core.webview.Action;

/**
 * 返回上一页
 */
@JSActionAnnotation(name = ActionNames.GoBack)
public class GoBackAction implements Action {
    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        heasyContext.getJsInterface().goBack();
        return SUCCESS;
    }
}
