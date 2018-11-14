package com.zhishen.aixuexue.activity.fragment.minefragment.user.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.CouponBean;

/**
 * Created by Jerome on 2018/7/2
 */
public class CouponAdapter extends BaseQuickAdapter<CouponBean, BaseViewHolder> {


    public CouponAdapter() {
        super(R.layout.item_usr_coupon, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, CouponBean item) {
        helper.setText(R.id.tvPrice, item.getPrice()).setText(R.id.tvTitle, item.getTitle()).setText(R.id.tvTime, item.getDate())
                .setText(R.id.tvContent, item.getDesc()).setText(R.id.tvDesc, item.getSource());
    }
}
