package com.heasy.app.action.pplay;

import com.alibaba.fastjson.JSONObject;
import com.heasy.app.action.ActionNames;
import com.heasy.app.baiduai.api.SpeechAPI;
import com.heasy.app.baiduai.config.AIConfigBean;
import com.heasy.app.baiduai.config.AIConfigFactory;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.MP3Play;
import com.heasy.app.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TTS语音合成
 */
@JSActionAnnotation(name = ActionNames.TTS)
public class TTSAction implements Action {
    private static Logger logger = LoggerFactory.getLogger(TTSAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        if("replay".equalsIgnoreCase(extend)){
            String mp3FilePath = FastjsonUtil.getString(jsonObject, "mp3FilePath");
            MP3Play.play(mp3FilePath);
            return SUCCESS;

        }else {
            try {
                String text = FastjsonUtil.getString(jsonObject, "text");

                String destFileName = YYContentAction.VOICE_FILE_BASE_PATH + "synthetize.mp3";
                logger.debug(destFileName);

                AIConfigBean configBean = AIConfigFactory.getAIConfigBean(heasyContext.getServiceEngine().getAndroidContext());

                SpeechAPI tts = new SpeechAPI(configBean.getTtsAppid(), configBean.getTtsApikey(), configBean.getTtsSecretkey());
                tts.setAue("3"); //mp3
                tts.setPer(configBean.getTtsPerson());
                tts.setSpd(configBean.getTtsSpeed());
                tts.setText(text);
                tts.setDestFileName(destFileName);
                tts.generate();

                MP3Play.play(destFileName);

                return destFileName;

            } catch (Exception ex) {
                logger.error("", ex);
            }

            return "";
        }
    }

}
