package com.heasy.app.baiduai.service;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.sdk.model.OcrRequestParams;
import com.baidu.ocr.sdk.model.OcrResponseResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.heasy.app.baiduai.listener.OCRServiceListener;
import com.heasy.app.core.service.AbstractService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Administrator on 2018/10/21.
 */
public class DefaultOCRService extends AbstractService implements OCRService {
    private static Logger logger = LoggerFactory.getLogger(DefaultOCRService.class);
    private boolean tokenLoaded = false;

    @Override
    public void init() {
        OCR.getInstance(getHeasyContext().getServiceEngine().getAndroidContext()).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                logger.debug("token=" + token);
                tokenLoaded = true;
            }

            @Override
            public void onError(OCRError ex) {
                logger.error("OCR TOKEN初始化失败", ex);
            }
        }, getHeasyContext().getServiceEngine().getAndroidContext());

        successInit = true;
    }

    @Override
    public boolean isInit() {
        return successInit;
    }

    @Override
    public void unInit() {
        OCR.getInstance(getHeasyContext().getServiceEngine().getAndroidContext()).release();
        successInit = false;
    }

    @Override
    public boolean tokenLoaded() {
        return tokenLoaded;
    }

    /**
     * 通用文字识别
     * @param imagePath 图片绝对路径
     * @param listener
     */
    @Override
    public void generalBasic(String imagePath, final OCRServiceListener listener) {
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true); //是否检测图像朝向
        param.setImageFile(new File(imagePath)); //图像数据

        OCR.getInstance(getHeasyContext().getServiceEngine().getAndroidContext())
                .recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                logger.debug(result.getJsonRes());

                StringBuilder sb = new StringBuilder();
                for (WordSimple wordSimple : result.getWordList()) {
                    WordSimple word = wordSimple;
                    sb.append(word.getWords());
                    sb.append("\\n");
                }

                listener.onResult(0, sb.toString());
            }

            @Override
            public void onError(OCRError ex) {
                logger.error("通用文字识别失败", ex);
                listener.onResult(-1, ex.getMessage());
            }
        });
    }

    @Override
    public void recognizeIDCard(String imagePath, String cardSide, final OCRServiceListener listener) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(imagePath));
        param.setIdCardSide(cardSide);
        param.setDetectDirection(true);
        param.setImageQuality(20);
        param.setDetectRisk(true);

        OCR.getInstance(getHeasyContext().getServiceEngine().getAndroidContext()).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                logger.debug(result.getJsonRes());
                listener.onResult(0, result.getJsonRes());
            }

            @Override
            public void onError(OCRError error) {
                logger.error("身份证识别失败", error);
                listener.onResult(-1, error.getMessage());
            }
        });
    }

    @Override
    public void recognizeDrivingLicense(String imagePath, final OCRServiceListener listener) {
        OcrRequestParams param = new OcrRequestParams();
        param.setImageFile(new File(imagePath));
        param.putParam("detect_direction", true);

        OCR.getInstance(getHeasyContext().getServiceEngine().getAndroidContext()).recognizeDrivingLicense(param, new OnResultListener<OcrResponseResult>() {
            @Override
            public void onResult(OcrResponseResult result) {
                logger.debug(result.getJsonRes());
                listener.onResult(0, result.getJsonRes());
            }

            @Override
            public void onError(OCRError error) {
                logger.error("驾驶证识别失败", error);
                listener.onResult(-1, error.getMessage());
            }
        });
    }

}
