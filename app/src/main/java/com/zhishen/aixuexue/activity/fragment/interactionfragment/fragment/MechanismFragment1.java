package com.zhishen.aixuexue.activity.fragment.interactionfragment.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.ChatActivity;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.GetInstitutionsResponse;
import com.zhishen.aixuexue.http.AesCBC;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.ScreenUtil;

public class MechanismFragment1 extends Fragment {
    //机构详情页 第一个tab   机构介绍

    private RecyclerView recyclerView;
    private Adapter adapter;

    private GetInstitutionsResponse response;

    public void setData(GetInstitutionsResponse response) {
        this.response = response;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = new RecyclerView(getContext());
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return recyclerView;
    }

    FrameLayout fl_webview;

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mechanism_fragment1, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_item_mechanism_fragment1_location;
            TextView tv_item_mechanism_fragment1_kefu;

            ViewHolder(View itemView) {
                super(itemView);

                tv_item_mechanism_fragment1_location = itemView.findViewById(R.id.tv_item_mechanism_fragment1_location);
                tv_item_mechanism_fragment1_kefu = itemView.findViewById(R.id.tv_item_mechanism_fragment1_kefu);
                fl_webview = itemView.findViewById(R.id.fl_webview);

                String decrypt = AesCBC.getInstance().decrypt(response.getInfo().trim());

                initWebView(getContext(), decrypt);

                tv_item_mechanism_fragment1_location.setText(TextUtils.isEmpty(response.getLocation_name()) ? "暂无信息" : response.getLocation_name());
                tv_item_mechanism_fragment1_kefu.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(response.getServiceMobile())) {
                        CommonUtils.showAlertDialog(getActivity(), "是否拨打客服电话", response.getServiceMobile()
                                , new CommonUtils.OnDialogClickListener() {
                                    @Override
                                    public void onPriformClock() {
                                    }

                                    @Override
                                    public void onCancleClock() {
                                    }
                                });
                        return;
                    }

                    if (!TextUtils.isEmpty(response.getCustomerServiceID())) {
                        startActivity(new Intent(getContext(), ChatActivity.class).putExtra("userId", response.getCustomerServiceID()).putExtra("userNick", response.getName()));
                        return;
                    }

                    CommonUtils.showToastShort(getContext(), "暂时不可用");
                });
            }

            private void initWebView(Context context, String content) {
                mWebView = new WebView(context);
                mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);

                fl_webview.addView(mWebView, 0);

                webSettings = mWebView.getSettings();
                webSettings.setLoadsImagesAutomatically(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }

                webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
                webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

                webSettings.setUseWideViewPort(true);
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setBlockNetworkImage(false);
                webSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
                webSettings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
                webSettings.setDisplayZoomControls(true); //隐藏原生的缩放控件

                mWebView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        if (mWebView == null) {
                            return;
                        }
                        mWebView.postDelayed(() -> {
                            if (mWebView == null) {
                                return;
                            }
                            mWebView.measure(0, 0);
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWebView.getMeasuredHeight() == 0 ? ScreenUtil.getWindowHeight(getActivity()) : mWebView.getMeasuredHeight());
                            mWebView.setLayoutParams(layoutParams);
                        }, 150);
                    }
                });

                mWebView.loadDataWithBaseURL("about:blank", content, "text/html", "utf-8",
                        null);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    WebView mWebView;
    WebSettings webSettings;

    public void flush() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            fl_webview.removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
    }
}
