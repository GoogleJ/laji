package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.graphics.Paint;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.DiscountBean;
import com.zhishen.aixuexue.util.BitmapUtil;

/**
 * Created by Jerome on 2018/7/5
 */
public class DiscountAdapter extends BaseQuickAdapter<DiscountBean,BaseViewHolder> {


    public DiscountAdapter() {
        super(R.layout.item_discount,null);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiscountBean item) {
        ImageView ivPhoto= helper.getView(R.id.ivPhoto);
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        TextView tvTag = helper.getView(R.id.tvTag);
        BitmapUtil.loadCircleImg(ivAvatar,item.getHeadUrl(), R.drawable.default_avatar);
        BitmapUtil.loadNormalImg(ivPhoto, item.getActivityImageUrl(), R.drawable.default_image);
        helper.setText(R.id.tvName,item.getTitle());
        tvTag.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvTag.getPaint().setAntiAlias(true);//抗锯齿
    }
}
