package com.zhishen.aixuexue.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.manager.HTClientHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by huangfangyi on 2016/10/27.
 * qq 84543217
 */

public class OkHttpUtils {
    private Context context;
    private OkHttpClient okHttpClient;
    private static final int RESULT_ERROR = 1000;
    private static final int RESULT_SUCESS = 2000;
    private static final int RESULT_SUCESS_FROM_GET = 3000;
    private HttpCallBack httpCallBack;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int reusltCode = msg.what;
            switch (reusltCode) {
                case RESULT_ERROR:
                    httpCallBack.onFailure((String) msg.obj);
                    Log.d("OkHttpUtils", "result----->" + (String) msg.obj);
                    break;
                case RESULT_SUCESS:
                    String result = (String) msg.obj;
                    Log.d("OkHttpUtils", "result----->" + result);
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        httpCallBack.onResponse(jsonObject);
                        if (jsonObject != null && jsonObject.containsKey("code") && jsonObject.containsKey("message")) {
                            if (jsonObject.getInteger("code") == 0 && jsonObject.getString("message").contains("session")) {
                                HTClientHelper.getInstance().notifyConflict(context);
                            }
                        }
                    } catch (JSONException e) {
                        httpCallBack.onFailure((String) msg.obj);
                    }
                    break;
                case RESULT_SUCESS_FROM_GET:
                    String resultfromget = (String) msg.obj;
                    Log.d("OkHttpUtils", "resultfromget----->" + resultfromget);
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(resultfromget);
                        httpCallBack.onResponse(jsonObject);
                        if (jsonObject != null && jsonObject.containsKey("code") && jsonObject.containsKey("message")) {
                            if (jsonObject.getInteger("code") == 0 && jsonObject.getString("message").contains("session")) {
                                HTClientHelper.getInstance().notifyConflict(context);
                            }
                        }
                    } catch (JSONException e) {
                        httpCallBack.onFailure((String) msg.obj);
                    }
                    break;
            }

        }
    };

    public OkHttpUtils(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
    }


    /**
     * get请求
     *
     * @param url
     * @param callBack
     */
    public void requestFromGet(String url, final HttpCallBack callBack) {
        this.httpCallBack = callBack;
        Request request = new Request.Builder()
                .url(url)
                .build();
        if (CommonUtils.isNetWorkConnected(context)) {
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message message = handler.obtainMessage();
                    message.what = RESULT_ERROR;
                    message.obj = e.getMessage().toString();
                    message.sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("result", response.body().string());
                    Message message = handler.obtainMessage();
                    message.what = RESULT_SUCESS_FROM_GET;
                    message.obj = jsonObject.toJSONString();
                    message.sendToTarget();
                }
            });
        } else {
            CommonUtils.showToastShort(context, R.string.the_current_network);
        }
    }


    private void startRequest(Request request) {
        if (CommonUtils.isNetWorkConnected(context)) {
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message message = handler.obtainMessage();
                    message.what = RESULT_ERROR;
                    try {
                        message.obj = e.getMessage().toString();
                    } catch (Exception e1) {
                        message.obj = e1.getMessage().toString();
                    }
                    message.sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Message message = handler.obtainMessage();
                    message.what = RESULT_SUCESS;
                    message.obj = response.body().string();
                    message.sendToTarget();
                }
            });
        } else {
            CommonUtils.showToastShort(context, R.string.the_current_network);
        }
    }

    public interface HttpCallBack {

        void onResponse(JSONObject jsonObject);

        void onFailure(String errorMsg);
    }

    /**
     * 下载不带进度
     */
    public interface DownloadCallBack {
        void onSuccess();

        void onFailure(String message);
    }


    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public void loadFile(String url, final String savePath, final DownloadCallBack callBack) {
        Request request = new Request.Builder()
                //下载地址
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                //可以在这里自定义路径
                File file1 = new File(savePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                callBack.onSuccess();
            }
        });
    }

    /**
     * 下载的带进度的callback
     */
    public interface ProgressDownloadCallBack {
        void onSuccess();

        void onProgress(int progress);

        void onFailure();
    }

    /**
     * 下载文件带进度
     *
     * @param url      下载地址
     * @param savePath 保存地址
     * @param callBack ProgressDownloadCallBack
     */
    public void loadFileHasProgress(String url, final String savePath, final ProgressDownloadCallBack callBack) {
        Request request = new Request.Builder()
                //下载地址
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                long requestLength = response.body().contentLength();
                long total = 0;
                //可以在这里自定义路径
                File file1 = new File(savePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                while ((len = inputStream.read(buf)) != -1) {
                    total += len;
                    // publishing the progress....
                    if (requestLength > 0) // only if total length is known
                        callBack.onProgress((int) (total * 100 / requestLength));
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                callBack.onSuccess();
            }
        });
    }

    /**
     * Json数据请求数据
     *
     * @param url      地址
     * @param object   Josn数据
     * @param callBack 回调
     */
    public void postByJSONObject(String url, JSONObject object, HttpCallBack callBack) {
        this.httpCallBack = callBack;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, object.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        startRequest(request);
    }

    /**
     * Json数据格式的字符串请求。
     *
     * @param url       地址
     * @param objString json数据格式的字符串
     * @param callBack  回调监听
     */
    public void postByJsonString(String url, String objString, HttpCallBack callBack) {
        this.httpCallBack = callBack;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, objString);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        startRequest(request);
    }
}
