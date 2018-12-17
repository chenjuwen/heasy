package com.heasy.app.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2018/2/12.
 */
public class MemoryCookieStore implements CookieStore {
    private Map<String, List<Cookie>> dataMap = new ConcurrentHashMap<>();

    @Override
    public void saveCookie(HttpUrl url, List<Cookie> cookieList) {
        List<Cookie> oldCookies = dataMap.get(url.host());
        List<Cookie> needRemove = new ArrayList<>();
        for (Cookie newCookie : cookieList) {
            for (Cookie cookie : oldCookies) {
                if (newCookie.name().equals(cookie.name())) {
                    needRemove.add(cookie);
                }
            }
        }
        oldCookies.removeAll(needRemove); //删除旧的
        oldCookies.addAll(cookieList); //添加新的
    }

    @Override
    public void saveCookie(HttpUrl url, Cookie newCookie) {
        List<Cookie> oldCookies = dataMap.get(url.host());
        List<Cookie> needRemove = new ArrayList<>();
        for (Cookie cookie : oldCookies) {
            if (newCookie.name().equals(cookie.name())) {
                needRemove.add(cookie);
            }
        }
        oldCookies.removeAll(needRemove);
        oldCookies.add(newCookie);
    }

    @Override
    public List<Cookie> loadCookie(HttpUrl url) {
        List<Cookie> oldCookies = dataMap.get(url.host());
        if (oldCookies == null) {
            oldCookies = new ArrayList<>();
            dataMap.put(url.host(), oldCookies);
        }
        return oldCookies;
    }

    @Override
    public List<Cookie> getCookie(HttpUrl url) {
        List<Cookie> cookies = new ArrayList<>();
        List<Cookie> oldCookies = dataMap.get(url.host());
        if (oldCookies != null) {
            cookies.addAll(oldCookies);
        }
        return cookies;
    }

    @Override
    public List<Cookie> getAllCookie() {
        List<Cookie> cookies = new ArrayList<>();
        Set<String> httpUrls = dataMap.keySet();
        for (String url : httpUrls) {
            cookies.addAll(dataMap.get(url));
        }
        return cookies;
    }

    @Override
    public boolean removeCookie(HttpUrl url, Cookie cookie) {
        List<Cookie> cookies = dataMap.get(url.host());
        if (cookie != null) {
            cookies.remove(cookie);
        }
        return true;
    }

    @Override
    public boolean removeCookie(HttpUrl url) {
        dataMap.remove(url.host());
        return true;
    }

    @Override
    public boolean removeAllCookie() {
        dataMap.clear();
        return true;
    }
}
