package com.heasy.app.action.pplay;

import com.alibaba.fastjson.JSONObject;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.action.ActionNames;
import com.heasy.app.core.webview.PictureCropCallback;
import com.heasy.app.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OCR图片文字识别
 */
@JSActionAnnotation(name = ActionNames.OCR)
public class OCRAction implements Action {
    private static Logger logger = LoggerFactory.getLogger(OCRAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        if("recognize".equalsIgnoreCase(extend)){ //重新识别图片
            String callbackClass = FastjsonUtil.getString(jsonObject, "callback");
            String imagePath = FastjsonUtil.getString(jsonObject, "imagePath");

            try {
                PictureCropCallback cb = (PictureCropCallback)Class.forName(callbackClass).newInstance();
                cb.execute(heasyContext, jsonData, imagePath);
            } catch (Exception ex) {
                logger.error("", ex);
            }

        }else if("recognizeIdcard".equalsIgnoreCase(extend)){ //身份证重新识别
            String callbackClass = FastjsonUtil.getString(jsonObject, "callback");
            String imagePath = FastjsonUtil.getString(jsonObject, "imagePath");

            try {
                PictureCropCallback cb = (PictureCropCallback)Class.forName(callbackClass).newInstance();
                cb.execute(heasyContext, jsonData, imagePath);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }else if("recognizeDrivingLicense".equalsIgnoreCase(extend)){ //驾驶证重新识别
            String callbackClass = FastjsonUtil.getString(jsonObject, "callback");
            String imagePath = FastjsonUtil.getString(jsonObject, "imagePath");

            try {
                PictureCropCallback cb = (PictureCropCallback)Class.forName(callbackClass).newInstance();
                cb.execute(heasyContext, jsonData, imagePath);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }

        return SUCCESS;
    }
}
