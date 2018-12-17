package com.heasy.app.action.pplay;

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
import com.heasy.app.core.activity.CameraActivity;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.StringUtil;
import com.heasy.app.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import rx.functions.Action1;

/**
 * 调用摄像头拍照
 */
@JSActionAnnotation(name = ActionNames.Camera)
public class CameraAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(CameraAction.class);

    private Uri imageUri = null;
    private String imageFilePath = null;

    private String fileName = "";
    private String cameraId = "";

    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        fileName = FastjsonUtil.getString(jsonObject, "fileName");

        cameraId = FastjsonUtil.getString(jsonObject, "cameraId");
        if(StringUtil.isEmpty(cameraId)){
            cameraId = "0";
        }
        logger.debug("cameraId=" + cameraId);

        resetOutputImageFile(heasyContext);
        takePicture(heasyContext);

        return SUCCESS;
    }

    /**
     * 调用相机拍照
     */
    private void takePicture(final HeasyContext heasyContext){
        HeasyApplication heasyApplication = (HeasyApplication) heasyContext.getServiceEngine().getAndroidContext();
        final FragmentActivity activity = (FragmentActivity) heasyApplication.getMainActivity();

        //拍照
        Intent intent = new Intent();
        intent.setClass(activity, CameraActivity.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
        intent.putExtra("cameraId", Integer.parseInt(cameraId));

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
            fileName = "camera_image.jpg";
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
