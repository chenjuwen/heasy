package com.heasy.app.core.webview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.heasy.app.core.HeasyApplication;
import com.heasy.app.core.service.ServiceEngineFactory;

/**
 * Created by Administrator on 2017/12/17.
 */
public class DefaultWebChromeClient extends WebChromeClient {
    /**
     * 显示Alert对话框
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        HeasyApplication heasyApplication = (HeasyApplication) ServiceEngineFactory.getServiceEngine().getAndroidContext();
        Activity activity = heasyApplication.getMainActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        HeasyApplication heasyApplication = (HeasyApplication) ServiceEngineFactory.getServiceEngine().getAndroidContext();
        Activity activity = heasyApplication.getMainActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                result.cancel();
            }
        });

        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });

        builder.setCancelable(false);
        builder.create().show();
        return true;
    }

}
