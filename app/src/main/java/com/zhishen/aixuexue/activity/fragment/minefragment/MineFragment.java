package com.zhishen.aixuexue.activity.fragment.minefragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseLazyFragment;
import com.zhishen.aixuexue.activity.ContactsActivity;
import com.zhishen.aixuexue.activity.LoginActivity;
import com.zhishen.aixuexue.activity.SignActivity;
import com.zhishen.aixuexue.activity.converstation.ConversationActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.NewsWebActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.ResApi;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.activity.fragment.minefragment.adapter.MineTypeAdapter;
import com.zhishen.aixuexue.activity.fragment.minefragment.follow.FollowOrganActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.follow.FollowPeopleActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserCouponActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserDetailNewActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserFeedbackActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserImgCodeActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserInfoActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserMineCollectActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserSettingActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserSysNoticeActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.WorldFakeActivity;
import com.zhishen.aixuexue.activity.scan.CaptureActivity;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.bean.MultiTypeItem;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的界面
 * Created by Jerome on 2018/6/15.
 */
@SuppressLint("CheckResult")
public class MineFragment extends BaseLazyFragment {

    public static final String TAG = MineFragment.class.getSimpleName();
    private MineTypeAdapter mAdapter;
    private List<MultiTypeItem> list = new ArrayList<>();
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    private static final int REQ_CODE_PERMISSION = 0x1111;

    @Override
    protected void initDependencies() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initEventView() {
        setUserVisibleHint(true);
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter = new MineTypeAdapter());
        mAdapter.setNewData(list);
        mAdapter.setInfoItemOnClickListener(id -> {
            switch (id) {
                case "my_shoucang":
                    readyGo(UserMineCollectActivity.class);
                    break;
                case "my_jigou":
                    readyGo(FollowOrganActivity.class);
                    break;
                case "my_friend":
                    readyGo(FollowPeopleActivity.class);
                    break;
                case "my_youhuiquan":
                    readyGo(UserCouponActivity.class);
                    break;
                default:
                    break;
            }
        });
        mAdapter.setUserMenuItemOnClickListener((id, menu) -> {
            switch (id) {
                case "my_kechen": //课程
                    loadWeb(menu, "我的课程");
                    break;
                case "my_huihua": //会话
                    readyGo(ConversationActivity.class);
                    break;
                case "my_tongxunlu": //通讯录
                    readyGo(ContactsActivity.class);
                    break;
                case "my_systemMessage": //系统消息
                    readyGo(UserSysNoticeActivity.class);
                    break;
            }
        });

        mAdapter.setCourseItemOnClickListener((id, menu) -> {
            switch (id) {
                case "my_review": //点评
                    toast("暂无此功能");
                    break;
                case "my_QRCode": //二维码
                    readyGo(UserImgCodeActivity.class);
                    break;
                case "my_album": //相册
                    Bundle bundle = new Bundle();
                    bundle.putString(WorldFakeActivity.CIRCLE_UID, AiApp.getInstance().getUsername());
                    readyGo(WorldFakeActivity.class, bundle);
                    break;
                case "my_feedback": //反馈
                    readyGo(UserFeedbackActivity.class);
                    break;
                case "my_scanCode": //扫码
                    scanCode();
                    break;
                case "my_shezhi":
                    readyGo(UserSettingActivity.class);
                    break;
            }
        });

        mAdapter.setUserToolsItemOnClickListener((id, menu, title) -> {
            switch (id) {
                case "jiancebaogao":
                case "yimiaozhushou":
                case "kechengbiao":
                case "zuoxishijian":
                case "yuyinpince":
                case "openWeb":
                    loadWeb(menu, title);
                    break;
            }
        });

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.tvSign:
                    readyGo(SignActivity.class);
                    break;
                case R.id.ll_mine_user:
                    if (HTClient.getInstance().isLogined()) {
                        readyGo(UserInfoActivity.class);
                    } else {
                        readyGo(LoginActivity.class);
                    }
                    break;
            }
        });
    }

    private void loadWeb(HomeBean.UserMenuBean.UserMenu menu, String title) {
        Bundle bundle = new Bundle();
        bundle.putInt(NewsWebActivity.NEWS_TYPE, NewsWebActivity.NEWS_WEB_URL);
        bundle.putSerializable(NewsWebActivity.NEWS_OBJECT, menu);
        bundle.putString(NewsWebActivity.NEWS_TITLE, title);
        readyGo(NewsWebActivity.class, bundle);
    }

    @Override
    protected void getDataFromServer() {
        HomeBean homeBean = LocalUserManager.getInstance().getAppconfig();
        if (homeBean.getUserMenus() != null && !homeBean.getUserMenus().isEmpty()) {
            for (HomeBean.UserMenuBean usrBean : homeBean.getUserMenus()) {
                switch (usrBean.getGroupTitleid()) {
                    case HomeBean.UserMenuBean.USER_MENU_INFO:
                        list.add(new MultiTypeItem<>(MultiTypeItem.MINE_USER_INFO, usrBean));
                        break;
                    case HomeBean.UserMenuBean.USER_MENU:
                        list.add(new MultiTypeItem<>(MultiTypeItem.MINE_USER_MENU, usrBean));
                        break;
                    case HomeBean.UserMenuBean.USER_MENU_COURSE:
                        list.add(new MultiTypeItem<>(MultiTypeItem.MINE_USER_COURSE, usrBean));
                        break;
                    case HomeBean.UserMenuBean.USER_MENU_TOOLS:
                        list.add(new MultiTypeItem<>(MultiTypeItem.MINE_USER_TOOLS, usrBean));
                        break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        statisticCount();
    }

    //更新关注
    private void statisticCount() {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, mContext)
                .getStatistCount(ParamsMap.getUserCount())
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .compose(bindUntilEvent(FragmentEvent.PAUSE))
                .subscribe(userMenus -> mAdapter.updateCount(userMenus), t -> toast(RxException.getMessage(t)));
    }

    private void scanCode() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        } else {
            startCaptureActivityForResult();
        }
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(mContext, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CaptureActivity.REQ_CODE:
                    if (null != data) {
                        String result = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                        if (!result.contains("data")) {
                            CommonUtils.showToastShort(getActivity(), "该码未识别");
                            return;
                        }
                        if (!StringUtils.isEmpty(result)) {
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            int codeType = jsonObject.getIntValue("codeType");
                            switch (codeType) {
                                case 1://二维码登录
//                                    JSONObject authObj = jsonObject.getJSONObject("data");
//                                    sendToAuth(authObj);
                                    break;
                                case 2://添加好友
                                    JSONObject data1 = jsonObject.getJSONObject("data");
                                    String userId = data1.getString(Constant.JSON_KEY_HXID);
                                    searchUser(userId);
                                    break;
                                case 3://预留

                                    break;
                                case 4:

                                    break;
                                case 5://支付二维码
//                                    String qrCodeStr = jsonObject.getString("data");
//                                    qrCodeStr = URLDecoder.decode(qrCodeStr);
//                                    startActivity(new Intent(getActivity(), QrCodePayMentActivity.class).putExtra("data", qrCodeStr));
                                    break;
                            }
                        }
                    } else {
                        CommonUtils.showToastShort(getActivity(), "解析二维码失败");
                    }
                    break;
                case CaptureActivity.RESULT_CANCELED:

                    break;
            }
        }
    }

    private void searchUser(String userid) {
        if (TextUtils.isEmpty(userid)) return;
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, getActivity());
        api.searchUser(userid, AiApp.getInstance().getUserSession())
                .compose(RxSchedulers.ioObserver())
                .compose(bindUntilEvent(FragmentEvent.PAUSE))
                .subscribe(jsonObject -> {
                    Log.d("AddFriendsNextActivity", "---------" + jsonObject.toJSONString());
                    CommonUtils.cencelDialog();
                    int code = jsonObject.getInteger("code");
                    if (code == 1) {
                        JSONObject json = jsonObject.getJSONObject("user");
                        startActivity(new Intent(getActivity(), UserDetailNewActivity.class).putExtra(Constant.KEY_USER_INFO, json.toJSONString()));
                    } else if (code == -1) {
                        CommonUtils.showToastShort(getActivity(), R.string.User_does_not_exis);
                    } else if (code == 0) {
                        CommonUtils.showToastShort(getActivity(), R.string.server_is_busy_try_again);
                    } else {
                        CommonUtils.showToastShort(getActivity(), R.string.server_is_busy_try_again);
                    }
                }, throwable -> {
                    CommonUtils.cencelDialog();
                    CommonUtils.showToastShort(getActivity(), R.string.server_is_busy_try_again);
                });
    }

}
