package com.heasy.app.action.common;

import com.alibaba.fastjson.JSONObject;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.StringUtil;
import com.heasy.app.action.ActionNames;
import com.heasy.app.core.webview.Action;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.webview.DefaultWebViewClient;

/**
 * 页面跳转
 */
@JSActionAnnotation(name = ActionNames.PageTransfer)
public class PageTransferAction implements Action {
    public static final String PROTOCOL_HTTP = "http://";
    public static final String PROTOCOL_HTTPS = "https://";

    //参数
    public static final String p_url = "url"; //页面
    public static final String p_parameters = "parameters"; //参数值，格式  key1=value1&key2=value2

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        //页面跳转前，取消倒计时
        CountDownAction.cancel();

        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        String url = FastjsonUtil.getString(jsonObject, p_url);
        String parameters = FastjsonUtil.getString(jsonObject, p_parameters);

        if(url.toLowerCase().startsWith(PROTOCOL_HTTP) || url.toLowerCase().startsWith(PROTOCOL_HTTPS)){ //公网页面地址
            heasyContext.getJsInterface().loadUrl(url);
        }else{
            heasyContext.getJsInterface().loadUrlFromAsset(url);

            //需要推送到页面的参数值
            if(StringUtil.isNotEmpty(parameters)){
                DefaultWebViewClient.addPageParameters(url, parameters);
            }
        }

        return SUCCESS;
    }

}
