package com.heasy.app.baiduai.service;

import com.heasy.app.baiduai.listener.SpeechCallback;
import com.heasy.app.core.service.Service;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface SpeechService extends Service {
    void start(int langPid, SpeechCallback speechCallback);
    void stop();
    void cancel();
    void exit();
}
