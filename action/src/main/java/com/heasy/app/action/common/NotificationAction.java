package com.heasy.app.action.common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.heasy.app.action.ActionNames;
import com.heasy.app.action.NotificationReceiver;
import com.heasy.app.core.HeasyApplication;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.webview.Action;
import com.heasy.app.webview.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Android通知
 */
@JSActionAnnotation(name = ActionNames.Notification)
public class NotificationAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(NotificationAction.class);

    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        String title = FastjsonUtil.getString(jsonObject, "title");
        String content = FastjsonUtil.getString(jsonObject, "content");
        String data = FastjsonUtil.getString(jsonObject, "data");

        Context context = heasyContext.getServiceEngine().getAndroidContext();

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true) //点击后自动消失
                .setWhen(System.currentTimeMillis());

        Intent notifyIntent = new Intent(context, NotificationReceiver.class);
        notifyIntent.putExtra("data", data);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);

        HeasyApplication application = (HeasyApplication)context;
        Activity activity = application.getMainActivity();
        NotificationManager notifyManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.notify(999, builder.build());

        return SUCCESS;
    }

}
