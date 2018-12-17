package com.heasy.app.baiduai.listener;

import com.heasy.app.baiduai.RecogResult;

/**
 * 语音识别回调接口
 */
public interface SpeechCallback {
    /**
     * ASR_START 输入事件调用后，引擎准备完毕
     */
    void onAsrReady();

    /**
     * onAsrReady后检查到用户开始说话
     */
    void onAsrBegin();

    /**
     * resultType=partial_result
     * onAsrBegin 后 随着用户的说话，返回的临时结果
     *
     * @param recogResult 完整的结果
     */
    void onAsrPartialResult(RecogResult recogResult);

    /**
     * resultType=final_result
     * 最终的识别结果
     *
     * @param recogResult 完整的结果
     */
    void onAsrFinalResult(RecogResult recogResult);

    /**
     * error!=0
     */
    void onAsrFinishError(int errorCode, int subErrorCode, String descMessage, RecogResult recogResult);

    /**
     * @param recogResult 完整的结果
     */
    void onAsrFinishSuccess(RecogResult recogResult);

    /**
     * 检查到用户开始说话停止，或者ASR_STOP 输入事件调用后，
     */
    void onAsrEnd();

    /**
     * 引擎完成整个识别，空闲中
     */
    void onAsrExit();

}
