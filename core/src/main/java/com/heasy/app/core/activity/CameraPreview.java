package com.heasy.app.core.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 预览类
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final Logger logger = LoggerFactory.getLogger(CameraPreview.class);
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int displayOrientation = 0;
    private int orientation = 1;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void startPreview(SurfaceHolder holder){
        try {
            mCamera.setPreviewDisplay(holder);

            initCamera();
            followScreenOrientation();

            mCamera.startPreview();
            //mCamera.cancelAutoFocus();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 设置相机属性
     */
    private void initCamera() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO); //闪光灯模式
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); //对焦模式
        mCamera.setParameters(parameters);
    }

    /**
     * 根据屏幕方向设定相机预览方向
     */
    private void followScreenOrientation(){
        //屏幕方向
        orientation = getContext().getResources().getConfiguration().orientation;
        logger.debug("orientation=" + orientation);

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) { //2 横屏
            mCamera.setDisplayOrientation(180);
            displayOrientation = 180;
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT) { //1 竖屏
            mCamera.setDisplayOrientation(90);
            displayOrientation = 90;
        }

        logger.debug("displayOrientation=" + displayOrientation);
    }

    public int getDisplayOrientation() {
        return displayOrientation;
    }

    public int getOrientation() {
        return orientation;
    }

    public void stopPreview(){
        try {
            mCamera.stopPreview();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null){
            return;
        }

        stopPreview();
        startPreview(mHolder);
    }

}
