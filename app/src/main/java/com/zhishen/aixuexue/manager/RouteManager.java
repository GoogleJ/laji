package com.zhishen.aixuexue.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.zhishen.aixuexue.activity.ContactsActivity;
import com.zhishen.aixuexue.activity.SignActivity;
import com.zhishen.aixuexue.activity.converstation.ConversationActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.DiscountActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.NearOrganActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.NewsWebActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserCouponActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserFeedbackActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserImgCodeActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserMineCollectActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserSysNoticeActivity;
import com.zhishen.aixuexue.bean.NewsBean;
import com.zhishen.aixuexue.main.MainActivity;

/**
 * Created by Jerome on 2018/6/26.
 */

public class RouteManager {

    private static Context mContext;

    private RouteManager() {
    }

    public static RouteManager getInstance(Context context){
        mContext = context;
        return RouteHolder.INSTANCE;
    }

    private static class RouteHolder {
        private static RouteManager INSTANCE = new RouteManager();
    }

    public void doAction(Object obj) {
        NewsBean homeNews = null;
        if (obj == null) return;
        if (obj instanceof NewsBean){
             homeNews = (NewsBean) obj;
        }

        if (homeNews == null) return;
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(homeNews.getActionid())){
            switch (homeNews.getActionid()) {
                case "openVideo":
                    break;
                case "tab_map": //首页地图
                    bundle.putInt(MainActivity.ACTION_TYPE_POSITION, 0);
                    readyGo(MainActivity.class, bundle);
                    break;
                case "tab_interactive": //首页互动
                    bundle.putInt(MainActivity.ACTION_TYPE_POSITION, 1);
                    readyGo(MainActivity.class, bundle);
                    break;
                case "tab_home"://首页爱学学
                    bundle.putInt(MainActivity.ACTION_TYPE_POSITION, 2);
                    readyGo(MainActivity.class, bundle);
                    break;
                case "tab_found":
                    bundle.putInt(MainActivity.ACTION_TYPE_POSITION, 3);
                    readyGo(MainActivity.class, bundle);
                    break;
                case "tab_my":
                    bundle.putInt(MainActivity.ACTION_TYPE_POSITION, 4);
                    readyGo(MainActivity.class, bundle);
                    break;
                case "my_QRCode": //二维码
                    readyGo(UserImgCodeActivity.class);
                    break;
                case "my_qunliao": //系统消息
                    readyGo(UserSysNoticeActivity.class);
                    break;
                case "my_tongxunlu": //通讯录
                    readyGo(ContactsActivity.class);
                    break;
                case "my_huihua": //会话
                    readyGo(ConversationActivity.class);
                    break;
                case "my_shoucang":  //收藏
                    readyGo(UserMineCollectActivity.class);
                    break;
                case "my_youhuiquan": //优惠券
                    readyGo(UserCouponActivity.class);
                    break;
                case "sign": //签到
                    readyGo(SignActivity.class);
                    break;
                case "my_jigou":
                    readyGo(NearOrganActivity.class);
                    break;
                case "my_feedback": //反馈
                    readyGo(UserFeedbackActivity.class);
                    break;
                case "favourableActivity":
                    readyGo(DiscountActivity.class);
                    break;
                case "openWeb":
                    if (!TextUtils.isEmpty(homeNews.getActionidparameter())) {
                        bundle.putSerializable(NewsWebActivity.NEWS_OBJECT, homeNews);
                        bundle.putString(NewsWebActivity.NEWS_TITLE, homeNews.getTitle());
                        bundle.putInt(NewsWebActivity.NEWS_TYPE, NewsWebActivity.NEWS_WEB_PARAMS);
                        readyGo(NewsWebActivity.class, bundle);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void readyGo(Class<?> clazz) {
        Intent intent = new Intent(mContext, clazz);
        mContext.startActivity(intent);
    }

    private void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(mContext, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        mContext.startActivity(intent);
    }
}
