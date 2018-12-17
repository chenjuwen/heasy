package com.heasy.app.baiduai.service;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.heasy.app.baiduai.OfflineResource;
import com.heasy.app.baiduai.config.AIConfigBean;
import com.heasy.app.baiduai.config.AIConfigFactory;
import com.heasy.app.baiduai.listener.DefaultSpeechSynthesizerListener;
import com.heasy.app.baiduai.listener.DefaultTTSServiceListener;
import com.heasy.app.baiduai.listener.TTSServiceListener;
import com.heasy.app.baiduai.listener.TTSSynthesizeFinishCallback;
import com.heasy.app.core.service.AbstractService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/21.
 */
public class DefaultTTSService extends AbstractService implements TTSService {
    private static Logger logger = LoggerFactory.getLogger(DefaultTTSService.class);
    private SpeechSynthesizer speechSynthesizer = null;
    private TTSServiceListener ttsServiceListener = null;

    public DefaultTTSService(){
        this.ttsServiceListener = new DefaultTTSServiceListener();
    }

    @Override
    public void init() {
        AIConfigBean configBean = AIConfigFactory.getAIConfigBean(getHeasyContext().getServiceEngine().getAndroidContext());

        //发音人： 0（默认）普通女声, 1 普通男声
        String person = configBean.getTtsPerson();

        //语速：范围["0" - "15"]，默认"5"
        String speed = configBean.getTtsSpeed();
        logger.debug("person=" + person + ", speed=" + speed);

        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(getHeasyContext().getServiceEngine().getAndroidContext(), person);
        } catch (IOException ex) {
            logger.error("", ex);
        }

        speechSynthesizer = SpeechSynthesizer.getInstance();
        speechSynthesizer.setContext(getHeasyContext().getServiceEngine().getAndroidContext());
        speechSynthesizer.setSpeechSynthesizerListener(new DefaultSpeechSynthesizerListener(ttsServiceListener));

        speechSynthesizer.setAppId(configBean.getTtsAppid());
        speechSynthesizer.setApiKey(configBean.getTtsApikey(), configBean.getTtsSecretkey());

        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "15");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, person);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, speed);

        //WIFI 使用在线合成，非WIFI使用离线合成
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);

        //声学模型文件路径(离线引擎使用)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, offlineResource.getModelFilename());

        AuthInfo authInfo = speechSynthesizer.auth(TtsMode.MIX);
        logger.debug("auth resule=" + authInfo.isSuccess());
        if(!authInfo.isSuccess()){
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            logger.error(errorMsg);
        }

        //离在线混合模式
        int result = speechSynthesizer.initTts(TtsMode.ONLINE);
        if (result != 0) {
            logger.error("initTts初始化失败 result=" + result);
        }else{
            logger.debug("initTts result=" + result);
        }

        successInit = true;
    }

    @Override
    public boolean isInit() {
        return successInit;
    }

    @Override
    public void unInit() {
        speechSynthesizer.stop();
        speechSynthesizer.release();
        speechSynthesizer = null;
        successInit = false;
    }

    @Override
    public int speak(String text, TTSSynthesizeFinishCallback callback) {
        logger.debug("text length: " + text.length());
        ttsServiceListener.setTTSSynthesizeFinishCallback(callback);
        return speechSynthesizer.speak(text);
    }

    @Override
    public int batchSpeak(List<String> textList) {
        if(textList != null && textList.size() > 0) {
            List<SpeechSynthesizeBag> bags = new ArrayList<SpeechSynthesizeBag>();
            for(int i=0; i<textList.size(); i++){
                //每个文本text不超过1024的GBK字节，即512个汉字或英文字母数字
                SpeechSynthesizeBag bag = new SpeechSynthesizeBag();
                bag.setText(textList.get(i));
                bag.setUtteranceId(String.valueOf(i));
                bags.add(bag);
            }
            return speechSynthesizer.batchSpeak(bags);
        }else{
            return 0;
        }
    }

    @Override
    public int synthesize(String text, TTSSynthesizeFinishCallback callback) {
        logger.debug("text length: " + text.length());
        ttsServiceListener.setTTSSynthesizeFinishCallback(callback);
        return speechSynthesizer.synthesize(text);
    }

    @Override
    public int resume() {
        return speechSynthesizer.resume();
    }

    @Override
    public int pause() {
        return speechSynthesizer.pause();
    }

    @Override
    public int stop() {
        return speechSynthesizer.stop();
    }

    @Override
    public void setTTSServiceListener(TTSServiceListener ttsServiceListener) {
        this.ttsServiceListener = ttsServiceListener;
    }

}
