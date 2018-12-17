package com.heasy.app.baiduai.service;

import com.heasy.app.baiduai.listener.TTSServiceListener;
import com.heasy.app.baiduai.listener.TTSSynthesizeFinishCallback;
import com.heasy.app.core.service.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface TTSService extends Service {
    int speak(String text, TTSSynthesizeFinishCallback callback);
    int batchSpeak(List<String> textList);
    int synthesize(String text, TTSSynthesizeFinishCallback callback);
    int resume();
    int pause();
    int stop();
    void setTTSServiceListener(TTSServiceListener ttsServiceListener);
}
