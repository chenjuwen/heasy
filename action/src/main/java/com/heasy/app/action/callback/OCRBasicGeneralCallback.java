package com.heasy.app.action.callback;

import com.heasy.app.baiduai.listener.OCRServiceListener;
import com.heasy.app.baiduai.service.DefaultOCRService;
import com.heasy.app.baiduai.service.OCRService;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.webview.PictureCropCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文字识别图片裁剪后的回调类
 */
public class OCRBasicGeneralCallback implements PictureCropCallback {
    private static Logger logger = LoggerFactory.getLogger(OCRBasicGeneralCallback.class);

    @Override
    public void execute(final HeasyContext heasyContext, String jsonData, String imagePath) {
        OCRService ocrService = heasyContext.getServiceEngine().getService(DefaultOCRService.class);
        ocrService.generalBasic(imagePath, new OCRServiceListener() {
            @Override
            public void onResult(int code, String result) {
                logger.debug("result=" + result);
                resultCallback(heasyContext, result);
            }
        });

        startCallback(heasyContext, imagePath);
    }

    private void startCallback(final HeasyContext heasyContext, String imagePath){
        String script = "javascript: try{ startCallback(\"" + imagePath + "\"); }catch(e){ }";
        heasyContext.getJsInterface().loadUrl(script);
    }

    private void resultCallback(final HeasyContext heasyContext, String content){
        String script = "javascript: try{ resultCallback(\"" + content + "\"); }catch(e){ }";
        heasyContext.getJsInterface().loadUrl(script);
    }

}
