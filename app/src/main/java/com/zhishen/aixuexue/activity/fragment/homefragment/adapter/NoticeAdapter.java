package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.HomeNoticeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jerome on 2018/6/28
 */
public class NoticeAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInfalter;
    private List<HomeNoticeBean.NoticeBean> noticeBeans;

    public NoticeAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInfalter = LayoutInflater.from(mContext);
    }

    public void addData(List<HomeNoticeBean.NoticeBean> data) {
        this.noticeBeans = data == null ? new ArrayList<>() : data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return noticeBeans == null ? 0 : noticeBeans.size();
    }

    @Override
    public HomeNoticeBean.NoticeBean getItem(int position) {
        return noticeBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        NoticeHolder holder;
        if (view == null) {
            view = mInfalter.inflate(R.layout.item_notice, null);
            holder = new NoticeHolder(view);
            view.setTag(holder);
        } else {
            holder = (NoticeHolder) view.getTag();
        }
        holder.tvTitle.setText(noticeBeans.get(0).getTitle());
        holder.tvTime.setText(noticeBeans.get(0).getDate());

        if (noticeBeans.size() > 1) {
            holder.llContent.setVisibility(View.VISIBLE);
            holder.tvTitle1.setText(noticeBeans.get(1).getTitle());
            holder.tvTime1.setText(noticeBeans.get(1).getDate());
        } else {
            holder.llContent.setVisibility(View.GONE);
        }
        return view;
    }

    class NoticeHolder {
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvTitle1) TextView tvTitle1;
        @BindView(R.id.tvTime) TextView tvTime;
        @BindView(R.id.tvTime1) TextView tvTime1;
        @BindView(R.id.llContent) LinearLayout llContent;

        public NoticeHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
