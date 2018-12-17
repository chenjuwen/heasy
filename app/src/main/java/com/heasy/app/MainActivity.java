package com.heasy.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.heasy.app.action.common.PageTransferAction;
import com.heasy.app.core.HeasyApplication;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.event.ExitAppEvent;
import com.heasy.app.core.service.ServiceEngineFactory;
import com.heasy.app.core.webview.WebViewWrapper;
import com.heasy.app.action.common.CountDownAction;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends AppCompatActivity{
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private WebViewWrapper webViewWrapper;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity设置属性： android:theme="@style/Theme.AppCompat.Light.NoActionBar"
        //隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //隐藏actionBar
        getSupportActionBar().hide();

        //HeasyApplication
        HeasyApplication heasyApplication = (HeasyApplication)getApplication();
        heasyApplication.addActivity(this); //后面需要用到MainActivity

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        initWebViewWrapper();

        setContentView(webViewWrapper.getWebView());
    }

    private void initWebViewWrapper(){
        HeasyContext heasyContext = ServiceEngineFactory.getServiceEngine().getHeasyContext();
        WebViewWrapperFactory.build(heasyContext);

        //webViewWrapper
        webViewWrapper = WebViewWrapperFactory.getWebViewWrapper();

        String mainPage = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getWebviewMainPage();
        if(mainPage.toLowerCase().startsWith(PageTransferAction.PROTOCOL_HTTP) || mainPage.toLowerCase().startsWith(PageTransferAction.PROTOCOL_HTTPS)){ //公网页面地址
            webViewWrapper.loadUrl(mainPage);
        }else{
            webViewWrapper.loadUrlFromAsset(mainPage);
        }
        //webViewWrapper.loadUrl("file:///sdcard/sdcard_file.html");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //后退页面，要取消倒计时
            CountDownAction.cancel();

            if(webViewWrapper.canGoBack()){
                webViewWrapper.goBack();
                return true;
            }else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    exitApp(null);
                }
                return true; //此行代码很关键
            }
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);

        HeasyApplication heasyApplication = (HeasyApplication)getApplication();
        heasyApplication.finishActivity(this);
    }

    /**
     * 退出应用
     * @param exitAppEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(ExitAppEvent exitAppEvent){
        if(exitAppEvent != null) {
            logger.debug(exitAppEvent.getSource().getClass().getName());
        }

        _destroy();
        finish();
        System.exit(0);
    }

    private void _destroy(){
        if(webViewWrapper != null) {
            webViewWrapper.destroy();
        }

        ServiceEngineFactory.getServiceEngine().close();
    }

}
