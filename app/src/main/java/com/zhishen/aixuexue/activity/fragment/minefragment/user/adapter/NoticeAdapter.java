package com.zhishen.aixuexue.activity.fragment.minefragment.user.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.HomeNoticeBean;
import com.zhishen.aixuexue.util.BitmapUtil;

/**
 * Created by Jerome on 2018/7/2
 */
public class NoticeAdapter extends BaseQuickAdapter<HomeNoticeBean.NoticeBean, BaseViewHolder> {

    public NoticeAdapter() {
        super(R.layout.item_home_notice, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeNoticeBean.NoticeBean item) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        BitmapUtil.loadCircleImg(ivAvatar, item.getIco(), R.drawable.default_avatar);
        helper.setText(R.id.tvName, item.getTitle())
                .setText(R.id.tvTime, item.getDate()).setText(R.id.tvContent, item.getDesc());
    }
}
