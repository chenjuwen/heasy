package com.heasy.app.action.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.library.ActivityBackWrapper;
import com.example.library.RxActivity;
import com.heasy.app.action.ActionNames;
import com.heasy.app.core.HeasyApplication;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.StringUtil;
import com.heasy.app.core.webview.Action;
import com.heasy.app.core.webview.PictureCropCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import rx.functions.Action1;

/**
 * 从相册选择图片并裁剪
 */
@JSActionAnnotation(name = ActionNames.PictureCrop)
public class PictureCropAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(PictureCropAction.class);

    private String fileName = "";
    private Uri imageUri = null;
    private String callbackClass = "";

    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        fileName = FastjsonUtil.getString(jsonObject, "fileName");
        callbackClass = FastjsonUtil.getString(jsonObject, "callback");

        resetOutputImageFile(heasyContext);

        openPicture(heasyContext, jsonData);

        return SUCCESS;
    }

    private void openPicture(final HeasyContext heasyContext, final String jsonData){
        //打开相册
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");

        HeasyApplication heasyApplication = (HeasyApplication) heasyContext.getServiceEngine().getAndroidContext();
        final FragmentActivity activity = (FragmentActivity) heasyApplication.getMainActivity();
        final Intent tmpIntent = intent;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RxActivity
                    .startActivityForResult(activity, tmpIntent, 1)
                    .subscribe(new Action1<ActivityBackWrapper>() {
                        @Override
                        public void call(ActivityBackWrapper activityBackWrapper) {
                            logger.debug("scheme=" + activityBackWrapper.getIntent().getData().getScheme());
                            logger.debug("path=" + activityBackWrapper.getIntent().getData().getPath());

                            if(activityBackWrapper.getResultCode() == Activity.RESULT_OK) {
                                Intent intent = new Intent("com.android.camera.action.CROP");
                                intent.setDataAndType(activityBackWrapper.getIntent().getData(), "image/*");
                                intent.putExtra("scale", true);
                                intent.putExtra("scaleUpIfNeeded", true);// 去黑边
                                intent.putExtra("crop", true);
                                intent.putExtra("return-data", false);
                                intent.putExtra("noFaceDetection", true);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                                RxActivity
                                    .startActivityForResult(activity, intent, 3)
                                    .subscribe(new Action1<ActivityBackWrapper>() {
                                        @Override
                                        public void call(ActivityBackWrapper activityBackWrapper) {
                                            cropCallback(heasyContext, jsonData, activityBackWrapper);
                                        }
                                    });
                            }
                        }
                    });
            }
        });
    }

    /**
     * 图片裁剪完后的回调函数
     */
    private void cropCallback(HeasyContext heasyContext, String jsonData, ActivityBackWrapper activityBackWrapper){
        if(activityBackWrapper.getResultCode() == Activity.RESULT_OK){
            try {
                if(StringUtil.isNotEmpty(callbackClass)){
                    PictureCropCallback cb = (PictureCropCallback)Class.forName(callbackClass).newInstance();
                    cb.execute(heasyContext, jsonData, imageUri.getPath());
                }
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

    private void resetOutputImageFile(HeasyContext heasyContext){
        if(StringUtil.isEmpty(fileName)){
            fileName = "picture_crop.jpg";
        }
        logger.debug("fileName=" + fileName);

        String rootPath = heasyContext.getServiceEngine().getConfigurationService().getConfigBean().getSdcardRootPath();
        File outputImage = new File(rootPath, fileName);

        if(!outputImage.getParentFile().exists()){
            outputImage.getParentFile().mkdirs();
        }

        if(outputImage.exists()){
            outputImage.delete();
        }

        imageUri = Uri.fromFile(outputImage);
    }

}
