package com.heasy.app.baiduai.listener;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.heasy.app.baiduai.RecogResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2018/11/13.
 */
public class DefaultEventListener implements EventListener {
    private static Logger logger = LoggerFactory.getLogger(DefaultEventListener.class);
    private SpeechCallback callback = null;

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        logger.debug("name=" + name + ", params=" + params);

        if(callback != null){
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                // 引擎准备就绪，可以开始说话
                callback.onAsrReady();

            }else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
                // 检测到用户已经开始说话
                callback.onAsrBegin();

            }else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                RecogResult recogResult = RecogResult.parseJson(params);
                // 临时识别结果, 长语音模式需要从此消息中取出结果
                if (recogResult.isFinalResult()) {
                    callback.onAsrFinalResult(recogResult);
                } else if (recogResult.isPartialResult()) {
                    callback.onAsrPartialResult(recogResult);
                }

            }else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                // 识别结束， 最终识别结果或可能的错误
                RecogResult recogResult = RecogResult.parseJson(params);
                if (recogResult.hasError()) {
                    int errorCode = recogResult.getError();
                    int subErrorCode = recogResult.getSubError();
                    callback.onAsrFinishError(errorCode, subErrorCode, recogResult.getDesc(), recogResult);
                } else {
                    callback.onAsrFinishSuccess(recogResult);
                }

            }else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
                // 检测到用户已经停止说话
                callback.onAsrEnd();

            }else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
                callback.onAsrExit();
            }
        }
    }

    public SpeechCallback getCallback() {
        return callback;
    }

    public void setCallback(SpeechCallback callback) {
        this.callback = callback;
    }
}
