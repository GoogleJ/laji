package com.zhishen.aixuexue.activity.fragment.worldfragment.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.LocalBean;


/**
 * Created by Jerome on 2018/7/14
 */
public class WorldLocalAdapter extends BaseQuickAdapter<LocalBean, BaseViewHolder> {


    public WorldLocalAdapter() {
        super(R.layout.view_world_near_local,null);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalBean item) {
        helper.setChecked(R.id.rbBtn,item.isSelected())
                .setText(R.id.tvTitle, item.getPoiItem().getTitle())
                .setText(R.id.tvContent, item.getPoiItem().getProvinceName() + item.getPoiItem().getCityName()
                        + item.getPoiItem().getAdName() + item.getPoiItem().getSnippet());
    }
}
