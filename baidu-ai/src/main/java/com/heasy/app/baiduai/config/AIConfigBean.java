package com.heasy.app.baiduai.config;

/**
 * Created by Administrator on 2018/12/13.
 */
public class AIConfigBean {
    public static final String AICONFIG_FILE_NAME = "ai-config.xml";

    private String ttsAppid;
    private String ttsApikey;
    private String ttsSecretkey;
    private String ttsPerson;
    private String ttsSpeed;

    private String asrAppid;
    private String asrApikey;
    private String asrSecretkey;

    private String transAppid;
    private String transSecretkey;

    private String faceAppid;
    private String faceApikey;
    private String faceSecretkey;

    public String getTtsAppid() {
        return ttsAppid;
    }

    public void setTtsAppid(String ttsAppid) {
        this.ttsAppid = ttsAppid;
    }

    public String getTtsApikey() {
        return ttsApikey;
    }

    public void setTtsApikey(String ttsApikey) {
        this.ttsApikey = ttsApikey;
    }

    public String getTtsSecretkey() {
        return ttsSecretkey;
    }

    public void setTtsSecretkey(String ttsSecretkey) {
        this.ttsSecretkey = ttsSecretkey;
    }

    public String getTtsPerson() {
        return ttsPerson;
    }

    public void setTtsPerson(String ttsPerson) {
        this.ttsPerson = ttsPerson;
    }

    public String getTtsSpeed() {
        return ttsSpeed;
    }

    public void setTtsSpeed(String ttsSpeed) {
        this.ttsSpeed = ttsSpeed;
    }

    public String getAsrAppid() {
        return asrAppid;
    }

    public void setAsrAppid(String asrAppid) {
        this.asrAppid = asrAppid;
    }

    public String getAsrApikey() {
        return asrApikey;
    }

    public void setAsrApikey(String asrApikey) {
        this.asrApikey = asrApikey;
    }

    public String getAsrSecretkey() {
        return asrSecretkey;
    }

    public void setAsrSecretkey(String asrSecretkey) {
        this.asrSecretkey = asrSecretkey;
    }

    public String getTransAppid() {
        return transAppid;
    }

    public void setTransAppid(String transAppid) {
        this.transAppid = transAppid;
    }

    public String getTransSecretkey() {
        return transSecretkey;
    }

    public void setTransSecretkey(String transSecretkey) {
        this.transSecretkey = transSecretkey;
    }

    public String getFaceAppid() {
        return faceAppid;
    }

    public void setFaceAppid(String faceAppid) {
        this.faceAppid = faceAppid;
    }

    public String getFaceApikey() {
        return faceApikey;
    }

    public void setFaceApikey(String faceApikey) {
        this.faceApikey = faceApikey;
    }

    public String getFaceSecretkey() {
        return faceSecretkey;
    }

    public void setFaceSecretkey(String faceSecretkey) {
        this.faceSecretkey = faceSecretkey;
    }
}
