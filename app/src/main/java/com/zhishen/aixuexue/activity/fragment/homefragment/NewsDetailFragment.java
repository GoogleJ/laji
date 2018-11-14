package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.FragmentEvent;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseLazyFragment;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.NewsTypeAdapter;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.bean.MultiTypeItem;
import com.zhishen.aixuexue.bean.NewsBean;
import com.zhishen.aixuexue.bean.NewsComment;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.weight.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Jerome on 2018/7/18
 */
@SuppressLint("CheckResult")
public class NewsDetailFragment extends BaseLazyFragment {

    private ValueAnimator pbAnim;
    private int newType = 0;
    private String currentUrl;
    private NewsTypeAdapter mAdapter;
    private List<MultiTypeItem> list = new ArrayList<>();
    @BindView(R.id.etContent) EditText etContent;
    @BindView(R.id.mWebView) WebView urlWebView;
    @BindView(R.id.proView) ProgressView proView;
    @BindView(R.id.flContainer) FrameLayout flContainer;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.llInputBottom) LinearLayoutCompat llInputBottom;
    @BindView(R.id.btnSend) Button btnSend;
    private Object obj;
    private NewsBean newsBean;
    private String newsTypeId;
    private boolean isLoadFinish;
    public static final String NEWS_TYPE = "news_type";
    public static final String NEWS_OBJECT = "news_object";
    public static final int NEWS_WEB_URL = 0;
    public static final int NEWS_WEB_PARAMS = 1;

    @Override
    protected void initDependencies() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_news_detail;
    }

    @Override
    protected void initEventView() {
        setUserVisibleHint(true);

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) {
                    btnSend.setBackgroundResource(R.drawable.shape_btn_news2);
                } else {
                    btnSend.setBackgroundResource(R.drawable.shape_btn_news);
                }
            }
        });

        pbAnim = ValueAnimator.ofFloat(0, 70);
        pbAnim.setDuration(3000);
        pbAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        pbAnim.addUpdateListener(animation -> proView.setProgress((Float) animation.getAnimatedValue()));
        pbAnim.start();
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter = new NewsTypeAdapter());
        //单web界面和列表公用监听
        mAdapter.setOnWebViewListener(new NewsTypeAdapter.OnWebViewListener() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pbAnim.cancel();
                if (proView != null) {
                    proView.doFinish();
                }
                if (newProgress == 100){
                    isLoadFinish = true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (proView.getVisibility() == View.INVISIBLE) {
                    proView.setProgress(0);
                    proView.setVisibility(View.VISIBLE);
                    pbAnim.start();
                }
            }
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            NewsBean newsBean = (NewsBean) mAdapter.getData().get(position).getData();
            if (view.getId() == R.id.tvCount) {
                TextView tvCount = view.findViewById(R.id.tvCount);
                praise(tvCount,newsBean);
            }
        });

        String url = null;
        if (newType == NEWS_WEB_URL){
            mRecyclerView.setVisibility(View.GONE);
            llInputBottom.setVisibility(View.GONE);
            urlWebView.setVisibility(View.VISIBLE);
            obj = getArguments().getSerializable(NEWS_OBJECT);
            if (obj instanceof NewsBean){
                url = ((NewsBean) obj).getUrl();
            } else if (obj instanceof HomeBean.UserMenuBean.UserMenu) {
                url = ((HomeBean.UserMenuBean.UserMenu)obj).getUrl();
            } else if (obj instanceof HomeBean.BannerBean){
                url = ((HomeBean.BannerBean) obj).getUrl();
            } else if (obj instanceof HomeBean.UserMenuBean.UserMenu){
                url = ((HomeBean.UserMenuBean.UserMenu)obj).getUrl();
            }else {
                url= getArguments().getString("url");
            }
            mAdapter.initWebView(urlWebView, url,true);
        } else if (newType == NEWS_WEB_PARAMS) {
            mRecyclerView.setVisibility(View.VISIBLE);
            urlWebView.setVisibility(View.GONE);
        }
        if (newType == NEWS_WEB_PARAMS) {
            mAdapter.setNewData(list);
        }
    }

    public void reload() {
        urlWebView.reload();
    }

    @OnClick(R.id.btnSend) void onMethodClick() {
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            toast("发送内容不能为空");
            return;
        }
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, mContext)
                .addNewsComment(ParamsMap.addNewsComment(newsBean.getId(), content))
                .compose(RxSchedulers.ioFlowable())
                .compose(bindUntilEvent(FragmentEvent.PAUSE))
                .compose(RxSchedulers.flowTransformer())
                .subscribe(s -> {
                    etContent.getText().clear();
                    hideInputSoft();
                    toast("评论成功");
                    mAdapter.addNewComment(content);
                }, t -> toast(RxException.getMessage(t)));
    }

    /**
     * 点赞
     */
    private void praise(TextView tvCount, NewsBean bean) {
        newsTypeId = bean.getId();
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, mContext)
                .setPraise(ParamsMap.getPraise(newsTypeId))
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .compose(bindToLifecycle())
                .subscribe(newsBean -> {
                    if (null == newsBean) return;
                    if (isLoadFinish){
                        if (newsBean.isFlag()) {
                            int num = Integer.parseInt(this.newsBean.getUp());
                            tvCount.setText(String.valueOf(++num));
                            toast("点赞成功");
                        } else {
                            toast("已经点赞过了");
                        }
                    }
                }, t -> toast(RxException.getMessage(t)));
    }

    @Override
    protected void getDataFromServer() {
        list.clear();
        newType = getArguments().getInt(NEWS_TYPE, newType);
        obj = getArguments().getSerializable(NEWS_OBJECT);
        if (newType == NEWS_WEB_URL) {
            if (obj instanceof HomeBean.Menu) {
                HomeBean.Menu menu = (HomeBean.Menu) obj;
                currentUrl = menu.getUrl();
            }
        } else {
            if (obj instanceof NewsBean) {
                newsBean = (NewsBean) obj;
            }
            list.add(new MultiTypeItem<>(MultiTypeItem.NEWS_DETAIL_HEADER, newsBean));
            loadComments();
        }
    }

    private void loadComments() {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, mContext)
                .getNewsComment(ParamsMap.getNewsComment(1, newsBean.getId()))
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .compose(bindUntilEvent(FragmentEvent.PAUSE))
                .subscribe(comments ->  showNewsDetail(comments), t -> {
                    toast(RxException.getMessage(t));
                    t.printStackTrace();
                });
    }

    private void showNewsDetail(NewsComment comments) {
        if (null != comments.list && !comments.list.isEmpty()) {
            list.add(new MultiTypeItem<>(MultiTypeItem.NEWS_DETAIL_COMMENT, comments));
        }
    }

    private void hideInputSoft(){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etContent.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        proView.cancel();
        pbAnim.cancel();
        flContainer.removeAllViews();
        super.onDestroyView();
    }
}
