package com.heasy.app.core.webview;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.heasy.app.core.service.ServiceEngineFactory;
import com.heasy.app.core.utils.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/12/17.
 */
public class DefaultWebViewClient extends WebViewClient {
    private static final Logger logger = LoggerFactory.getLogger(DefaultWebViewClient.class);
    private static ConcurrentHashMap<String, String> pageParameters = new ConcurrentHashMap<String, String>(); //用于存放页面跳转时附带的参数信息

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        logger.debug("访问URL： " + url);

        String htmlLoadBasePath = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getWebviewLoadBasePath();

        //将页面参数值推送到页面
        String parameters = "";
        String pageURL = url.replace(htmlLoadBasePath, "");

        if(pageParameters.containsKey(pageURL)){
            parameters = pageParameters.get(pageURL);
            pageParameters.remove(pageURL);
        }

        //页面参数值解析
        if(StringUtil.isNotEmpty(parameters)){
            logger.debug("页面参数: " + parameters);
            webView.loadUrl("javascript: try{ parsePageParameters(\"" + parameters + "\"); pageFinishCallback(); }catch(e){ }");
        }else{
            webView.loadUrl("javascript: try{ pageFinishCallback(); }catch(e){ }");
        }
    }

    public static void addPageParameters(String pageURL, String parameters){
        logger.debug("页面参数添加到pageParameters集合：pageURL=" + pageURL + ", parameters=" + parameters);
        pageParameters.put(pageURL, parameters);
    }

}
