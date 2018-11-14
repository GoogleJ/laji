package com.zhishen.aixuexue.http;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * 加解密工具类
 * Created by yangfaming on 2017/12/15.
 */

public class AesCBC {
    private static String sKey = "NzoxMTowMCAgICAg";
    private static String ivParameter = "TW9uIE9jdCAzMCAx";
    private static AesCBC instance = null;

    private AesCBC() {

    }

    public static AesCBC getInstance() {
        if (instance == null)
            instance = new AesCBC();
        return instance;
    }

    // 加密
    public static String encrypt(String sSrc, String encodingFormat, String sKey, String ivParameter)
            throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(encodingFormat));

        return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码。
    }

    public String encrypt(String sSrc) {
        try {
            String encrypt = encrypt(sSrc, "utf-8", sKey, ivParameter);
            String decrypt = decrypt(encrypt);
            Log.d("1212", "加密" + encrypt + "解密" + decrypt);
            return encrypt(sSrc, "utf-8", sKey, ivParameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String sSrc) {
        try

        {
            return decrypt(sSrc, "utf-8", sKey, ivParameter);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解密
    public static String decrypt(String sSrc, String encodingFormat, String sKey, String ivParameter)
            throws Exception {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, encodingFormat);
            return originalString.trim();
        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
            return null;
        }
    }
}
