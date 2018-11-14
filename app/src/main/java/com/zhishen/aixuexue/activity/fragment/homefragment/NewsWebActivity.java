package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.bean.NewsBean;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.StatusBarHelper;

/**
 * Created by Jerome on 2018/7/5
 */
public class NewsWebActivity extends BaseActivity {

    private NewsDetailFragment detailFragment;
    private String currentTitle;
    private String url = "";
    private Object obj;
    public static final String NEWS_TYPE = "news_type";
    public static final String NEWS_OBJECT = "news_object";
    public static final String NEWS_TITLE = "news_title";
    public static final int NEWS_WEB_URL = 0;
    public static final int NEWS_WEB_PARAMS = 1;

    private boolean isPopupShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_web);

        showRightView(R.drawable.chat_more, view -> {
            showFunction();
        });

        if (hasKitKat() && !hasLollipop()) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        View view = findViewById(R.id.title);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.topMargin = 0;
        view.setLayoutParams(layoutParams);
        StatusBarHelper.setStatusBarLightMode(mActivity);

        obj = getIntent().getSerializableExtra(NEWS_OBJECT);
        if (obj instanceof NewsBean) {
            url = ((NewsBean) obj).getUrl();

        } else if (obj instanceof HomeBean.Menu) {
            url = ((HomeBean.Menu) obj).getUrl();

        } else if (obj instanceof HomeBean.BannerBean) {
            url = ((HomeBean.BannerBean) obj).getUrl();

        } else if (obj instanceof HomeBean.UserMenuBean.UserMenu) {
            url = ((HomeBean.UserMenuBean.UserMenu) obj).getUrl();
        }
        currentTitle = getIntent().getStringExtra(NEWS_TITLE);
        setTitleCenter(getIntent().getStringExtra(NEWS_TITLE) == null ? "新闻详情" : currentTitle);

        Bundle bundle = new Bundle();
        detailFragment = new NewsDetailFragment();
        bundle.putInt(NEWS_TYPE, getIntent().getIntExtra(NEWS_TYPE, 0));
        bundle.putSerializable(NEWS_OBJECT, getIntent().getSerializableExtra(NEWS_OBJECT));
        bundle.putString("url", getIntent().getStringExtra("url"));
        detailFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.flContainer, detailFragment, NewsDetailFragment.class.getSimpleName())
                .commitAllowingStateLoss();
    }

    PopupWindow mPopWindow;

    public void showFunction() {
        isPopupShow = true;
        View popup = View.inflate(this, R.layout.popup_webfunc, null);
        showPopup(popup);
        mPopWindow = new PopupWindow(popup, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopWindow.setBackgroundDrawable(new ColorDrawable());
        mPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        handlePopup(popup, mPopWindow);
    }

    //给popup设置监听
    private void handlePopup(final View popup, final PopupWindow mPopWindow) {
        TextView tv_webfunc1 = popup.findViewById(R.id.tv_webfunc1);
        TextView tv_webfunc2 = popup.findViewById(R.id.tv_webfunc2);
        TextView tv_webfunc3 = popup.findViewById(R.id.tv_webfunc3);
        TextView tv_webfunc4 = popup.findViewById(R.id.tv_webfunc4);

        LinearLayout ll_popup_window = popup.findViewById(R.id.ll_popup_window);

        tv_webfunc1.setOnClickListener(v -> {
            //todo 刷新写在这里
            if (detailFragment != null) {
                detailFragment.reload();
            }
            mPopWindow.dismiss();
        });

        tv_webfunc2.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", url);
            cm.setPrimaryClip(mClipData);
            CommonUtils.showToastShort(this, "已复制到剪切板");
            mPopWindow.dismiss();
        });

        tv_webfunc3.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(Intent.createChooser(intent, "请选择浏览器"));

            mPopWindow.dismiss();
        });

        tv_webfunc4.setOnClickListener(v -> {
            initShare();
            mPopWindow.dismiss();
        });

        ll_popup_window.setOnClickListener(view -> mPopWindow.dismiss());
        mPopWindow.setOnDismissListener(() -> {
            backgroundAlpha(1f);
            isPopupShow = false;
        });
    }

    //更改activity背景透明度
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    //弹出popup
    private void showPopup(View popup) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnimation.setDuration(300);
        popup.startAnimation(translateAnimation);
        backgroundAlpha(0.6f);
    }

    private void initShare() {
        final UMImage thumb = new UMImage(mActivity, R.drawable.icon_single);
        UMWeb web = new UMWeb(url);
        web.setTitle(currentTitle);
        web.setThumb(thumb);
        web.setDescription("来自爱学学");
        new ShareAction(mActivity)
                .setDisplayList(new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE})
                .withMedia(web)
                .open();
    }

    @Override
    public void onBackPressed() {
        if (isPopupShow) {
            mPopWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }
}
