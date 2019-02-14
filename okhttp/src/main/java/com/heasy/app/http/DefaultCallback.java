package com.heasy.app.http;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/12.
 */
public class DefaultCallback implements Callback {
    private static Logger logger = LoggerFactory.getLogger(DefaultCallback.class);
    private HttpClientListener listener;
    
    public DefaultCallback(HttpClientListener listener){
    	this.listener = listener;
    }
    
    @Override
    public void onFailure(Call call, IOException ex) {
        logger.error("callback error", ex);
        call.cancel();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if(response.isSuccessful()){
        	if(listener != null){
        		listener.onReponse(response);
        	}
        }
		response.close();
    }

	public void setListener(HttpClientListener listener) {
		this.listener = listener;
	}

}
