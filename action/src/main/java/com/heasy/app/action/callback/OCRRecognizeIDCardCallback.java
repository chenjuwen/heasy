package com.heasy.app.action.callback;

import com.alibaba.fastjson.JSONObject;
import com.heasy.app.baiduai.listener.OCRServiceListener;
import com.heasy.app.baiduai.service.DefaultOCRService;
import com.heasy.app.baiduai.service.OCRService;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.utils.DatetimeUtil;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.webview.PictureCropCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 身份证识别图片裁剪后的回调类
 */
public class OCRRecognizeIDCardCallback implements PictureCropCallback {
    private static Logger logger = LoggerFactory.getLogger(OCRRecognizeIDCardCallback.class);

    @Override
    public void execute(final HeasyContext heasyContext, String jsonData, final String imagePath) {
        final JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        final String cardSide = FastjsonUtil.getString(jsonObject, "cardSide");

        OCRService ocrService = heasyContext.getServiceEngine().getService(DefaultOCRService.class);
        ocrService.recognizeIDCard(imagePath, cardSide, new OCRServiceListener() {
            @Override
            public void onResult(int code, String result) {
                logger.debug("result=" + result);
                if(code == 0){
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    String image_status = jsonObject.getString("image_status");

                    if("normal".equalsIgnoreCase(image_status)){
                        if("front".equalsIgnoreCase(cardSide)) {
                            String returnStr = getFrontResult(jsonObject, cardSide, imagePath);
                            postJsonObject(heasyContext, returnStr);
                        }else{
                            String returnStr = getBackResult(jsonObject, cardSide, imagePath);
                            postJsonObject(heasyContext, returnStr);
                        }
                    }else{
                        String returnStr = FastjsonUtil.toJSONString("code", "-1", "desc", "image_status=" + image_status);
                        postJsonObject(heasyContext, returnStr);
                    }
                }else{
                    String returnStr = FastjsonUtil.toJSONString("code", "-1", "desc", result);
                    postJsonObject(heasyContext, returnStr);
                }
            }
        });

        postText(heasyContext, "start");
    }

    private String getFrontResult(JSONObject jsonObject, String cardSide, String imagePath) {
        JSONObject wordsObject = jsonObject.getJSONObject("words_result");

        JSONObject returnObject = new JSONObject();
        returnObject.put("name", wordsObject.getJSONObject("姓名").getString("words"));
        returnObject.put("sex", wordsObject.getJSONObject("性别").getString("words"));
        returnObject.put("nation", wordsObject.getJSONObject("民族").getString("words"));

        String birth = wordsObject.getJSONObject("出生").getString("words");
        birth = DatetimeUtil.changeDateFormat(birth, "yyyyMMdd", "yyyy年MM月dd日");
        returnObject.put("birth", birth);

        returnObject.put("address", wordsObject.getJSONObject("住址").getString("words"));
        returnObject.put("idNumber", wordsObject.getJSONObject("公民身份号码").getString("words"));

        returnObject.put("frontImagePath", imagePath);
        returnObject.put("cardSide", cardSide);
        returnObject.put("code", "0");
        returnObject.put("desc", "");

        String returnStr = returnObject.toJSONString();
        logger.debug(returnStr);
        return returnStr;
    }

    private String getBackResult(JSONObject jsonObject, String cardSide, String imagePath) {
        JSONObject wordsObject = jsonObject.getJSONObject("words_result");

        JSONObject returnObject = new JSONObject();
        returnObject.put("authority", wordsObject.getJSONObject("签发机关").getString("words"));

        String signDate = wordsObject.getJSONObject("签发日期").getString("words");
        signDate = DatetimeUtil.changeDateFormat(signDate, "yyyyMMdd", "yyyy-MM-dd");
        returnObject.put("signDate", signDate);

        String invalidDate = wordsObject.getJSONObject("失效日期").getString("words");
        invalidDate = DatetimeUtil.changeDateFormat(invalidDate, "yyyyMMdd", "yyyy-MM-dd");
        returnObject.put("invalidDate", invalidDate);

        returnObject.put("backImagePath", imagePath);
        returnObject.put("cardSide", cardSide);
        returnObject.put("code", "0");
        returnObject.put("desc", "");

        String returnStr = returnObject.toJSONString();
        logger.debug(returnStr);
        return returnStr;
    }

    private void postText(final HeasyContext heasyContext, String content){
        String script = "javascript: try{ actionCallback(\"" + content + "\"); }catch(e){ }";
        heasyContext.getJsInterface().loadUrl(script);
    }

    private void postJsonObject(final HeasyContext heasyContext, String content){
        String script = "javascript: try{ actionCallback(" + content + "); }catch(e){ }";
        heasyContext.getJsInterface().loadUrl(script);
    }

}
