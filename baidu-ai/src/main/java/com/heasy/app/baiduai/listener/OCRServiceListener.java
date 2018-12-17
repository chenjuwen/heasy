package com.heasy.app.baiduai.listener;

/**
 * Created by Administrator on 2018/10/21.
 */
public interface OCRServiceListener {
    /**
     * @param code  0成功， -1失败
     * @param result 识别结果
     */
    public void onResult(int code, String result);
}
