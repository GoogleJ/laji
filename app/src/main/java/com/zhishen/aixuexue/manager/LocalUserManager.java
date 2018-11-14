package com.zhishen.aixuexue.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.zhishen.aixuexue.bean.HomeBean;

/**
 * Created by yangfaming on 2018/6/20.
 */

public class LocalUserManager {
    public static final String PREFERENCE_NAME = "userInfo";
    private String USER_INFO = "user_info";
    private String USER_PAGE_VERSION = "user_page_version";
    private String USER_APPCONFIG = "user_appconfig";
    private String ISFIRSTLAUNCH = "user_islaunch";
    private static SharedPreferences mSharedPreferences;
    private static LocalUserManager mPreferencemManager;
    private static SharedPreferences.Editor editor;
    private Context context;

    private LocalUserManager(Context ctx) {
        this.context = ctx;
        mSharedPreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context ctx) {
        if (mPreferencemManager == null) {
            mPreferencemManager = new LocalUserManager(ctx);
        }
    }

    public synchronized static LocalUserManager getInstance() {
        if (mPreferencemManager == null) {
            throw new RuntimeException("please init first!");
        }
        return mPreferencemManager;
    }

    public void setUserJson(JSONObject userJson) {
        String userInfo = "";
        if (userJson != null) {
            try {
                userInfo = userJson.toJSONString();
            } catch (JSONException e) {
            }
        }
        editor.putString(USER_INFO, userInfo);
        editor.commit();
    }

    public JSONObject getUserJson() {
        JSONObject userJson = null;

        String userStr = mSharedPreferences.getString(USER_INFO, null);
        if (userStr != null) {
            userJson = JSONObject.parseObject(userStr);

        }
        return userJson;
    }


    public void setPageDataVersion(String version) {
        editor.putString(USER_PAGE_VERSION, version).commit();
    }

    public String getPageDataVersion() {
        return mSharedPreferences.getString(USER_PAGE_VERSION, "null");
    }

    public void setAppconfig(HomeBean appPageData) {
        editor.putString(USER_APPCONFIG, new Gson().toJson(appPageData)).commit();
    }

    public void setFirstLanuch(Boolean firstLanuch) {
        editor.putBoolean(ISFIRSTLAUNCH, firstLanuch).commit();
    }

    public Boolean getFirstLanuch() {
        return mSharedPreferences.getBoolean(ISFIRSTLAUNCH, true);
    }

    public void setLatlng(Location location) {
        editor.putString("location", new Gson().toJson(location)).commit();
    }

    public Location getLatlng() {
        String location = mSharedPreferences.getString("location", null);
        return new Gson().fromJson(location, Location.class);
    }

    public HomeBean getAppconfig() {
        String string = mSharedPreferences.getString(USER_APPCONFIG, null);
        return new Gson().fromJson(string, HomeBean.class);
    }

    public void setHasUnReadInviateNubmer(boolean count) {
        editor.putBoolean("setUnReadInviateNubmer", count).commit();
    }

    public boolean getHasUnReadInviateNubmer() {
        return mSharedPreferences.getBoolean("setUnReadInviateNubmer", false);
    }

}
