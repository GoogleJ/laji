package com.zhishen.aixuexue.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.manager.HTClientHelper;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.manager.NotifierManager;
import com.zhishen.aixuexue.manager.PreferenceManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yangfaming on 2018/6/20.
 */
@SuppressLint("CheckResult")
public class AiApp extends Application {
    private String TAG = this.getClass().getName();
    private static AiApp instance;
    private static Context applicationContext;
    public static boolean isCalling = false;
    private JSONObject userJson = null;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        // 初始化登录用户信息管理类
        LocalUserManager.init(applicationContext);
        //好友列表管理类
        ContactsManager.init(applicationContext);
        //sdk管理类
        HTClientHelper.init(applicationContext);
        //通知管理类
        NotifierManager.init(applicationContext);
        PreferenceManager.init(applicationContext);
        UMConfigure.init(applicationContext, "5b2b46e0b27b0a4c1a000015", "umeng", UMConfigure.DEVICE_TYPE_PHONE, "90d019b45841367153755deb8148a489");
        PlatformConfig.setWeixin("wx1927982a0fae293b", "c109cd21635d320c770be793ceb52205");
        PlatformConfig.setQQZone("1106986060", "T34trcZ68iT7mWni");
        CrashReport.initCrashReport(getApplicationContext(), "0210a93ec7", false);
        initPushAgent();


    }

    public static AiApp getInstance() {
        return instance;
    }

    public static AiApp getContext() {
        return instance;
    }

    /**
     * 初始化推送
     */
    private void initPushAgent() {
        PushAgent instance = PushAgent.getInstance(applicationContext);
        instance.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "umeng token   -----   " + s);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.d(TAG, s + "------" + s1);
            }
        });
    }

    /**
     * 暂时写在这里，后期会再loginactivity里调用。
     */
    private void loginIM() {

        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, this);
        api.login("15693822795", "123456")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        Logger.json(jsonObject.toJSONString());
                        JSONObject userJson = jsonObject.getJSONObject("user");
                        String userId = userJson.getString("userId");
                        String password = userJson.getString("hx_password");
                        if (TextUtils.isEmpty(password)) {
                            password = userJson.getString("password");
                        }
                        HTClient.getInstance().login(userId, password, new HTClient.HTCallBack() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "消息服务器连接成功");
                            }

                            @Override
                            public void onError() {
                                Log.d(TAG, "消息服务器连接失败");
                            }
                        });
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "========    loginim    " + throwable.getMessage());
                    }
                });


    }


    public void setUserJson(JSONObject userJson) {
        this.userJson = userJson;
        LocalUserManager.getInstance().setUserJson(userJson);
    }

    public JSONObject getUserJson() {
        if (userJson == null) {
            userJson = LocalUserManager.getInstance().getUserJson();
        }
        return userJson;
    }

    public void setUserAvatar(String userAvatar) {
        JSONObject userJson = getUserJson();
        userJson.put(Constant.JSON_KEY_AVATAR, userAvatar);
        setUserJson(userJson);
    }

    public void setUserNick(String nick) {
        JSONObject jsonObject = getUserJson();
        jsonObject.put(Constant.JSON_KEY_NICK, nick);
        setUserJson(jsonObject);
    }

    public String getUsertel() {
        String username = null;
        if (getUserJson() != null) {
            username = getUserJson().getString(Constant.JSON_KEY_TEL);
        }
        return username;
    }

    public String getUserAvatar() {
        String avatar = null;
        if (getUserJson() != null) {
            avatar = getUserJson().getString(Constant.JSON_KEY_AVATAR);
            if (TextUtils.isEmpty(avatar)) {
                avatar = "false";
            }
        }
        return avatar;
    }


    public String getUsername() {
        String username = null;
        if (getUserJson() != null) {
            username = getUserJson().getString(Constant.JSON_KEY_HXID);
        }
        return username;
    }

    public String getUserNick() {
        String username = null;
        if (getUserJson() != null) {
            username = getUserJson().getString(Constant.JSON_KEY_NICK);
            if (TextUtils.isEmpty(username)) {
                username = getUsername();
            }
        }
        return username;
    }

    public String getUserSession() {
        String session = null;
        if (getUserJson() != null) {
            session = getUserJson().getString(Constant.JSON_KEY_SESSION);
        }
        return session;
    }
}
