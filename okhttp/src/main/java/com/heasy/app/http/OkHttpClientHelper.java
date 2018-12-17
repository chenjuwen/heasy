package com.heasy.app.http;

import com.heasy.app.core.utils.FileUtil;
import com.heasy.app.core.utils.ParameterUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/12.
 */
public class OkHttpClientHelper {
    private static Logger logger = LoggerFactory.getLogger(OkHttpClientHelper.class);
    private static final int DEFAULT_SECONDS = 5;
    private OkHttpClient.Builder builder;
    private OkHttpClient okHttpClient;

    public OkHttpClientHelper(){
        builder = new OkHttpClient.Builder();

        DefaultCookieJar cookieJar = new DefaultCookieJar(new MemoryCookieStore());

        builder.connectTimeout(DEFAULT_SECONDS, TimeUnit.SECONDS)
                .cookieJar(cookieJar);
    }
    
    public OkHttpClientHelper connectTimeout(long timeout, TimeUnit unit){
        builder.connectTimeout(timeout, unit);
        return this;
    }

    public OkHttpClientHelper pingInterval(long timeout, TimeUnit unit){
        builder.pingInterval(timeout, unit);
        return this;
    }

    public OkHttpClientHelper readTimeout(long timeout, TimeUnit unit){
        builder.readTimeout(timeout, unit);
        return this;
    }

    public OkHttpClientHelper writeTimeout(long timeout, TimeUnit unit){
        builder.writeTimeout(timeout, unit);
        return this;
    }

    public OkHttpClientHelper cookieJar(CookieJar cookieJar){
        builder.cookieJar(cookieJar);
        return this;
    }
    
    public void build(){
    	okHttpClient = builder.build();
    }

    /**
     * 同步get
     * @param url
     * @return
     */
    public String synGet(String url){
        return synGet(url, null);
    }

    public String synGet(String url, Map<String, String> params){
        try {
            StringBuilder sb = new StringBuilder(url);
            if(params != null && params.size() > 0){
                sb.append("?");
                sb.append(ParameterUtil.toParamString(params, true));
            }
            logger.debug(sb.toString());

            Request request = new Request.Builder().url(sb.toString()).build();
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                return response.body().string();
            }
            response.close();

        }catch (Exception ex){
            logger.error("synGet error: " + ex.toString());
        }
        return null;
    }

    /**
     * 异步get
     * @param url
     * @param listener
     */
    public void asynGet(String url, HttpClientListener listener){
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new DefaultCallback(listener));
    }
    
    /**
     * post json格式内容
     * @param url
     * @param jsonData
     * @param listener
     */
    public void postJSON(String url, String jsonData, HttpClientListener listener){
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    	RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder().url(url).post(body).build();
        okHttpClient.newCall(request).enqueue(new DefaultCallback(listener));
    }
    
    public void post(Request request, HttpClientListener listener){
        okHttpClient.newCall(request).enqueue(new DefaultCallback(listener));
    }
    
    public void download(String url, final String destFilePath){
    	Request request = new Request.Builder().url(url).build();
    	okHttpClient.newCall(request).enqueue(new DefaultCallback(new HttpClientListener() {
			@Override
			public void onReponse(Response response) {
				if(response.isSuccessful()){
					InputStream inputStream = response.body().byteStream();
					FileUtil.writeFile(inputStream, destFilePath);
		            logger.info("file download to: " + destFilePath);
				}
			}
		}));
    }

}
