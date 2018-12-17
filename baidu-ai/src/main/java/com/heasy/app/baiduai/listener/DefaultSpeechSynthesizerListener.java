package com.heasy.app.baiduai.listener;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.heasy.app.baiduai.listener.TTSServiceListener;

/**
 * Created by Administrator on 2018/10/28.
 */
public class DefaultSpeechSynthesizerListener implements SpeechSynthesizerListener {
    private TTSServiceListener ttsServiceListener = null;

    public DefaultSpeechSynthesizerListener(TTSServiceListener ttsServiceListener){
        this.ttsServiceListener = ttsServiceListener;
    }

    @Override
    public void onSynthesizeStart(String utteranceId) {
        if(ttsServiceListener != null){
            ttsServiceListener.onSynthesizeStart(utteranceId);
        }
    }

    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int progress) {
        if(ttsServiceListener != null){
            ttsServiceListener.onSynthesizeDataArrived(utteranceId, bytes, progress);
        }
    }

    @Override
    public void onSynthesizeFinish(String utteranceId) {
        if(ttsServiceListener != null){
            ttsServiceListener.onSynthesizeFinish(utteranceId);
        }
    }

    @Override
    public void onSpeechStart(String utteranceId) {
        if(ttsServiceListener != null){
            ttsServiceListener.onSpeechStart(utteranceId);
        }
    }

    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        if(ttsServiceListener != null){
            ttsServiceListener.onSpeechProgressChanged(utteranceId, progress);
        }
    }

    @Override
    public void onSpeechFinish(String utteranceId) {
        if(ttsServiceListener != null){
            ttsServiceListener.onSpeechFinish(utteranceId);
        }
    }

    @Override
    public void onError(String utteranceId, SpeechError speechError) {
        if(ttsServiceListener != null){
            ttsServiceListener.onError(utteranceId, speechError.code, speechError.description);
        }
    }

}
