package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseLazyFragment;
import com.zhishen.aixuexue.activity.ContactsActivity;
import com.zhishen.aixuexue.activity.SignActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.HomeTypeAdapter;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserCouponActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserDetailNewActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserSysNoticeActivity;
import com.zhishen.aixuexue.activity.scan.CaptureActivity;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.bean.HomeNoticeBean;
import com.zhishen.aixuexue.bean.MultiTypeItem;
import com.zhishen.aixuexue.bean.NewsBean;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.manager.PreferenceManager;
import com.zhishen.aixuexue.manager.RouteManager;
import com.zhishen.aixuexue.runtimepermissions.MPermissionUtils;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.StringUtils;
import com.zhishen.aixuexue.weight.HomeAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 爱学学界面
 * Created by Jerome on 2018/6/15.
 */
@SuppressLint("CheckResult")
public class HomeFragment extends BaseLazyFragment {

    private NewsBean homeNews;
    private HomeTypeAdapter mAdapter;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private List<MultiTypeItem> list = new ArrayList<>();

    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.mRefreshLayout) SmartRefreshLayout mRefreshLayout;

    @Override
    protected void initDependencies() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }

    /*@Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getAleartData();
        }
    }*/

    @OnClick({R.id.ivScan, R.id.ivShare})
    void onMethodClick(View v) {
        switch (v.getId()) {
            case R.id.ivScan:
                scanCode();
                break;
            case R.id.ivShare:
                share();
                break;
        }
    }

    @Override
    protected void initEventView() {
        setUserVisibleHint(true);

        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter = new HomeTypeAdapter());

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            switch (adapter.getItemViewType(position)) {
                case MultiTypeItem.HOME_NEWS_TXT:
                case MultiTypeItem.HOME_NEWS_LEFT:
                case MultiTypeItem.HOME_NEWS_BOTTOM_IMGS:
                case MultiTypeItem.HOME_NEWS_IMG:
                case MultiTypeItem.HOME_NEWS_VIDEO:
                    homeNews = (NewsBean) mAdapter.getData().get(position).getData();
                    bundle.putSerializable(NewsWebActivity.NEWS_OBJECT, homeNews);
                    if (homeNews == null) return;
                    if ("my_scanCode".equals(homeNews.getActionid())){
                        scanCode();
                    } else {
                        RouteManager.getInstance(mContext).doAction(homeNews);
                    }
                    break;
            }
        });
        //子项视图
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.ivPlay: //视频播放
                    break;
                case R.id.scrollLayout:
                    PreferenceManager.getInstance().setReadyNotify(true);
                    readyGo(NoticeListActivity.class);
                    mAdapter.notifyItemChanged(1);
                    break;
            }
        });

        //菜单
        mAdapter.setMenuOnItemClickListener((position, itemId, menu) -> {
            Bundle bundle = new Bundle();
            switch (itemId) {
                case "DomesticInformation":
                case "Educationonsulting":
                case "StudyAbroadInformation": //资讯
                    bundle.putInt(NewsReportActivity.TAB_PAGE, position);
                    readyGo(NewsReportActivity.class, bundle);
                    break;
                case "institutions": //机构
                    readyGo(NearOrganActivity.class);
                    break;
                case "favourableActivity": //优惠活动
                    readyGo(DiscountActivity.class);
                    break;
                case "openWeb":
                    HomeBean.UserMenuBean.UserMenu userMenu = new HomeBean.UserMenuBean.UserMenu();
                    userMenu.setUrl(menu.getUrl());
                    bundle.putSerializable(NewsWebActivity.NEWS_OBJECT,userMenu);
                    bundle.putInt(NewsWebActivity.NEWS_TYPE, NewsWebActivity.NEWS_WEB_URL);
                    bundle.putString(NewsWebActivity.NEWS_TITLE,menu.getTitle());
                    readyGo(NewsWebActivity.class, bundle);
                    break;
                default:
                    break;
            }
        });

        CommonUtils.showDialog(mContext, "加载中..");
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            list.clear();
            mRefreshLayout.finishRefresh(1000);
            getDataFromServer();
        });
    }

    @Override
    protected void getDataFromServer() {
        showMenuBean();
        ResApi resApi = ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, getContext());
        resApi.getNotice(ParamsMap.getNotice(1, AiApp.getInstance().getUsername()))
                .compose(RxSchedulers.ioFlowable()).compose(RxSchedulers.flowTransformer())
                .compose(bindToLifecycle())
                .subscribe(noticeBean -> showNotices(noticeBean), t -> {
                    CommonUtils.cencelDialog();
                    toast(RxException.getMessage(t));
                });

        resApi.getNews(ParamsMap.getNews(1))
                .compose(RxSchedulers.ioFlowable()).compose(RxSchedulers.flowTransformer())
                .compose(bindToLifecycle())
                .subscribe(homeNews -> showNews(homeNews.getList()), t -> {
                    CommonUtils.cencelDialog();
                    toast(RxException.getMessage(t));
                });
    }

    private void showMenuBean() {
        HomeBean homeBean = LocalUserManager.getInstance().getAppconfig();
        if (homeBean == null) return;
        list.add(new MultiTypeItem<>(MultiTypeItem.HOME_HEADER_MENU, homeBean));
    }

    private void showNotices(HomeNoticeBean noticeBean) {
        list.add(new MultiTypeItem<>(MultiTypeItem.HOME_HEADER_NOTICE, noticeBean));
    }

    private void showNews(List<NewsBean> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        for (NewsBean homeNews : data) {
            switch (homeNews.getStyle()) {
                case MultiTypeItem.HOME_NEWS_LEFT:
                    list.add(new MultiTypeItem<>(MultiTypeItem.HOME_NEWS_LEFT, homeNews));
                    break;
                case MultiTypeItem.HOME_NEWS_IMG:
                    list.add(new MultiTypeItem<>(MultiTypeItem.HOME_NEWS_IMG, homeNews));
                    break;
                case MultiTypeItem.HOME_NEWS_BOTTOM_IMGS:
                    list.add(new MultiTypeItem<>(MultiTypeItem.HOME_NEWS_BOTTOM_IMGS, homeNews));
                    break;
                case MultiTypeItem.HOME_NEWS_VIDEO:
                    list.add(new MultiTypeItem<>(MultiTypeItem.HOME_NEWS_VIDEO, homeNews));
                    break;
                default:
                    break;
            }
        }
        mAdapter.setNewData(list);
        CommonUtils.cencelDialog();
    }

    private void getAleartData() {
        ArrayList<String> list = new ArrayList<>();
        ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, getActivity())
                .getAleart().compose(RxSchedulers.ioFlowable())
                .compose(bindUntilEvent(FragmentEvent.PAUSE))
                .subscribe(homeAleartBean -> {
                    if (homeAleartBean != null) {
                        for (int i = 0; i < homeAleartBean.data.getActions().size(); i++) {
                            list.add(homeAleartBean.data.getActions().get(i).getTitle());
                        }
                    }
                    HomeAlertDialog dialog = new HomeAlertDialog(getActivity(),
                            homeAleartBean.data.getTitle(),
                            homeAleartBean.data.getMessage(),
                            list, R.style.dialog, (position, homeAlertDialog) -> {
                        switch (homeAleartBean.data.getActions().get(position).getActionID()) {
                            case "sign"://去签到
                                readyGo(SignActivity.class);
                                break;
                            case "my_youhuiquan"://我的优惠券
                                readyGo(UserCouponActivity.class);
                                break;
                            case "favourableActivity"://优惠活动列表
                                readyGo(DiscountActivity.class);
                                break;
                            case "my_tongxunlu"://通讯录
                                readyGo(ContactsActivity.class);
                                break;
                            case "my_systemMessage"://系统消息
                                readyGo(UserSysNoticeActivity.class);
                                break;
                            case "qdt"://设置重启显示启动图
                                break;
                            default:
                                CommonUtils.showToastShort(getActivity(), "暂不支持此事件");
                                break;
                        }
                        homeAlertDialog.dismiss();
                    });
                    dialog.show();
                }, throwable -> {

                });
    }

    private void scanCode() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        } else {
            startCaptureActivityForResult();
        }
    }

    private void startCaptureActivityForResult() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(mContext, CaptureActivity.class);
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
                    toast("需要获取相机权限");
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
                        Log.d("1212", result);
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
            }
        }
    }

    public void share() {
        if (Build.VERSION.SDK_INT >= 23) {
            MPermissionUtils.requestPermissionsResult(getActivity(), 1,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    new MPermissionUtils.OnPermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            initShare();
                        }

                        @Override
                        public void onPermissionDenied() {
                            MPermissionUtils.showTipsDialog(getActivity(), "文件存储");
                        }
                    });
        } else {
            initShare();
        }
    }

    /**
     * 分享图文链接
     */
    private void initShare() {
        UMImage thumb = new UMImage(getActivity(), LocalUserManager.getInstance().getAppconfig().getShare().getImage());
        UMWeb web = new UMWeb(LocalUserManager.getInstance().getAppconfig().getShare().getUrl());
        web.setTitle(LocalUserManager.getInstance().getAppconfig().getShare().getTitle());
        web.setThumb(thumb);
        web.setDescription(LocalUserManager.getInstance().getAppconfig().getShare().getContent());
        new ShareAction(getActivity())
                .setDisplayList(new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE})
                .withMedia(web)
                .setCallback(shareListener).open();
    }

    /**
     * 搜索好友
     *
     * @param userid
     */
    private void searchUser(String userid) {
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, getActivity());
        api.searchUser(userid, AiApp.getInstance().getUserSession())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            if (!UMShareAPI.get(getActivity()).isInstall(getActivity(), share_media)) {
                if (share_media == SHARE_MEDIA.WEIXIN) {
                    CommonUtils.showToastShort(getActivity(), "没有安装微信应用");
                    return;
                } else if (share_media == SHARE_MEDIA.QQ) {
                    CommonUtils.showToastShort(getActivity(), "没有安装QQ应用");
                    return;
                }
                return;
            }
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };


}
