package com.heasy.app.action.common;

import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.event.ExitAppEvent;
import com.heasy.app.core.webview.Action;
import com.heasy.app.action.ActionNames;

/**
 * 退出应用
 */
@JSActionAnnotation(name = ActionNames.ExitApp)
public class ExitAppAction implements Action {
    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        heasyContext.getServiceEngine().getEventService().postEvent(new ExitAppEvent(this));
        return SUCCESS;
    }

}
