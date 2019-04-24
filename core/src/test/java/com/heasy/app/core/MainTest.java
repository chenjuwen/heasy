package com.heasy.app.core;

import com.heasy.app.core.utils.AESUtil;

import org.junit.Test;

/**
 * Created by Administrator on 2018/2/13.
 */
public class MainTest {
    @Test
    public void testAESUtil() throws Exception {
        String key = AESUtil.generateKey();
        System.out.println("key: " + key);

        String data = "2016原始数据qwer!@#$%^&*()_+}{:.,<>?fdasfad45353范德萨发打发范德萨发生阿范德萨阿范德萨发顺丰阿发法萨芬撒";
        System.out.println("原始数据：" + data + ", " + data.getBytes().length);

        String dataEnc = AESUtil.encrypt(key, data);
        System.out.println("加密数据：" + dataEnc + ", " + dataEnc.getBytes().length);

        String dataDec = AESUtil.decrypt(key, dataEnc);
        System.out.println("解密数据: " + dataDec);

        System.out.println(data.equals(dataDec));
    }

}
