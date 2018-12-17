package com.heasy.app.baiduai.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018/10/28.
 */
public class DefaultTTSServiceListener implements TTSServiceListener {
    private static Logger logger = LoggerFactory.getLogger(DefaultTTSServiceListener.class);
    private TTSSynthesizeFinishCallback synthesizeFinishCallback = null;
    private ByteArrayOutputStream outStream = null;

    @Override
    public void onSynthesizeStart(String utteranceId) {
        outStream = new ByteArrayOutputStream();
    }

    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int progress) {
        //logger.debug("onSynthesizeDataArrived: utteranceId=" + utteranceId + ", progress=" + progress);
        //logger.debug(bytes.length + ": " + Arrays.toString(bytes));

        try {
            outStream.write(bytes);
        } catch (IOException ex) {
            logger.error("", ex);
        }
    }

    @Override
    public void onSynthesizeFinish(String utteranceId) {
        logger.debug("onSynthesizeFinish: utteranceId=" + utteranceId);
        byte[] dataByte = null;
        try {
            dataByte = outStream.toByteArray();
            outStream.close();
            outStream = null;
        } catch (IOException ex) {
            dataByte = null;
            logger.error("", ex);
        }

        if(dataByte != null && synthesizeFinishCallback != null){
            synthesizeFinishCallback.execute(dataByte);
        }
    }

    @Override
    public void onSpeechStart(String utteranceId) {
        //logger.debug("onSpeechStart: utteranceId=" + utteranceId);
    }

    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        //logger.debug("onSpeechProgressChanged: utteranceId=" + utteranceId + ", progress=" + progress);
    }

    @Override
    public void onSpeechFinish(String utteranceId) {
        logger.debug("onSpeechFinish: utteranceId=" + utteranceId);
    }

    @Override
    public void onError(String utteranceId, int code, String description) {
        if(outStream != null) {
            try {
                outStream.close();
                outStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.debug("onError: utteranceId=" + utteranceId + ", code=" + code + ", description=" + description);
    }

    @Override
    public void setTTSSynthesizeFinishCallback(TTSSynthesizeFinishCallback callback) {
        this.synthesizeFinishCallback = callback;
    }

}
