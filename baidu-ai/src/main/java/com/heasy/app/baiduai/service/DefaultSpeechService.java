package com.heasy.app.baiduai.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.heasy.app.baiduai.config.AIConfigBean;
import com.heasy.app.baiduai.config.AIConfigFactory;
import com.heasy.app.baiduai.listener.DefaultEventListener;
import com.heasy.app.baiduai.listener.SpeechCallback;
import com.heasy.app.core.service.AbstractService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/13.
 */
public class DefaultSpeechService extends AbstractService implements SpeechService {
    private static Logger logger = LoggerFactory.getLogger(DefaultSpeechService.class);

    private EventManager eventManager = null;
    private DefaultEventListener eventListener = null;

    @Override
    public void init() {
        eventListener = new DefaultEventListener();
        eventManager = EventManagerFactory.create(getHeasyContext().getServiceEngine().getAndroidContext(), "asr");
        eventManager.registerListener(eventListener);

        successInit = true;
    }

    @Override
    public boolean isInit() {
        return successInit;
    }

    @Override
    public void unInit() {
        exit();
        successInit = false;
    }

    @Override
    public void start(int langPid, SpeechCallback speechCallback) {
        if(eventManager != null) {
            eventListener.setCallback(speechCallback);

            AIConfigBean configBean = AIConfigFactory.getAIConfigBean(getHeasyContext().getServiceEngine().getAndroidContext());

            //1.2
            Map<String, Object> params = new HashMap<>();

            //1537	普通话	输入法模型	有标点（逗号）
            //1637	粤语		有标点（逗号）
            //1737	英语		有标点（逗号）

            params.put(SpeechConstant.PID, langPid); //识别语种
            params.put(SpeechConstant.DECODER, 0); //纯在线
            params.put(SpeechConstant.VAD, "dnn"); //语音活动检测
            params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 1000); //静音超时断句及长语音
            params.put(SpeechConstant.APP_ID, configBean.getAsrAppid());
            params.put(SpeechConstant.APP_KEY, configBean.getAsrApikey());
            params.put(SpeechConstant.SECRET, configBean.getAsrSecretkey());
            params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false); //是否需要语音音量数据回调
            params.put(SpeechConstant.DISABLE_PUNCTUATION, true); //在线识别，禁用标点

            String json = JSONObject.toJSONString(params);
            eventManager.send(SpeechConstant.ASR_START, json, null, 0, 0);
        }
    }

    @Override
    public void stop() {
        if(eventManager != null) {
            eventManager.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
        }
    }

    @Override
    public void cancel() {
        if(eventManager != null) {
            eventManager.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        }
    }

    @Override
    public void exit() {
        if(eventManager != null) {
            logger.debug("stop EventManager");
            cancel();
            eventManager.unregisterListener(eventListener);
            eventManager = null;
            eventListener = null;
        }
    }

}
