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
 * 驾驶证识别图片裁剪后的回调类
 */
public class OCRRecognizeDrivingLicenseCallback implements PictureCropCallback {
    private static Logger logger = LoggerFactory.getLogger(OCRRecognizeDrivingLicenseCallback.class);

    @Override
    public void execute(final HeasyContext heasyContext, String jsonData, final String imagePath) {
        OCRService ocrService = heasyContext.getServiceEngine().getService(DefaultOCRService.class);
        ocrService.recognizeDrivingLicense(imagePath, new OCRServiceListener() {
            @Override
            public void onResult(int code, String result) {
                if(code == 0){
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONObject wordsObject = jsonObject.getJSONObject("words_result");

                    JSONObject returnObject = new JSONObject();
                    returnObject.put("idNumber", wordsObject.getJSONObject("证号").getString("words"));
                    returnObject.put("name", wordsObject.getJSONObject("姓名").getString("words"));
                    returnObject.put("sex", wordsObject.getJSONObject("性别").getString("words"));
                    returnObject.put("nationality", wordsObject.getJSONObject("国籍").getString("words"));
                    returnObject.put("address", wordsObject.getJSONObject("住址").getString("words"));

                    String birth = wordsObject.getJSONObject("出生日期").getString("words");
                    birth = DatetimeUtil.changeDateFormat(birth, "yyyyMMdd", "yyyy-MM-dd");
                    returnObject.put("birth", birth);

                    String certDate = wordsObject.getJSONObject("初次领证日期").getString("words");
                    certDate = DatetimeUtil.changeDateFormat(certDate, "yyyyMMdd", "yyyy-MM-dd");
                    returnObject.put("certDate", certDate);

                    returnObject.put("cardType", wordsObject.getJSONObject("准驾车型").getString("words"));

                    String validDeadline = wordsObject.getJSONObject("有效期限").getString("words");
                    validDeadline = DatetimeUtil.changeDateFormat(validDeadline, "yyyyMMdd", "yyyy-MM-dd");
                    validDeadline += " 至 " + DatetimeUtil.changeDateFormat(wordsObject.getJSONObject("至").getString("words"), "yyyyMMdd", "yyyy-MM-dd");
                    returnObject.put("validDeadline", validDeadline);

                    returnObject.put("imagePath", imagePath);
                    returnObject.put("code", "0");
                    returnObject.put("desc", "");

                    String returnStr = returnObject.toJSONString();
                    logger.debug(returnStr);
                    postJsonObject(heasyContext, returnStr);
                }else{
                    String returnStr = FastjsonUtil.toJSONString("code", "-1", "desc", result);
                    postJsonObject(heasyContext, returnStr);
                }
            }
        });

        postText(heasyContext, "start");
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
