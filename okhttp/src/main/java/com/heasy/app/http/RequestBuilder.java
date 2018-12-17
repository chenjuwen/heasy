package com.heasy.app.http;

import com.heasy.app.core.utils.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestBuilder {
	private Request.Builder builder;

	private Map<String, String> formParamMap = new HashMap<>();
	private Map<String, FileWrapper> fileParamMap = new HashMap<>();
	
	public RequestBuilder(){
		builder = new Request.Builder();
	}
	
	public Request build(){
		RequestBody body = generateRequestBody();
		builder.post(body);
		return builder.build();
	}
	
	public RequestBuilder url(String url){
		builder.url(url);
		return this;
	}
	
	public RequestBuilder addHeader(String name, String value){
		builder.addHeader(name, value);
		return this;
	}
	
	public RequestBuilder addFormParam(String name, String value){
		this.formParamMap.put(name, value);
		return this;
	}
	
	public RequestBuilder addFileParam(FileWrapper fileWrapper){
		this.fileParamMap.put(StringUtil.getUUIDString(), fileWrapper);
		return this;
	}
	
	public RequestBuilder addFileParam(String fileFieldKeyName, String filename, MediaType mediaType, File file){
		this.fileParamMap.put(StringUtil.getUUIDString(), new FileWrapper(fileFieldKeyName, filename, mediaType, file));
		return this;
	}
	
	private RequestBody generateRequestBody(){
		if(fileParamMap != null && !fileParamMap.isEmpty()){
			MultipartBody.Builder builder = new MultipartBody.Builder();
			builder.setType(MultipartBody.FORM);
			
			for(String key : fileParamMap.keySet()){
				FileWrapper fileWrapper = fileParamMap.get(key);
				builder.addFormDataPart(fileWrapper.getName(), fileWrapper.getFilename(), RequestBody.create(fileWrapper.getMediaType(), fileWrapper.getFile()));
			}
			
			if(formParamMap != null && !formParamMap.isEmpty()){
	    		for(String key : formParamMap.keySet()){
	    			builder.addFormDataPart(key, formParamMap.get(key));
	    		}
			}
			
			return builder.build();
			
		}else if(formParamMap != null && !formParamMap.isEmpty()){
			FormBody.Builder builder = new FormBody.Builder();
    		for(String key : formParamMap.keySet()){
    			builder.add(key, formParamMap.get(key));
    		}
    		
	    	return builder.build();
	    	
		}else{
			return new FormBody.Builder().build();
		}
	}
	
}
