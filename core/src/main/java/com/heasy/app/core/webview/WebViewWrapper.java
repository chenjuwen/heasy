package com.heasy.app.core.webview;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.heasy.app.core.service.ServiceEngineFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/12/17.
 */
public class WebViewWrapper {
    private static final Logger logger = LoggerFactory.getLogger(WebViewWrapper.class);
    private WebView webView;

    private WebViewWrapper(WebView webView){
        this.webView = webView;
    }

    public WebView getWebView() {
        return webView;
    }

    public void loadUrl(String url){
        webView.loadUrl(url);
    }

    public void loadUrlFromAsset(String url){
        String htmlLoadBasePath = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getWebviewLoadBasePath();
        webView.loadUrl(htmlLoadBasePath + url);
    }

    public void post(Runnable action){
        webView.post(action);
    }

    public void post(final String script){
        webView.post(new Runnable() {
            @Override
            public void run() {
                logger.debug("<< " + script);

                if(Build.VERSION.SDK_INT >= 19){ //Android4.4
                    webView.evaluateJavascript(script, null);
                }else{
                    webView.loadUrl(script);
                }
            }
        });
    }

    public boolean canGoBack(){
        return webView.canGoBack();
    }

    public void goBack(){
        if(webView.canGoBack()) {
            webView.goBack();
        }
    }

    public void destroy(){
        webView.loadDataWithBaseURL(null,"","text/html","utf-8", null);
        webView.setVisibility(View.GONE);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.stopLoading();
        webView.clearHistory();
        webView.clearCache(true);
        webView.clearFormData();
        webView.removeAllViews();
        webView.loadUrl("about:blank");
        webView.destroy();
    }

    public static class Builder{
        private WebView webView;
        private WebViewClient webViewClient;
        private JSInterface jsInterface;

        public Builder(Context context){
            this.webView = new WebView(context);
        }

        public WebViewWrapper build(){
            initWebSettings(webView);

            WebViewWrapper webViewWrapper = new WebViewWrapper(webView);

            ((JSInterfaceImpl)jsInterface).setWebViewWrapper(webViewWrapper);

            return webViewWrapper;
        }

        private void initWebSettings(WebView webView){
            WebSettings settings = webView.getSettings();

            settings.setDefaultTextEncodingName("utf-8");
            settings.setJavaScriptEnabled(true);  //支持javascript
            settings.setDomStorageEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);  //不使用缓存

            settings.setUseWideViewPort(false); //将图片调整到适合webview的大小
            settings.setLoadWithOverviewMode(true); //缩放至屏幕的大小

            //不常用设置
            settings.setSupportZoom(false);  //支持缩放
            settings.setBuiltInZoomControls(false); //显示缩放控制按钮
            //settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL); //支持内容重新布局
            //settings.supportMultipleWindows();  //多窗口
            settings.setAllowFileAccess(true);  //设置可以访问文件
            //settings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点

            logger.debug("SDK VERSION: " + Build.VERSION.SDK_INT);
            if(Build.VERSION.SDK_INT >= 19){
                settings.setLoadsImagesAutomatically(true);  //支持自动加载图片
            }else{
                settings.setLoadsImagesAutomatically(false);
            }

            settings.setMediaPlaybackRequiresUserGesture(false);
        }

        public Builder setWebViewClient(WebViewClient webViewClient){
            this.webViewClient = webViewClient;
            webView.setWebViewClient(webViewClient);
            return this;
        }

        public Builder setWebChromeClient(WebChromeClient webChromeClient){
            webView.setWebChromeClient(webChromeClient);
            return this;
        }

        public Builder setJSInterface(JSInterface jsInterface){
            this.jsInterface = jsInterface;
            webView.addJavascriptInterface((JSInterfaceImpl)jsInterface, jsInterface.getName());
            return this;
        }
    }

}
