package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.AddCommentRequest;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.Api;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.GetCommentResponse;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.RatingBar;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCommentActivity extends BaseActivity {
    private ImageView iv_addcomment_head;
    private RatingBar ratingBar;
    private EditText et_addcomment;
    private TextView tv_addcomment_invisiable;

    private boolean isVisiable = true;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        id = getIntent().getStringExtra("id");

        iv_addcomment_head = findViewById(R.id.iv_addcomment_head);
        ratingBar = findViewById(R.id.ratingBar);
        et_addcomment = findViewById(R.id.et_addcomment);
        tv_addcomment_invisiable = findViewById(R.id.tv_addcomment_invisiable);

        BitmapUtil.loadCircleImg(iv_addcomment_head, AiApp.getInstance().getUserAvatar(), R.drawable.default_avatar);
    }

    public void updateMode(View view) {
        Drawable drawable;
        if (isVisiable) {
            drawable = ContextCompat.getDrawable(AddCommentActivity.this, R.drawable.ic_addcomment_visiable);
        } else {
            drawable = ContextCompat.getDrawable(AddCommentActivity.this, R.drawable.ic_addcomment_invisiable);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv_addcomment_invisiable.setCompoundDrawables(drawable, null, null, null);
        isVisiable = !isVisiable;
    }

    @SuppressLint("CheckResult")
    public void submit(View view) {

        String content = et_addcomment.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            CommonUtils.showToastShort(this, "请输入评论内容");
            return;
        }

        int score = ratingBar.getCurrentStars();
        if (score == 0) {
            CommonUtils.showToastShort(this, "请评分");
            return;
        }

        AddCommentRequest request = new AddCommentRequest();

        request.setContent(content);
        request.setFromId(AiApp.getInstance().getUsername());
        request.setTargeType("3");
        request.setTargetId(id);
        request.setScore(score);
        request.setStatus(isVisiable ? 0 : 1);

        ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, this)
                .addComment(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(stringBaseResponseDataT -> {
                    if (stringBaseResponseDataT.code != 0) {
                        CommonUtils.showToastShort(AddCommentActivity.this, stringBaseResponseDataT.msg);
                        return;
                    }

                    CommonUtils.showToastShort(AddCommentActivity.this, "评论成功");

                    Intent mIntent = new Intent();
                    GetCommentResponse.ListBean listBean = new GetCommentResponse.ListBean();
                    listBean.setContent(content);
                    listBean.setHeadUrl(AiApp.getInstance().getUserAvatar());
                    listBean.setScore(score);
                    listBean.setUser_id(AiApp.getInstance().getUsername());
                    if (isVisiable) {
                        listBean.setUser_name(AiApp.getInstance().getUserNick());
                    } else {
                        String tel = AiApp.getInstance().getUsertel();
                        String substring1 = tel.substring(0, 3);
                        String substring2 = tel.substring(7, 11);

                        listBean.setUser_name(substring1 + "****" + substring2);
                    }
                    listBean.setDate(new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));

                    mIntent.putExtra("item", listBean);
                    setResult(1, mIntent);
                    finish();
                }, throwable -> {
                    CommonUtils.showToastShort(AddCommentActivity.this, "评论失败，请重试");
                    throwable.printStackTrace();
                });
    }

    public void back(View view) {
        finish();
    }
}
