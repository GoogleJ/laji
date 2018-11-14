package com.zhishen.aixuexue.http;


import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public final class CbcConverterFactory extends Converter.Factory {
    private final Gson gson;

    public static CbcConverterFactory create() {
        return create(new Gson());
    }

    public static CbcConverterFactory create(Gson gson) {
        return new CbcConverterFactory(gson);
    }
    private CbcConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
       //TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new ResponseBodyCbcConverter<>(gson, type);  //响应
    }




}
