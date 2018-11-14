package com.zhishen.aixuexue.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

/**
 * 基本配置文件
 */
public class Constant {
    public static final String NEW_API_HOST = "http://47.95.123.98/api/";//PHP消息相关接口。
    public static final String NEW_API_APPCONFIG = "http://192.168.10.8:8888/MAMP/zmhphp/education/";//张明辉本地数据接口
//    public static final String BASE_TEMP = "http://47.95.244.69:7075/";//java业务逻辑接口
    public static final String BASE_TEMP = "http://api.platform.aixuexue.com/";//java业务逻辑接口
//    public static final String BASE_TEMP = "http://192.168.10.161:7075/";//java业务逻辑接口
    public static String APP_VERSION = "1.0.1";   // APP版本
    public static String APP_UUID = "";
    public static String bundleIdentifier = "";
    public static String PhoneType = "";
    public static String APP_NAME = "android";
    public static String chooseLocation;

    public static final String JSON_KEY_NICK = "nick";
    public static final String JSON_KEY_HXID = "userId";
    public static final String JSON_KEY_FXID = "fxid";
    public static final String JSON_KEY_SEX = "sex";
    public static final String JSON_KEY_AVATAR = "avatar";
    public static final String JSON_KEY_CITY = "city";
    public static final String JSON_KEY_PASSWORD = "hx_password";
    public static final String JSON_KEY_PROVINCE = "province";
    public static final String JSON_KEY_TEL = "tel";
    public static final String JSON_KEY_SIGN = "sign";
    public static final String JSON_KEY_ROLE = "role";
    public static final String JSON_KEY_BIGREGIONS = "bigRegions";
    public static final String JSON_KEY_SESSION = "session";
    public static final String SMALL_FROM_MOMENT = "SMALL_FROM_MOMENT";
    public static final String baseImgUrl = "http://zs-3-km.oss-cn-beijing.aliyuncs.com/";
    //添加好友的原因
    public static final String CMD_ADD_REASON = "ADD_REASON";
    /**
     * 关于艾特功能
     */
    public static final String MESSAGE_ATTR_AT_MSG = "at_list";
    public static final String MESSAGE_ATTR_VALUE_AT_MSG_ALL = "ALL";
    public static final String PLACEHOLDER = " ";//空格占位符
    public static final String ATHOLDER = "@";
    public static final String KEY_USER_INFO = "userInfo";

    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionCode + "";
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

    public static String getAppVersion(Context context) {
        if (APP_VERSION.equals("")) {
            APP_VERSION = getVersion(context);
        }
        return APP_VERSION;
    }

    public static String getBundleIdentifier(Context context) {
        if (bundleIdentifier.equals("")) {
            bundleIdentifier = context.getPackageName();
        }
        return bundleIdentifier;
    }

    public static String getAppUuid(Context context) {
        if (APP_UUID.equals("")) {
            APP_UUID = getuuid(context);
        }
        return APP_UUID;
    }

    private static String getuuid(Context context) {
        try {
            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
            String identity = preference.getString("identity", null);
            if (identity == null) {
                identity = java.util.UUID.randomUUID().toString();
                preference.edit().putString("identity", identity);
            }
            return identity;
        } catch (Exception e) {
            return "未定义";
        }
    }

    public static String getPhoneType() {
        if (PhoneType.equals("")) {
            PhoneType = getDeviceBrand() + getSystemModel() + getSystemVersion();
        }
        return PhoneType;
    }


    /**
     * @return 系统版本
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * @return 终端最终名称
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * @return 设备品牌
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

}