package com.heasy.app.baiduai.service;

import com.heasy.app.baiduai.listener.OCRServiceListener;
import com.heasy.app.core.service.Service;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface OCRService extends Service {
    //通用文字识别
    void generalBasic(String imagePath, OCRServiceListener listener);

    //身份证识别
    void recognizeIDCard(String imagePath, String cardSide, OCRServiceListener listener);

    //驾驶证识别
    void recognizeDrivingLicense(String imagePath, OCRServiceListener listener);

    //token是否获取成功
    boolean tokenLoaded();
}
