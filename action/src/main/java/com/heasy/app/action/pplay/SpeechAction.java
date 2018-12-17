package com.heasy.app.action.pplay;

import com.alibaba.fastjson.JSONObject;
import com.heasy.app.baiduai.RecogResult;
import com.heasy.app.baiduai.listener.DefaultSpeechCallback;
import com.heasy.app.baiduai.listener.SpeechCallback;
import com.heasy.app.baiduai.service.DefaultSpeechService;
import com.heasy.app.baiduai.service.SpeechService;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.webview.Action;
import com.heasy.app.action.ActionNames;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Speech 语音识别
 */
@JSActionAnnotation(name = ActionNames.Speech)
public class SpeechAction implements Action {
    private static Logger logger = LoggerFactory.getLogger(SpeechAction.class);

    private SpeechService speechService = null;
    private SpeechCallback speechCallback = null;
    private boolean doing = false; //是否正在识别

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        if(speechService == null) {
            speechService = heasyContext.getServiceEngine().getService(DefaultSpeechService.class);
            speechCallback = new MYSpeechCallback(heasyContext);
        }

        if("start".equalsIgnoreCase(extend)){
            JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
            String lang = FastjsonUtil.getString(jsonObject, "lang");
            logger.debug("lang=" + lang);

            int langPid = 1537; //普通话
            if("粤语".equalsIgnoreCase(lang)){
                langPid = 1637;
            }else if("英语".equalsIgnoreCase(lang)){
                langPid = 1737;
            }

            if(!doing) {
                speechService.start(langPid, speechCallback);
                doing = true;
            }

        }else if("stop".equalsIgnoreCase(extend)){
            doing = false;
            speechService.stop();
        }

        return SUCCESS;
    }


    /**
     * 识别回调类
     */
    class MYSpeechCallback extends DefaultSpeechCallback {
        private HeasyContext heasyContext;

        public MYSpeechCallback(HeasyContext heasyContext){
            this.heasyContext = heasyContext;
        }

        private void postMessage(String content){
            String script = "javascript: try{ speechCallback(\"" + content + "\"); }catch(e){ }";
            heasyContext.getJsInterface().loadUrl(script);
        }

        @Override
        public void onAsrReady() {
            postMessage("开始识别");
        }

        @Override
        public void onAsrFinalResult(RecogResult recogResult) {
            doing = false;
            postMessage(recogResult.getResultsRecognition());
        }

        @Override
        public void onAsrFinishError(int errorCode, int subErrorCode, String descMessage, RecogResult recogResult) {
            doing = false;
            postMessage("识别出错: errorCode=" + errorCode + ", subErrorCode=" + subErrorCode + ", desc=" + descMessage);
        }

        @Override
        public void onAsrExit() {
            doing = false;
            postMessage("识别结束");
            speechService.stop();
        }
    }

}
