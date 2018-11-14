package com.zhishen.aixuexue.activity.fragment.minefragment.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.FollowBean;
import com.zhishen.aixuexue.util.BitmapUtil;

/**
 * Created by Jerome on 2018/6/30
 */
public class FollowOrganAdapter extends BaseQuickAdapter<FollowBean,BaseViewHolder> {


    public FollowOrganAdapter() {
        super(R.layout.item_follow_organ,null);
    }

    @Override
    protected void convert(BaseViewHolder helper, FollowBean item) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        ImageView ivIcon = helper.getView(R.id.ivIcon);

        BitmapUtil.loadCircleImg(ivAvatar, item.getHeadUrl(),R.drawable.default_avatar);
        BitmapUtil.loadImg(ivIcon, item.getIco());
        helper.setText(R.id.tvName, item.getName()).setText(R.id.tvContent, item.getSummary()).addOnClickListener(R.id.tvFocus);

        if (item.isFocus()){
            helper.setText(R.id.tvFocus, "取消关注");
        }
    }
}
