package com.heasy.app.baiduai.listener;

import com.heasy.app.baiduai.RecogResult;

/**
 * Created by Administrator on 2018/11/14.
 */
public class DefaultSpeechCallback implements SpeechCallback {
    @Override
    public void onAsrReady() {

    }

    @Override
    public void onAsrBegin() {

    }

    @Override
    public void onAsrPartialResult(RecogResult recogResult) {

    }

    @Override
    public void onAsrFinalResult(RecogResult recogResult) {

    }

    @Override
    public void onAsrFinishError(int errorCode, int subErrorCode, String descMessage, RecogResult recogResult) {

    }

    @Override
    public void onAsrFinishSuccess(RecogResult recogResult) {

    }

    @Override
    public void onAsrEnd() {

    }

    @Override
    public void onAsrExit() {

    }
}
