package com.heasy.app.action;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 点击消息通知后的回调处理类
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(NotificationReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.debug("notification receiver...");
        String data = intent.getStringExtra("data");
        logger.debug("data=" + data);
    }

}
