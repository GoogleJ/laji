package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.Api;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.BlackAndWhiteRequest;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.GetInstitutionsResponse;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.InstitutionDetailRequest;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.fragment.MechanismFragment1;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.fragment.MechanismFragment2;
import com.zhishen.aixuexue.http.BaseResponseDataT;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.PicturePageTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.jivesoftware.smack.util.SystemUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class MechanismActivity extends BaseActivity {

    private ImageView iv_mechanism_top;
    private ImageView iv_mechanism_head;
    private TextView tv_mechanism_title;
    private TextView tv_mechanism_addnotice;

    public static final String MECHAIN_ID = "mechain_id";
    private MagicIndicator indicator_mechanism;

    private ViewPager viewpager_machanism;

    String[] titles = {"机构介绍", "评论"};

    private String id;
    private GetInstitutionsResponse response;

    private boolean canAddNotice = true;

    Api retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanism);

        retrofitService = ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, this);

        iv_mechanism_top = findViewById(R.id.iv_mechanism_top);
        iv_mechanism_head = findViewById(R.id.iv_mechanism_head);
        tv_mechanism_title = findViewById(R.id.tv_mechanism_title);
        indicator_mechanism = findViewById(R.id.indicator_mechanism);
        viewpager_machanism = findViewById(R.id.viewpager_machanism);
        tv_mechanism_addnotice = findViewById(R.id.tv_mechanism_addnotice);

        initData();

    }

    private void initPager() {
        viewpager_machanism.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    MechanismFragment1 mechanismFragment1 = new MechanismFragment1();
                    mechanismFragment1.setData(response);
                    return mechanismFragment1;
                } else {
                    MechanismFragment2 mechanismFragment2 = new MechanismFragment2();
                    mechanismFragment2.setDataList(id);
                    return mechanismFragment2;
                }
            }

            @Override
            public int getCount() {
                return titles.length;
            }
        });
    }

    private void initMagic() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);

        CommonNavigatorAdapter commonNavigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
//                PicturePageTitleView simplePagerTitleView = new PicturePageTitleView(context);
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.textcolor3));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.textcolor1));
                simplePagerTitleView.setOnClickListener(v -> viewpager_machanism.setCurrentItem(index, true));
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(1.5f));
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setColors(ContextCompat.getColor(context, R.color.themecolor));
                return indicator;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return 1;
            }
        };

        commonNavigator.setAdapter(commonNavigatorAdapter);
        indicator_mechanism.setNavigator(commonNavigator);

        ViewPagerHelper.bind(indicator_mechanism, viewpager_machanism);
    }

    private void initData() {
        id = getIntent().getStringExtra(MECHAIN_ID);
        InstitutionDetailRequest request = new InstitutionDetailRequest();
        request.setId(id);
        request.setCustomerId(AiApp.getInstance().getUsername());

        CommonUtils.showDialog(MechanismActivity.this, "加载中");
        ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, this)
                .institutionDetail(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(getInstitutionsResponseBaseResponseDataT -> {
                    CommonUtils.cencelDialog();
                    response = getInstitutionsResponseBaseResponseDataT.data;

                    if (getInstitutionsResponseBaseResponseDataT.code != 0) {
                        CommonUtils.showToastShort(MechanismActivity.this, getInstitutionsResponseBaseResponseDataT.msg);
                        return;
                    }

                    canAddNotice = !response.isFocus();

                    if (canAddNotice) {
                        tv_mechanism_addnotice.setText("关注");
                        tv_mechanism_addnotice.setBackgroundResource(R.drawable.shape_mechanism_notice);
                    } else {
                        tv_mechanism_addnotice.setText("取消关注");
                        tv_mechanism_addnotice.setBackgroundResource(R.drawable.shape_mechanism_notice_unable);
                    }

                    BitmapUtil.loadNormalImg(iv_mechanism_top,
                            response.getHeadUrl()
                            , R.drawable.default_image, new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    iv_mechanism_top.setImageBitmap(BitmapUtil.rsBlur(MechanismActivity.this, resource, 3, 0.125f));

                                    BitmapUtil.loadCircleImg(iv_mechanism_head,
                                            response.getHeadUrl(),
                                            R.drawable.default_avatar);
                                    return true;
                                }
                            });

                    tv_mechanism_title.setText(response.getName());

                    initPager();
                    initMagic();

                }, e -> {
                    CommonUtils.showToastShort(MechanismActivity.this, "未知错误");
                    CommonUtils.cencelDialog();
                    e.printStackTrace();
                });
    }

    String decode;

    //关注
    public void addNotice(View view) {
        if (decode == null) {
            BlackAndWhiteRequest blackAndWhiteRequest = new BlackAndWhiteRequest();
            blackAndWhiteRequest.setFromUserid(AiApp.getInstance().getUsername());
            blackAndWhiteRequest.setToContentid(id);
            blackAndWhiteRequest.setOperateType("2");
            blackAndWhiteRequest.setContentType("3");
            decode = blackAndWhiteRequest.decode();
        }

        Observable<BaseResponseDataT> observable;

        if (!canAddNotice) {
            observable = retrofitService.cancelBlackAndWhite(decode);
        } else {
            observable = retrofitService.addBlackAndWhite(decode);
        }

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(response -> {
                    if (response.code != 0) {
                        CommonUtils.showToastShort(this, response.msg);
                        return;
                    }

                    if (canAddNotice) {
                        tv_mechanism_addnotice.setText("取消关注");
                        CommonUtils.showToastShort(this, "已关注");
                        tv_mechanism_addnotice.setBackgroundResource(R.drawable.shape_mechanism_notice_unable);
                    } else {
                        tv_mechanism_addnotice.setText("关注");
                        CommonUtils.showToastShort(this, "已取消关注");
                        tv_mechanism_addnotice.setBackgroundResource(R.drawable.shape_mechanism_notice);
                    }

                    canAddNotice = !canAddNotice;
                }, throwable -> {

                });
    }

    View popup;
    PopupWindow mPopWindow;

    //更多功能
    public void more(View view) {
        if (mPopWindow == null) {
            popup = View.inflate(this, R.layout.popup_mechanism_more, null);
            mPopWindow = new PopupWindow(popup, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            tv_report = popup.findViewById(R.id.tv_report);
            tv_delete = popup.findViewById(R.id.tv_delete);
            tv_cancel = popup.findViewById(R.id.tv_cancel);
            ll_popup_window = popup.findViewById(R.id.ll_popup_window);
        }
        mPopWindow.setBackgroundDrawable(new ColorDrawable());
        mPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        handlePopup(mPopWindow);
        showPopup(popup);
    }

    TextView tv_report;
    TextView tv_delete;
    TextView tv_cancel;
    LinearLayout ll_popup_window;

    //给popup设置监听
    private void handlePopup(PopupWindow mPopWindow) {
        tv_report.setOnClickListener(view -> {
            mPopWindow.dismiss();
            if (!canReport) {
                CommonUtils.showToastShort(this, "请勿重复举报！");
                return;
            }
            Intent intent = new Intent(MechanismActivity.this, ReportActivity.class);
            intent.putExtra("id", id);
            startActivityForResult(intent, 1);
        });

        tv_delete.setOnClickListener(view -> {
            BlackAndWhiteRequest blackAndWhiteRequest = new BlackAndWhiteRequest();
            blackAndWhiteRequest.setFromUserid(AiApp.getInstance().getUsername());
            blackAndWhiteRequest.setToContentid(id);
            blackAndWhiteRequest.setOperateType("1");
            blackAndWhiteRequest.setContentType("3");

            retrofitService.addBlackAndWhite(blackAndWhiteRequest.decode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle())
                    .subscribe(response -> {
                        if (response.code != 0) {
                            CommonUtils.showToastShort(MechanismActivity.this, response.msg);
                            return;
                        }

                        CommonUtils.showToastShort(MechanismActivity.this, "已拉黑");

                    }, throwable -> CommonUtils.showToastShort(MechanismActivity.this, "未知错误"));

            mPopWindow.dismiss();
        });

        tv_cancel.setOnClickListener(view -> mPopWindow.dismiss());

        ll_popup_window.setOnClickListener(view -> mPopWindow.dismiss());

        ll_popup_window.setOnClickListener(view -> mPopWindow.dismiss());

        mPopWindow.setOnDismissListener(() -> backgroundAlpha(1f));
    }

    private void showPopup(View popup) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnimation.setDuration(300);
        popup.startAnimation(translateAnimation);
        backgroundAlpha(0.6f);
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    public void back(View view) {
        finish();
    }

    boolean canReport = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("1212", "-----------");
        if (requestCode == 1 && resultCode == 1) {
            if (data.getStringExtra("result").equals("success")) {
                canReport = false;
            }
        }
    }
}
