package com.heasy.app.core.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heasy.app.core.R;
import com.heasy.app.core.service.ServiceEngineFactory;
import com.heasy.app.core.utils.FileUtil;
import com.heasy.app.core.utils.ImageUtil;
import com.heasy.app.core.utils.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private static final Logger logger = LoggerFactory.getLogger(CameraActivity.class);
    private Camera mCamera;
    private CameraPreview mPreview;
    private String filePath = "";
    private int cameraId = 0; //0后置， 1前置
    private byte[] data = null;

    private TextView messageContainer;
    private Button recaptureButton;
    private Button captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //隐藏actionBar
        getSupportActionBar().hide();

        messageContainer = (TextView)findViewById(R.id.messageContainer);
        messageContainer.setText("");

        //file path
        filePath = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
        logger.debug("filePath=" + filePath);

        cameraId = getIntent().getIntExtra("cameraId", 0);

        if(StringUtil.isEmpty(filePath)){
            String rootPath = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getSdcardRootPath();
            filePath = rootPath + "camera_image.jpg";
        }

        //默认打开前置摄像头
        openCamera(cameraId);

        //取消
        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //重拍
        recaptureButton = (Button) findViewById(R.id.button_recapture);
        recaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureButton.setVisibility(View.VISIBLE);
                recaptureButton.setVisibility(View.GONE);
                mPreview.startPreview(mPreview.getHolder());
            }
        });
        recaptureButton.setVisibility(View.GONE);

        //拍照
        captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        mCamera.takePicture(null, null, new TakePictureCallback());
                        captureButton.setVisibility(View.GONE);
                        recaptureButton.setVisibility(View.VISIBLE);
                    }
                });
                */

                mCamera.takePicture(null, null, new TakePictureCallback());
                captureButton.setVisibility(View.GONE);
                recaptureButton.setVisibility(View.VISIBLE);
            }
        });

        //确定
        Button confirmButton = (Button) findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data != null) {
                    FileUtil.writeFile(data, filePath);

                    //旋转图片
                    Bitmap bitmap = ImageUtil.rotateImage(ImageUtil.readFromPath(filePath), mPreview.getDisplayOrientation());
                    FileUtil.writeFile(ImageUtil.bitmap2ByteArray(bitmap, Bitmap.CompressFormat.JPEG), filePath);

                    if(mPreview.getOrientation() == Configuration.ORIENTATION_PORTRAIT) { //1 竖屏
                        if (cameraId == 1) { //前置
                            //垂直翻转
                            bitmap = ImageUtil.vFlipImage(ImageUtil.readFromPath(filePath));
                            FileUtil.writeFile(ImageUtil.bitmap2ByteArray(bitmap, Bitmap.CompressFormat.JPEG), filePath);
                        }
                    }else if(mPreview.getOrientation() == Configuration.ORIENTATION_LANDSCAPE){ //2 横屏

                    }

                    setResult(Activity.RESULT_OK, new Intent());
                    finish();

                }else{
                    Toast.makeText(CameraActivity.this,"请先拍照！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 打开摄像头并预览
     */
    private void openCamera(int cameraId) {
        logger.debug("camera num: " + Camera.getNumberOfCameras());

        //0 后摄像头， 1 前摄像头
        mCamera = Camera.open(cameraId);

        Camera.Parameters params = mCamera.getParameters();
        Camera.Size pictureSize = params.getPictureSize();
        BigDecimal pictureWH = new BigDecimal(pictureSize.width).divide(new BigDecimal(pictureSize.height), 2, BigDecimal.ROUND_HALF_UP);
        logger.debug("width=" + pictureSize.width + ", height=" + pictureSize.height + ", pictureWH=" + pictureWH );

        Camera.Size previewSize = params.getPreviewSize();
        BigDecimal previewWH = new BigDecimal(previewSize.width).divide(new BigDecimal(previewSize.height), 2, BigDecimal.ROUND_HALF_UP);
        logger.debug("width=" + previewSize.width + ", height=" + previewSize.height + ", previewWH=" + previewWH );

        //new picture size
        Camera.Size newSize = pictureSize;
        List<Camera.Size> pictureSizes = mCamera.getParameters().getSupportedPictureSizes();
        for (int i=0; i<pictureSizes.size(); i++) {
            Camera.Size pSize = pictureSizes.get(i);
            BigDecimal pWH = new BigDecimal(pSize.width).divide(new BigDecimal(pSize.height), 2, BigDecimal.ROUND_HALF_UP);
            //logger.debug("1 width=" + pSize.width + ", height=" + pSize.height + ", wh=" + pWH);

            if(pSize.width > previewSize.width && pictureWH.equals(pWH)){
                newSize = pSize;
            }
        }

        params.setPictureSize(newSize.width, newSize.height);
        mCamera.setParameters(params);

        /*
        List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        for (int i=0; i<previewSizes.size(); i++) {
            Camera.Size pSize = previewSizes.get(i);
            logger.debug("2 width=" + pSize.width + ", height=" + pSize.height + ", wh=" + (new BigDecimal(pSize.width).divide(new BigDecimal(pSize.height), 2, BigDecimal.ROUND_HALF_UP)) );
        }
        */

        mPreview = new CameraPreview(this, mCamera);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    private void closeCamera(){
        if(mPreview != null) {
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.removeView(mPreview);
            mPreview = null;
        }

        if(mCamera != null){
            mCamera.setPreviewCallback(null) ;
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 是否有摄像头设备
     */
    private boolean hasCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        closeCamera();
        super.onDestroy();
    }

    /**
     * 拍照后的回调函数
     */
    class TakePictureCallback implements Camera.PictureCallback{
        @Override
        public void onPictureTaken(byte[] return_data, Camera camera) {
            data = return_data;
        }
    }

}
