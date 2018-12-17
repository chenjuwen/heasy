package com.heasy.app.http;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

public class HttpTest {
    @Test
    public void synGet() throws Exception {
        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper.build();

        String content = okHttpClientHelper.synGet("http://10.1.43.6:8888/boot/test");
        System.out.println(content);
    }

    @Test
    public void asynGet() throws Exception {
        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper.build();

        okHttpClientHelper.asynGet("http://10.1.43.6:8888/boot/test", new HttpClientListener() {
            @Override
            public void onReponse(Response response) {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void postJSON() throws Exception {
        String jsonData = "{\"username\":\"cjm\", \"password\":\"123\"}";

        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper.build();

        okHttpClientHelper.postJSON("http://10.1.43.6:8888/boot/saveUser2", jsonData, new HttpClientListener() {
            @Override
            public void onReponse(Response response) {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void post() throws Exception {
        Request request = new RequestBuilder()
                .url("http://10.1.43.6:8888/boot/saveUser3")
                .addHeader("token", "123456")
                .addFormParam("username", "cjm")
                .addFormParam("password", "123中文")
                .build();

        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper.build();

        okHttpClientHelper.post(request, new HttpClientListener() {
            @Override
            public void onReponse(Response response) {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void postWithFile() throws Exception {
        Request request = new RequestBuilder()
                .url("http://10.1.43.6:8888/boot/saveUser4")
                .addHeader("token", "123456")
                .addFormParam("username", "cjm")
                .addFormParam("password", "123中文")
                .addFileParam("image", "error.png", MediaType.parse("image/png"), new File("D:\\git_proj\\study\\okhttp\\error.png"))
                .build();

        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper.build();

        okHttpClientHelper.post(request, new HttpClientListener() {
            @Override
            public void onReponse(Response response) {
                try {
                    String content = response.body().string();
                    System.out.println(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}