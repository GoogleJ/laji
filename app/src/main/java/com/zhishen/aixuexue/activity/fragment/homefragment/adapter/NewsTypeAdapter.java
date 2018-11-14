package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.MultiTypeItem;
import com.zhishen.aixuexue.bean.NewsBean;
import com.zhishen.aixuexue.bean.NewsComment;
import com.zhishen.aixuexue.http.AesCBC;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.DateUtils;
import com.zhishen.aixuexue.util.ViewUtils;
import com.zhishen.aixuexue.weight.NoScrollWebView;
import com.zhishen.aixuexue.weight.manager.LinearLayoutManagerPlus;

/**
 * Created by Jerome on 2018/7/18
 */
public class NewsTypeAdapter extends BaseMultiItemQuickAdapter<MultiTypeItem, BaseViewHolder> {

    private WebSettings mWebSettings;
    private RecyclerView rvContent;
    private NewsCommentAdapter commentAdapter;
    private OnWebViewListener onWebViewListener;

    public NewsTypeAdapter() {
        super(null);
        addItemType(MultiTypeItem.NEWS_DETAIL_HEADER, R.layout.view_news_header_dt);
        addItemType(MultiTypeItem.NEWS_DETAIL_COMMENT, R.layout.view_news_comment_dt);
    }

    public void setOnWebViewListener(OnWebViewListener onWebViewListener) {
        if (onWebViewListener == null) {
            throw new NullPointerException("listener is null");
        }
        this.onWebViewListener = onWebViewListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiTypeItem item) {
        switch (helper.getItemViewType()) {
            case MultiTypeItem.NEWS_DETAIL_HEADER:
                NewsBean newsBean = (NewsBean) item.getData();
                if (null == newsBean) return;
                NoScrollWebView mWebView = helper.getView(R.id.mWebView);

                initWebView(mWebView, AesCBC.getInstance().decrypt(newsBean.getActionidparameter()), false);
                helper.setText(R.id.tvTitle, newsBean.getTitle())
                        .setText(R.id.tvSource, newsBean.getSource()).setText(R.id.tvTime, newsBean.getDate())
                        .setText(R.id.tvCount, newsBean.getUp()).addOnClickListener(R.id.tvCount);
                break;
            case MultiTypeItem.NEWS_DETAIL_COMMENT:
                NewsComment comment = (NewsComment) item.getData();

                helper.setText(R.id.tvCount, "全部评论 (" + comment.list.size() + ")");
                rvContent = helper.getView(R.id.rvContent);
                rvContent.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                rvContent.setLayoutManager(new LinearLayoutManagerPlus(mContext));
                rvContent.setAdapter(commentAdapter = new NewsCommentAdapter(comment.list));
                commentAdapter.setEmptyView(ViewUtils.getCommEmptyRes((Activity) mContext, rvContent, "暂无评论", R.mipmap.ic_sys_empty));
                break;
        }
    }

    public void initWebView(WebView mWebView, String currentUrl, boolean isURL) {
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebSettings.setUseWideViewPort(true);
        //允许js代码
        mWebSettings.setJavaScriptEnabled(true);
        //允许SessionStorage/LocalStorage存储
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setBlockNetworkImage(false);
        //缩放操作
        mWebSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        mWebSettings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        mWebSettings.setDisplayZoomControls(true); //隐藏原生的缩放控件
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    if (onWebViewListener != null) {
                        onWebViewListener.onProgressChanged(view, newProgress);
                    }

                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (onWebViewListener != null) {
                    onWebViewListener.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null)
                    return false;
                view.loadUrl(url);
                return true;
            }
        });
        if (isURL) {
            mWebView.loadUrl(currentUrl);
        } else {
            mWebView.loadDataWithBaseURL(null, currentUrl, "text/html", "utf-8", null);
        }
    }

    public void addNewComment(String content) {
        commentAdapter.addData(0, new NewsComment(AiApp.getInstance().getUserAvatar(),
                AiApp.getInstance().getUserNick(), AiApp.getInstance().getUsername(), 0, content, DateUtils.getStringTime(System.currentTimeMillis())));
        rvContent.smoothScrollToPosition(commentAdapter.getItemCount() - 1);
    }

    public interface OnWebViewListener {

        void onProgressChanged(WebView view, int newProgress);

        void onPageStarted(WebView view, String url, Bitmap favicon);
    }
}
