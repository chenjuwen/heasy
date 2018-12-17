package com.heasy.app.core;

import com.heasy.app.core.service.ServiceEngine;
import com.heasy.app.core.webview.JSInterface;

/**
 * Created by Administrator on 2017/12/29.
 */
public class HeasyContext {
    private JSInterface jsInterface;
    private ServiceEngine serviceEngine;

    public JSInterface getJsInterface() {
        return jsInterface;
    }

    public void setJsInterface(JSInterface jsInterface) {
        this.jsInterface = jsInterface;
    }

    public ServiceEngine getServiceEngine() {
        return serviceEngine;
    }

    public void setServiceEngine(ServiceEngine serviceEngine) {
        this.serviceEngine = serviceEngine;
    }

}
