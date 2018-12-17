package com.heasy.app.core.webview;

import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.service.ServiceEngineFactory;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.StringUtil;

/**
 * Created by Administrator on 2017/12/17.
 */
public class JSInterfaceImpl implements JSInterface {
    public static final String PageTransfer = "PageTransfer";

    private String name = "heasy";
    private WebViewWrapper webViewWrapper;
    private ActionDispatcher actionDispatcher;
    private HeasyContext heasyContext;

    /**
     *   功能： 接收js传递过来的消息内容
     *    @param  actionName action的名字
     *    @param jsonData json格式的数据
     *    @param extend 扩展字段
    **/
    @android.webkit.JavascriptInterface
    @Override
    public String dispatchAction(String actionName, String jsonData, String extend){
        return actionDispatcher.dispatch(heasyContext, actionName, jsonData, extend);
    }

    /**
     * 页面跳转
     * @param url 页面
     * @param parameters 参数值，格式  key1=value1&key2=value2
     */
    @Override
    public String pageTransfer(String url, String parameters){
        String jsonData = FastjsonUtil.toJSONString("url", url, "parameters", StringUtil.trimToEmpty(parameters));
        return actionDispatcher.dispatch(heasyContext, PageTransfer, jsonData, null);
    }

    @Override
    public void goBack() {
        webViewWrapper.post(new Runnable() {
            @Override
            public void run() {
                if(webViewWrapper.canGoBack()){
                    webViewWrapper.goBack();
                }
            }
        });
    }

    /**
     *   功能： 将json数据发送给js
     *    @param  funcName action的名字
     *    @param data: 数据
     **/
    @Override
    public void executeFunction(String funcName, String data){
        String script = String.format(SCRIPT_TEMPLATE, funcName, data);
        webViewWrapper.post(script);
    }

    @Override
    public void loadUrl(String url){
        doLoad(url);
    }

    @Override
    public void loadUrlFromAsset(String url){
        String htmlLoadBasePath = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getWebviewLoadBasePath();
        doLoad(htmlLoadBasePath + url);
    }

    private void doLoad(final String url){
        webViewWrapper.post(new Runnable() {
            @Override
            public void run() {
                webViewWrapper.loadUrl(url);
            }
        });
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWebViewWrapper(WebViewWrapper webViewWrapper) {
        this.webViewWrapper = webViewWrapper;
    }

    public void setActionDispatcher(ActionDispatcher actionDispatcher) {
        this.actionDispatcher = actionDispatcher;
    }

    public void setHeasyContext(HeasyContext heasyContext) {
        this.heasyContext = heasyContext;
        this.heasyContext.setJsInterface(this);
    }

}
