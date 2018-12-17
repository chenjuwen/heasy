package com.heasy.app.baiduai.api;

import com.heasy.app.core.utils.MD5Util;
import com.heasy.app.http.OkHttpClientHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TranslateAPI {
    private static Logger logger = LoggerFactory.getLogger(TranslateAPI.class);
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private String appid;
    private String securityKey;

    public TranslateAPI(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public String getTransResult(String query, String from, String to) {
        Map<String, String> params = buildParams(query, from, to);
        String result = "";

        try {
            OkHttpClientHelper helper = new OkHttpClientHelper();
            helper.build();

            result = helper.synGet(TRANS_API_HOST, params);

        }catch(Exception ex){
            logger.error("", ex);
        }

        return result;
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5Util.md5(src));

        return params;
    }

}
