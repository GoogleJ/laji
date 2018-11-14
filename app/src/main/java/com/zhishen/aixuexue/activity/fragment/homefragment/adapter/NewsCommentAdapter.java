package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.NewsComment;
import com.zhishen.aixuexue.util.BitmapUtil;

import java.util.List;

/**
 * Created by Jerome on 2018/7/24
 */
public class NewsCommentAdapter extends BaseQuickAdapter<NewsComment,BaseViewHolder> {


    public NewsCommentAdapter(@Nullable List<NewsComment> data) {
        super(R.layout.item_news_comment,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsComment item) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        BitmapUtil.loadCircleImg(ivAvatar, item.headUrl, R.drawable.default_avatar);
        helper.setText(R.id.tvName, item.user_name).setText(R.id.tvContent, item.content).setText(R.id.tvTime, item.date);
    }
}
