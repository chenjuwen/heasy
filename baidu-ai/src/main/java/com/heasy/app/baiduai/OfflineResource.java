package com.heasy.app.baiduai;

import android.content.Context;

import com.heasy.app.core.service.ServiceEngineFactory;
import com.heasy.app.core.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class OfflineResource {
    private static Logger logger = LoggerFactory.getLogger(OfflineResource.class);

    public static final String VOICE_FEMALE = "0"; //女性
    public static final String VOICE_MALE = "1"; //男性

    private String destPath;
    private String textFilename;
    private String modelFilename;

    public OfflineResource(Context context, String voiceType) throws IOException {
        this.destPath = createTmpDir(context);
        copyAssetsFile(context, voiceType);
    }

    private String createTmpDir(Context context) {
        String sampleDir = "baiduTTS";
        String rootPath = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getSdcardRootPath();
        String tmpDir = rootPath + sampleDir;
        if (!makeDir(tmpDir)) {
            tmpDir = context.getExternalFilesDir(sampleDir).getAbsolutePath();
            if (!makeDir(sampleDir)) {
                throw new RuntimeException("create model resources dir failed :" + tmpDir);
            }
        }
        return tmpDir;
    }

    private boolean makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    public void copyAssetsFile(Context context, String voiceType) throws IOException {
        //text
        String text = "bd_etts_text.dat";
        textFilename = destPath + File.separator + text;
        boolean b1 = FileUtil.copyFromAssetsToSDCard(context, text, textFilename);
        if(b1) {
            logger.debug("文件复制成功：" + textFilename);
        }

        //model
        String model;
        if (VOICE_MALE.equals(voiceType)) {
            model = "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";
        } else {
            model = "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
        }

        modelFilename = destPath + File.separator + model;
        boolean b2 = FileUtil.copyFromAssetsToSDCard(context, model, modelFilename);
        if(b2) {
            logger.debug("文件复制成功：" + modelFilename);
        }
    }

    public String getModelFilename() {
        return modelFilename;
    }

    public String getTextFilename() {
        return textFilename;
    }

}
