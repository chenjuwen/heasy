package com.heasy.app.http;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2018/2/12.
 */
public class DefaultCookieJar implements CookieJar {
    private CookieStore cookieStore;

    public DefaultCookieJar(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookieList) {
        cookieStore.saveCookie(httpUrl, cookieList);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        return cookieStore.loadCookie(httpUrl);
    }

}
