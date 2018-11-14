package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.utils.MessageUtils;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.ChatActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.WorldFakeActivity;
import com.zhishen.aixuexue.activity.newfriend.AddFriendsFinalActivity;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.dbsqlite.UserDao;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UserDetailNewActivity extends BaseActivity {
    @BindView(R.id.iv_user_avatar)
    CircleImageView circleImageView;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.tv_user_id)
    TextView tv_user_id;
    @BindView(R.id.tv_user_descrise)
    TextView tv_user_descrise;
    @BindView(R.id.ll_user_addfriend)
    LinearLayout ll_user_addfriend;
    @BindView(R.id.ll_user_sendmessage)
    LinearLayout ll_user_sendmessage;
    private Unbinder bind;
    private JSONObject userJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_new);
        transparentStatusBar();
        bind = ButterKnife.bind(this);
        initData();
    }

    @SuppressLint("CheckResult")
    public void refreshInfo(final String userId) {
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, mActivity);
        CommonUtils.showDialog(mActivity, getString(R.string.now_refresh_msg));
        api.getUserInfo(userId, AiApp.getInstance().getUserSession())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        Log.d("UserDetailActivity", "----------" + jsonObject.toJSONString());
                        int code = jsonObject.getInteger("code");
                        CommonUtils.cencelDialog();
                        switch (code) {
                            case 1:
                                JSONObject json = jsonObject.getJSONObject("user");
                                userJson = json;
                                //刷新ui
                                if (isFriend(userId)) {
                                    User user = CommonUtils.Json2User(json);
                                    UserDao dao = new UserDao(mActivity);
                                    dao.saveContact(user);
                                    ContactsManager.getInstance().getContactList().put(user.getUsername(), user);
                                }
                                String id = json.getString(Constant.JSON_KEY_HXID);
                                String name = json.getString(Constant.JSON_KEY_NICK);
                                String avatarUrl = json.getString(Constant.JSON_KEY_AVATAR);
                                String sign = json.getString(Constant.JSON_KEY_SIGN);
                                if (!TextUtils.isEmpty(avatarUrl)) {
                                    if (!avatarUrl.contains("http:")) {
                                        avatarUrl = Constant.NEW_API_HOST + "upload/" + avatarUrl;
                                    }
                                }
                                Log.d("1212", id + name);
                                CommonUtils.loadUserAvatar(mActivity, avatarUrl, circleImageView);
                                tv_user_name.setText(name);
                                tv_user_id.setText(id);
                                tv_user_descrise.setText(sign);
                                if (isMe(id)) {//是自己两个按钮都隐藏
                                    ll_user_addfriend.setVisibility(View.GONE);
                                    ll_user_sendmessage.setVisibility(View.GONE);
                                    return;
                                }
                                if (isFriend(id)) {//是好友则显示发送消息反之显示添加好友
                                    ll_user_sendmessage.setVisibility(View.VISIBLE);
                                    ll_user_addfriend.setVisibility(View.GONE);
                                } else {
                                    ll_user_addfriend.setVisibility(View.VISIBLE);
                                    ll_user_sendmessage.setVisibility(View.GONE);
                                }
                                break;
                            default:
                                CommonUtils.cencelDialog();
                                break;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        CommonUtils.cencelDialog();
                    }
                });
    }

    private void initData() {
        if (!TextUtils.isEmpty(getFxid())) {
            refreshInfo(getFxid());
            return;
        }
        if (getUserJson() == null) {
            finish();
            return;
        }
        if (isFriend(getUserJson().getString(Constant.JSON_KEY_HXID))) {
            refreshInfo(getUserJson().getString(Constant.JSON_KEY_HXID));
        } else {
            refreshInfo(getUserJson().getString(Constant.JSON_KEY_HXID));
        }
    }


    public String getFxid() {
        return getIntent().getStringExtra(Constant.JSON_KEY_HXID);
    }

    public JSONObject getUserJson() {
        String extra = getIntent().getStringExtra(Constant.KEY_USER_INFO);
        Log.d("1212", "-----" + extra);
        JSONObject object = JSONObject.parseObject(extra);
        return object;
    }

    /**
     * 资料是否是自己
     *
     * @param userId
     * @return
     */
    public boolean isMe(String userId) {
        return AiApp.getInstance().getUsername().equals(userId);
    }

    /**
     * 资料是否是好友
     *
     * @param userId
     * @return
     */
    public boolean isFriend(String userId) {
        return ContactsManager.getInstance().getContactList().containsKey(userId);
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }

    private void transparentStatusBar() {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                w.setStatusBarColor(Color.TRANSPARENT);
            } else {
                w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    @OnClick({R.id.ll_user_addfriend, R.id.ll_user_sendmessage, R.id.iv_user_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_user_addfriend:
                if (userJson != null) {
                    startActivity(new Intent(mActivity, AddFriendsFinalActivity.class).putExtra(Constant.KEY_USER_INFO, userJson.toJSONString()));
                    Log.d("1212", "++++++++++");
                }
                break;
            case R.id.ll_user_sendmessage:
                if (userJson != null) {
                    Intent intent = new Intent(mActivity, ChatActivity.class);
                    intent.putExtra("userId", userJson.getString(Constant.JSON_KEY_HXID));
                    intent.putExtra("userNick", userJson.getString(Constant.JSON_KEY_NICK));
                    intent.putExtra("userAvatar", userJson.getString(Constant.JSON_KEY_AVATAR));
                    intent.putExtra("chatType", MessageUtils.CHAT_SINGLE);
                    startActivity(intent.putExtra("userId", userJson.getString(Constant.JSON_KEY_HXID)));
                }
                break;
            case R.id.iv_user_avatar:
                if (userJson != null) {
                    Intent intent = new Intent(mActivity, WorldFakeActivity.class);
                    intent.putExtra(WorldFakeActivity.CIRCLE_UID, userJson.getString(Constant.JSON_KEY_HXID));
                    intent.putExtra(WorldFakeActivity.CIRCLE_TYPE, 1);
                    startActivity(intent);
                }
                break;
        }
    }

}
