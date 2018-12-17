package com.heasy.app.action.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.heasy.app.core.utils.ImageUtil;
import com.heasy.app.core.utils.StringUtil;
import com.heasy.app.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import rx.functions.Action1;

/**
 * 调用相机拍照
 */
@JSActionAnnotation(name = ActionNames.TakePhoto)
public class TakePhotoAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(TakePhotoAction.class);

    private Uri imageUri = null;
    private String imageFilePath = null;

    private String fileName = "";
    private String zoomWidth = "";
    private String zoomHeight = "";

    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        fileName = FastjsonUtil.getString(jsonObject, "fileName");
        zoomWidth = FastjsonUtil.getString(jsonObject, "zoomWidth");
        zoomHeight = FastjsonUtil.getString(jsonObject, "zoomHeight");

        resetOutputImageFile(heasyContext);
        takePhoto(heasyContext);

        return SUCCESS;
    }

    /**
     * 调用相机拍照
     */
    private void takePhoto(final HeasyContext heasyContext){
        //拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

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
                        if(activityBackWrapper.getResultCode() == Activity.RESULT_OK) {
                            //图片缩放
                            if(StringUtil.isNotEmpty(zoomWidth) && StringUtil.isNotEmpty(zoomHeight)) {
                                ImageUtil.zoomImage(imageFilePath, Integer.parseInt(zoomWidth), Integer.parseInt(zoomHeight), Bitmap.CompressFormat.JPEG);
                            }

                            handleCallback(heasyContext, imageFilePath);
                        }
                    }
                });
            }
        });
    }

    private void handleCallback(final HeasyContext heasyContext, String imageFilePath){
        String script = "javascript: try{ takePhotoCallback(\"" + imageFilePath + "\"); }catch(e){ }";
        heasyContext.getJsInterface().loadUrl(script);
    }

    private void resetOutputImageFile(HeasyContext heasyContext){
        if (StringUtil.isEmpty(fileName)) {
            fileName = "take_photo.jpg";
        }
        logger.debug("fileName=" + fileName);

        String rootPath = heasyContext.getServiceEngine().getConfigurationService().getConfigBean().getSdcardRootPath();
        File imageFile = new File(rootPath, fileName);

        if(!imageFile.getParentFile().exists()){
            imageFile.getParentFile().mkdirs();
        }

        if(imageFile.exists()){
            imageFile.delete();
        }

        imageUri = Uri.fromFile(imageFile);
        imageFilePath = imageUri.getPath();
    }

}
