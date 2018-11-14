package com.zhishen.aixuexue.http;

import com.google.gson.Gson;

/**
 * Created by FangJie on 16/1/7.
 */
public class BaseAesRequestData {

    public String decode() {
        String gson = new Gson().toJson(this);
        String encode = AesCBC.getInstance().encrypt(gson);
        encode = encode.replaceAll("", "");
        return encode;
    }
}
