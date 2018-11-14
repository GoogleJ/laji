package com.zhishen.aixuexue.activity.fragment.minefragment.user.adapter;

import android.graphics.Paint;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.CollectBean;
import com.zhishen.aixuexue.util.BitmapUtil;

/**
 * Created by Jerome on 2018/6/30
 */
public class MineCollectAdapter extends BaseQuickAdapter<CollectBean, BaseViewHolder> {


    public MineCollectAdapter() {
        super(R.layout.item_user_collect, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectBean item) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        if (TextUtils.isEmpty(item.getImage())) {
            ivAvatar.setImageResource(R.drawable.default_image);
        } else {
            BitmapUtil.loadCornerImg(ivAvatar, item.getImage(), R.drawable.default_image,5);
        }

        helper.setText(R.id.tvTitle, item.getTitle())
                .setText(R.id.tvName, item.getTag() + "\t\t" + item.getStudyCount() + "人在学")
                .setText(R.id.tvCount, item.getActivityPrice());
        TextView price = helper.getView(R.id.tvPrice);
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        price.setText(item.getOriginalPrice());
    }
}
