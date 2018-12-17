package com.heasy.app.baiduai.listener;

/**
 * Created by Administrator on 2018/10/28.
 */
public interface TTSServiceListener {
    /**
     * 开始合成
     */
    public void onSynthesizeStart(String utteranceId);

    /**
     * 合成数据接收
     */
    public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int progress);

    /**
     * 合成完成
     */
    public void onSynthesizeFinish(String utteranceId);

    /**
     * 播放开始
     */
    public void onSpeechStart(String utteranceId);

    /**
     * 播放进度变化
     */
    public void onSpeechProgressChanged(String utteranceId, int progress);


    /**
     * 播放完成
     */
    public void onSpeechFinish(String utteranceId);

    /**
     * 出错
     */
    public void onError(String utteranceId, int code, String description);

    /**
     * 合成完成的回调
     */
    public void setTTSSynthesizeFinishCallback(TTSSynthesizeFinishCallback callback);

}
