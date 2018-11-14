package com.zhishen.aixuexue.activity.fragment.minefragment.user.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.SystemNoticeBean;
import com.zhishen.aixuexue.util.BitmapUtil;

/**
 * Created by Jerome on 2018/7/5
 */
public class SystemNoticeAdapter extends BaseQuickAdapter<SystemNoticeBean, BaseViewHolder> {

    public SystemNoticeAdapter() {
        super(R.layout.item_sys_notice, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, SystemNoticeBean item) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        BitmapUtil.loadCircleImg(ivAvatar, item.getIco(), R.mipmap.ic_sys_notice);
        helper.setText(R.id.tvTitle, item.getTitle()).setText(R.id.tvTime, item.getDate())
                .setText(R.id.tvContent, item.getContent()).setGone(R.id.ivTips, !item.isRead());
    }
}
