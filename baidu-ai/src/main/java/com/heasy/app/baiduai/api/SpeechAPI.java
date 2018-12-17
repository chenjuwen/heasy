package com.heasy.app.baiduai.api;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.heasy.app.core.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * 语音合成 - 百度SDK
 * 
 * 支持最多512字（1024 字节)的音频合成，合成的文件格式为mp3
 * 接口说明： https://cloud.baidu.com/doc/SPEECH/TTS-Online-Java-SDK.html#.E7.AE.80.E4.BB.8B
 * 
 */
public class SpeechAPI {
	private static Logger logger = LoggerFactory.getLogger(SpeechAPI.class);
	private static AipSpeech client = null;
	
	private String cuid = "qc00001";
	private String per = "0";
	private String aue = "4";
	private String vol = "15";
	private String spd = "5";
	private String pit = "5";
	
	private String appId;
	private String apiKey;
	private String secretKey;
	
	private String text; //需要合成语音的文字内容
	private String destFileName; //保存语音数据流的目标文件地址
	
	public SpeechAPI(String appId, String apiKey, String secretKey){
		this.appId = appId;
		this.apiKey = apiKey;
		this.secretKey = secretKey;
		
		init();
	}
	
	private void init(){
		try{
			if(client == null){
				client = new AipSpeech(appId, apiKey, secretKey);
				client.setConnectionTimeoutInMillis(10000);
		        client.setSocketTimeoutInMillis(10000);
			}
		}catch(Exception ex){
			logger.error("", ex);
		}
	}

	public void generate() throws Exception {
		//lan语言
		//ctp 客户端类型选择，web端填写固定值1
		//cuid用户唯一标识
		//tok开发者access_token
		//tex要合成语音的文本，使用UTF-8编码。小于2048个中文字或者英文数字。
		//per发音人选择, 0为普通女声默认，1为普通男生，3为情感合成-度逍遥，4为情感合成-度丫丫
		//aue 3为mp3格式(默认)； 4为pcm-16k；5为pcm-8k；6为wav（内容同pcm-16k）; 注意aue=4或者6是语音识别要求的格式
		//vol音量，取值0-15，默认为5
		//spd语速，取值0-15，默认为5
		//pit音调，取值0-15，默认为5
		HashMap<String, Object> options = new HashMap<String, Object>();
	    options.put("cuid", cuid);
	    options.put("per", per);
	    options.put("aue", aue);
	    options.put("vol", vol);
	    options.put("spd", spd);
	    options.put("pit", pit);
	    
		TtsResponse response = client.synthesis(text, "zh", 1, options);
		if(response.getResult() == null){ //合成成功时为null
			byte[] data = response.getData();
			FileUtil.writeFile(data, destFileName);
		}else{
			logger.error("合成失败：" + response.getResult().toString(4));
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDestFileName() {
		return destFileName;
	}

	public void setDestFileName(String destFileName) {
		this.destFileName = destFileName;
	}

	public String getCuid() {
		return cuid;
	}

	public void setCuid(String cuid) {
		this.cuid = cuid;
	}

	public String getPer() {
		return per;
	}

	public void setPer(String per) {
		this.per = per;
	}

	public String getAue() {
		return aue;
	}

	public void setAue(String aue) {
		this.aue = aue;
	}

	public String getVol() {
		return vol;
	}

	public void setVol(String vol) {
		this.vol = vol;
	}

	public String getSpd() {
		return spd;
	}

	public void setSpd(String spd) {
		this.spd = spd;
	}

	public String getPit() {
		return pit;
	}

	public void setPit(String pit) {
		this.pit = pit;
	}
	
}
