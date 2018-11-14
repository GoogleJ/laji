package com.zhishen.aixuexue.util;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 项目名称：yichat0504
 * 类描述：UpdateLocalLoginTimeUtils 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/5 17:48
 * 邮箱:814326663@qq.com
 */
public class UpdateLocalLoginTimeUtils {
    public static void sendLocalTimeToService(Context context) {
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, context);
        JSONObject userJson = AiApp.getInstance().getUserJson();
        if (userJson != null) {
            String session = userJson.getString(Constant.JSON_KEY_SESSION);
            if (session != null) {
                api.updateLocalTimestamp(session)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<JSONObject>() {
                            @Override
                            public void accept(JSONObject jsonObject) throws Exception {
                                int code = jsonObject.getIntValue("code");
                                switch (code) {
                                    case 1:
//                                CommonUtils.showToastShort(context, "上传本地成功!");
                                        break;
                                    default:
//                                CommonUtils.showToastShort(context, "上传本地失败!");
                                        break;
                                }
                            }
                        }, throwable -> CommonUtils.showToastShort(context, "上传本地失败!"));
            }

        }
    }
}
