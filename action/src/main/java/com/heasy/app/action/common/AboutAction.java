package com.heasy.app.action.common;

import com.heasy.app.action.ActionNames;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.VersionUtil;
import com.heasy.app.core.webview.Action;

/**
 * 关于
 */
@JSActionAnnotation(name = ActionNames.About)
public class AboutAction implements Action {
    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        String versionName = VersionUtil.getVersionName(heasyContext.getServiceEngine().getAndroidContext());
        return versionName;
    }

}
