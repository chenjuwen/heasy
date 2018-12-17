package com.heasy.app.http;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2018/2/12.
 */
public interface CookieStore {
    void saveCookie(HttpUrl url, List<Cookie> cookieList);
    void saveCookie(HttpUrl url, Cookie cookie);

    List<Cookie> loadCookie(HttpUrl url);

    List<Cookie> getCookie(HttpUrl url);
    List<Cookie> getAllCookie();

    boolean removeCookie(HttpUrl url, Cookie cookie);
    boolean removeCookie(HttpUrl url);
    boolean removeAllCookie();

}
