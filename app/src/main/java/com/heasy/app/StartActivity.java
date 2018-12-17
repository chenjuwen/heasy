package com.heasy.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.service.ServiceEngineFactory;
import com.heasy.app.core.utils.VersionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class StartActivity extends AppCompatActivity {
    private static final Logger logger = LoggerFactory.getLogger(StartActivity.class);
    private Thread thread = null;
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logger.debug("versionCode: " + VersionUtil.getVersionCode(getApplicationContext()));
        logger.debug("versionName: " + VersionUtil.getVersionName(getApplicationContext()));

        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_start);

        //隐藏actionBar
        getSupportActionBar().hide();

        TextView textView = (TextView)findViewById(R.id.versionName);
        textView.setText("版本号：" + VersionUtil.getVersionName(getApplicationContext()));

        handler = new Handler(){
            @Override
            public void handleMessage(Message message) {
                if(message.what == 1) {
                    logger.debug("start MainActivity...");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                }
            }
        };

        thread = new MainThread();
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(thread != null){
            thread.interrupt();
            thread = null;
        }
    }

    class MainThread extends Thread{
        @Override
        public void run() {
            try {
                openServiceEngine();
                initActionDispatcher();

                //TimeUnit.MILLISECONDS.sleep(1500);

                handler.sendEmptyMessage(1);

            } catch (Exception ex) {
                logger.error("", ex);
            }
        }

        private void openServiceEngine(){
            logger.debug("open ServiceEngine...");

            ServiceEngineFactory.setServiceEngine(new ServiceEngineImpl());

            ServiceEngineFactory.getServiceEngine().setAndroidContext(getApplicationContext());
            ServiceEngineFactory.getServiceEngine().setHeasyContext(new HeasyContext());
            ServiceEngineFactory.getServiceEngine().open();
        }

        /**
         * 扫描加载Action类
         */
        private void initActionDispatcher(){
            HeasyContext heasyContext = ServiceEngineFactory.getServiceEngine().getHeasyContext();
            String actionBasePackages = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getActionBasePackage();
            WebViewWrapperFactory.initActionDispatcher(heasyContext, actionBasePackages);
        }

    }

}
