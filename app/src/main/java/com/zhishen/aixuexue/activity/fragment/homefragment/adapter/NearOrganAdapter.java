package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.NearOrganBean;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.weight.RatingBar;
import com.zhishen.aixuexue.weight.tag.FlowLayout;
import com.zhishen.aixuexue.weight.tag.TagAdapter;
import com.zhishen.aixuexue.weight.tag.TagFlowLayout;

/**
 * Created by Jerome on 2018/7/5
 */
public class NearOrganAdapter extends BaseQuickAdapter<NearOrganBean, BaseViewHolder> {


    public NearOrganAdapter() {
        super(R.layout.item_near_organ, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, NearOrganBean item) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        RatingBar ratingBar = helper.getView(R.id.ratingBar);
        TagFlowLayout mFlowLayout = helper.getView(R.id.flowLayout);

        BitmapUtil.loadNormalImg(ivAvatar, item.getHeadUrl(), R.drawable.default_image);
        ratingBar.setStar(Float.valueOf(item.getScore()));

        helper.setText(R.id.tvTitle, item.getName()).setText(R.id.tvLocal, item.getSummary()).setText(R.id.tvDistance, item.getDistance());
        mFlowLayout.setAdapter(new TagAdapter<String>(item.getTag()) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_tag,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });
    }
}
