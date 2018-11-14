package com.zhishen.aixuexue.activity.fragment.minefragment.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.FollowBean;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.StringUtils;
import com.zhishen.aixuexue.weight.tag.FlowLayout;
import com.zhishen.aixuexue.weight.tag.TagAdapter;
import com.zhishen.aixuexue.weight.tag.TagFlowLayout;

/**
 * Created by Jerome on 2018/6/30
 */
public class FollowTchAdapter extends BaseQuickAdapter<FollowBean, BaseViewHolder> {

    public FollowTchAdapter() {
        super(R.layout.item_follow_tch, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, FollowBean item) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        ImageView ivIcon = helper.getView(R.id.ivIcon);
        TagFlowLayout mFlowLayout = helper.getView(R.id.flowLayout);

        if (TextUtils.isEmpty(item.getHeadUrl())) {
            ivAvatar.setImageResource(R.drawable.default_avatar);
        } else {
            BitmapUtil.loadCircleImg(ivAvatar, item.getHeadUrl(), R.drawable.default_avatar);
        }
        if (StringUtils.isEmpty(item.getIco())) {
            ivIcon.setVisibility(View.GONE);
        } else {
            BitmapUtil.loadImg(ivIcon, item.getIco());
        }
        mFlowLayout.setAdapter(new TagAdapter<String>(item.getCourse()) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_tag,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });
        helper.setText(R.id.tvName, item.getName()).setText(R.id.tvContent, item.getSummary());
            helper.setText(R.id.tvFocus, "取消关注").addOnClickListener(R.id.tvFocus);
    }
}
