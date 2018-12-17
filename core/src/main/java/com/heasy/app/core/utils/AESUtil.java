package com.heasy.app.core.utils;

import com.heasy.app.core.base64.Base64Decoder;
import com.heasy.app.core.base64.Base64Encoder;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES算法加密工具类
 */
public class AESUtil {
    private static final String HEX = "0123456789ABCDEF";
    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";//AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private static final String ALGO = "AES";//AES 加密
    private static final String SHA1PRNG = "SHA1PRNG";//// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
    private static final int KEYSIZE = 128;

    /**
     * 加密
     * @param key
     * @param content
     * @return
     */
    public static String encrypt(String key, String content) {
        if(StringUtil.isEmpty(content)){
            return content;
        }

        try {
            SecretKeySpec secretKeySpec = generateSecretKeySpec(key.getBytes());

            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));

            byte[] encrypted = cipher.doFinal(content.getBytes());
            return Base64Encoder.encode(encrypted);

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 解密
     * @param key
     * @param content
     * @return
     */
    public static String decrypt(String key, String content) {
        if(StringUtil.isEmpty(content)){
            return content;
        }

        try {
            byte[] encrypted = Base64Decoder.decodeToBytes(content);

            SecretKeySpec secretKeySpec = generateSecretKeySpec(key.getBytes());

            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

     /*
     * 生成随机数，可作为动态密钥
     */
    public static String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance(SHA1PRNG);
            byte[] bytes_key = new byte[20];
            localSecureRandom.nextBytes(bytes_key);
            String str_key = toHex(bytes_key);
            return str_key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String toHex(byte[] buf) {
        if (buf == null) return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            result.append(HEX.charAt((buf[i] >> 4) & 0x0f)).append(HEX.charAt(buf[i] & 0x0f));
        }
        return result.toString();
    }

    /**
     * 生成SecretKeySpec对象
     * @param seed
     * @return
     * @throws Exception
     */
    private static SecretKeySpec generateSecretKeySpec(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGO);

        //防止linux下随机生成key
        SecureRandom secureRandom = null;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            secureRandom = SecureRandom.getInstance(SHA1PRNG, "Crypto");
        }else{
            secureRandom = SecureRandom.getInstance(SHA1PRNG);
        }

        secureRandom.setSeed(seed);
        kgen.init(KEYSIZE, secureRandom); //128, 192, 256

        SecretKey secretKey = kgen.generateKey();
        byte[] encodeFormat = secretKey.getEncoded();

        SecretKeySpec secretKeySpec = new SecretKeySpec(encodeFormat, ALGO);

        return secretKeySpec;
    }


}
