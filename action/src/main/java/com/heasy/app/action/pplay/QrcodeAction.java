package com.heasy.app.action.pplay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.library.ActivityBackWrapper;
import com.example.library.RxActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.heasy.app.action.ActionNames;
import com.heasy.app.core.HeasyApplication;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.activity.ScanActivity;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.configuration.ConfigBean;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.FileUtil;
import com.heasy.app.core.utils.ImageUtil;
import com.heasy.app.core.utils.StringUtil;
import com.heasy.app.core.utils.ZxingUtil;
import com.heasy.app.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import rx.functions.Action1;

/**
 * 二维码生成和扫描
 */
@JSActionAnnotation(name = ActionNames.Qrcode)
public class QrcodeAction implements Action {
    private static Logger logger = LoggerFactory.getLogger(QrcodeAction.class);
    private String imageFileName = "";
    private String decodeContent = "";

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        if("create".equalsIgnoreCase(extend)) {
            imageFileName = FastjsonUtil.getString(jsonObject, "imageFileName");
            String content = FastjsonUtil.getString(jsonObject, "content");
            String size = FastjsonUtil.getString(jsonObject, "size");

            initImageFilePath();

            String filePath = "";
            Bitmap bitmap = ZxingUtil.createQrcode(content, Integer.parseInt(size));
            if (bitmap != null) {
                byte[] byteData = ImageUtil.bitmap2ByteArray(bitmap, Bitmap.CompressFormat.JPEG);
                if (byteData != null) {
                    String rootPath = heasyContext.getServiceEngine().getConfigurationService().getConfigBean().getSdcardRootPath();
                    filePath = rootPath + imageFileName;
                    FileUtil.writeFile(byteData, filePath);
                }
            }

            logger.debug("filePath=" + filePath);
            return filePath;

        }else if("scan".equalsIgnoreCase(extend)){
            decodeContent = FastjsonUtil.getString(jsonObject, "decodeContent");
            if(StringUtil.isEmpty(decodeContent)){
                decodeContent = "0";
            }

            scan(heasyContext);
        }

        return SUCCESS;
    }

    private void initImageFilePath(){
        if (StringUtil.isEmpty(imageFileName)) {
            imageFileName = "qrcode_image.jpg";
        }
        logger.debug("imageFileName=" + imageFileName);
    }

    private void scan(final HeasyContext heasyContext){
        HeasyApplication application = (HeasyApplication)heasyContext.getServiceEngine().getAndroidContext();
        final FragmentActivity activity = (FragmentActivity)application.getMainActivity();

        //IntentIntegrator
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setBarcodeImageEnabled(true);//是否保留扫码成功时候的截图
        integrator.setBeepEnabled(true);  //扫描成功的「哔哔」声
        integrator.setOrientationLocked(true);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setCameraId(0);
        integrator.setPrompt("请对准二维码进行扫描");

        final Intent intent = integrator.createScanIntent();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RxActivity
                    .startActivityForResult(activity, intent, IntentIntegrator.REQUEST_CODE)
                    .subscribe(new Action1<ActivityBackWrapper>() {
                        @Override
                        public void call(ActivityBackWrapper activityBackWrapper) {
                            if(activityBackWrapper.getResultCode() == Activity.RESULT_OK) {
                                //二维码内容
                                String content = "";

                                IntentResult result = IntentIntegrator.parseActivityResult(activityBackWrapper.getRequestCode(), activityBackWrapper.getResultCode(), activityBackWrapper.getIntent());
                                if(result != null) {
                                    if(StringUtil.isNotEmpty(result.getContents())) {
                                        try {
                                            if("1".equalsIgnoreCase(decodeContent)) {
                                                content = URLDecoder.decode(result.getContents(), ConfigBean.DEFAULT_CHARSET_UTF8);
                                            }else{
                                                content = result.getContents();
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                handleCallback(heasyContext, content);
                            }
                        }
                    });
            }
        });
    }

    private void handleCallback(final HeasyContext heasyContext, String content){
        String script = "javascript: try{ scanCallback(\"" + content + "\"); }catch(e){ }";
        heasyContext.getJsInterface().loadUrl(script);
    }

}
