package com.heasy.app.core.webview;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface JSInterface {
    public static final String SCRIPT_TEMPLATE = "javascript:jsBridge.executeFunction(\"%s\", '%s')";

    String dispatchAction(String actionName, String jsonData, String extend);
    void executeFunction(String funcName, String data);
    String pageTransfer(String url, String parameters);
    void goBack();
    void loadUrl(String url);
    void loadUrlFromAsset(String url);
    String getName();
}
