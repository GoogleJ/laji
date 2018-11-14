package com.zhishen.aixuexue.http;

import android.content.Context;
import android.util.Log;

import com.zhishen.aixuexue.util.CommonUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServiceFactory {
    private static String TAG = ServiceFactory.class.getName();
    private static OkHttpClient client;

    //带加密的
    public static <T> T createNewRetrofitServiceAes(final Class<T> clazz, final Context mContext) {
        try {
            if (!CommonUtils.isNetWorkConnected(mContext)) {
                CommonUtils.showToastShort(mContext, "请检查你的网络连接");
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("app_name", Constant.APP_NAME)//平台
                                .header("app_version", Constant.getAppVersion(mContext))//系统版本号
                                .header("type", Constant.getBundleIdentifier(mContext))//包信息
                                .header("uuid", Constant.getAppUuid(mContext));//设备唯一码
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.NEW_API_HOST)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(AesCbcConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        T service = retrofit.create(clazz);
        return service;
    }

    public static <T> T createRetrofitService(final Class<T> clazz, String host, Context mContext) {
        try {
            if (!CommonUtils.isNetWorkConnected(mContext)) {
                CommonUtils.showToastShort(mContext, "请检查你的网络连接");
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(host)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(AesCbcConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        T service = retrofit.create(clazz);

        return service;
    }

    /**
     * 这是不带解密的请求
     *
     * @param clazz
     * @param host
     * @param mContext
     * @param <T>
     * @return
     */
    public static <T> T createRetrofitServiceNoAes(final Class<T> clazz, String host, Context mContext) {
        try {
            if (!CommonUtils.isNetWorkConnected(mContext)) {
                CommonUtils.showToastShort(mContext, "请检查你的网络连接");
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(host)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(CbcConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        T service = retrofit.create(clazz);

        return service;
    }
}