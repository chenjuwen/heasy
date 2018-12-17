package com.heasy.app.action.pplay;

import com.alibaba.fastjson.JSONObject;
import com.heasy.app.action.ActionNames;
import com.heasy.app.baiduai.api.FaceAPI;
import com.heasy.app.baiduai.config.AIConfigBean;
import com.heasy.app.baiduai.config.AIConfigFactory;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 人脸识别
 */
@JSActionAnnotation(name = ActionNames.Face)
public class FaceAction implements Action {
    private static Logger logger = LoggerFactory.getLogger(FaceAction.class);
    private Thread verifyThread;

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        String imageFilePath = FastjsonUtil.getString(jsonObject, "imageFilePath");

        if("detect".equalsIgnoreCase(extend)){
            AIConfigBean configBean = AIConfigFactory.getAIConfigBean(heasyContext.getServiceEngine().getAndroidContext());
            FaceAPI faceAPI = new FaceAPI(configBean.getFaceAppid(), configBean.getFaceApikey(), configBean.getFaceSecretkey());
            String result = faceAPI.detect(imageFilePath);
            return result;

        }else{
            verifyThread = new VerifyThread(heasyContext, imageFilePath );
            verifyThread.start();
        }

        return SUCCESS;
    }


    class VerifyThread extends Thread{
        private HeasyContext heasyContext;
        private String imageFilePath;

        public VerifyThread(HeasyContext heasyContext, String imageFilePath){
            this.heasyContext = heasyContext;
            this.imageFilePath = imageFilePath;
        }

        @Override
        public void run() {
            String result = faceVerify(heasyContext, imageFilePath);
            handleCallback(heasyContext, result);
        }

        //在线活体检测
        private String faceVerify(HeasyContext heasyContext, String imageFilePath){
            AIConfigBean configBean = AIConfigFactory.getAIConfigBean(heasyContext.getServiceEngine().getAndroidContext());

            FaceAPI faceAPI = new FaceAPI(configBean.getFaceAppid(), configBean.getFaceApikey(), configBean.getFaceSecretkey());
            String result = faceAPI.verify(imageFilePath);
            return result;
        }

        private void handleCallback(final HeasyContext heasyContext, String jsonData){
            String script = "javascript: try{ faceVerifyCallback(" + jsonData + "); }catch(e){ }";
            heasyContext.getJsInterface().loadUrl(script);
        }
    }

}
