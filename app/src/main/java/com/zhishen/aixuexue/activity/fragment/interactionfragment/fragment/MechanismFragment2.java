package com.zhishen.aixuexue.activity.fragment.interactionfragment.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.AddCommentActivity;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.Api;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.GetCommentRequest;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.GetCommentResponse;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.ScreenUtil;
import com.zhishen.aixuexue.weight.RatingBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MechanismFragment2 extends Fragment {
    //机构详情页 第二个tab   评论

    private List<GetCommentResponse.ListBean> dataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter adapter;

    private String id;

    public void setDataList(String id) {
        this.id = id;
    }

    int currentPage = 0;

    private View rootView;
    private View view_notice_empty;

    private double score;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_mechanism2, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerview);
        view_notice_empty = rootView.findViewById(R.id.view_notice_empty);

        view_notice_empty.setPadding(0, ScreenUtil.dip2px(getContext(), 48), 0, 0);

        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        getData();

        return rootView;
    }

    @SuppressLint("CheckResult")
    private void getData() {
        GetCommentRequest request = new GetCommentRequest();
        request.setPageNo(currentPage + 1);
        request.setTargetId(id);
        request.setTargetType("3");
        ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, getContext())
                .getAppCommentList(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponseDataT -> {
                    if (baseResponseDataT.code != 0) {
                        CommonUtils.showToastShort(getContext(), baseResponseDataT.msg);
                        return;
                    }

                    int preIndex = dataList.size() == 0 ? 0 : dataList.size() - 1;
                    int preSize = dataList.size();
                    dataList.addAll(baseResponseDataT.data.getList());

                    score = baseResponseDataT.data.getScore();

                    if (preSize == 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyItemRangeInserted(preIndex, dataList.size() - preSize);
                        adapter.notifyItemChanged(0);
                    }
                    currentPage += 1;

                    if (dataList.size() == 0) {
                        view_notice_empty.setVisibility(View.VISIBLE);
                    } else {
                        view_notice_empty.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    CommonUtils.showToastShort(getContext(), "未知错误");
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("1212", "========");
        if (requestCode == 1 && resultCode == 1) {
            GetCommentResponse.ListBean listBean = (GetCommentResponse.ListBean) data.getSerializableExtra("item");
            dataList.add(0, listBean);
            adapter.notifyItemInserted(1);

            view_notice_empty.setVisibility(View.GONE);
        }

    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new ViewHolder1(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mechanism_fragment2_head, parent, false));
            }
            return new ViewHolder2(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mechanism_fragment2_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case 1:
                    ViewHolder1 viewHolder1 = (ViewHolder1) holder;
                    viewHolder1.initData();
                    break;
                default:
                    ViewHolder2 viewHolder2 = (ViewHolder2) holder;
                    viewHolder2.initData();
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size() == 0 ? 1 : dataList.size() + 1;
        }

        //1:head  2:list
        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 1 : 2;
        }

        class ViewHolder1 extends RecyclerView.ViewHolder {
            TextView tv_item_mechanism_fragment2_addcomment;
            TextView tv_item_mechanism_fragment2_score;
            RatingBar ratingbar;

            ViewHolder1(View itemView) {
                super(itemView);
                tv_item_mechanism_fragment2_addcomment = itemView.findViewById(R.id.tv_item_mechanism_fragment2_addcomment);
                tv_item_mechanism_fragment2_score = itemView.findViewById(R.id.tv_item_mechanism_fragment2_score);
                ratingbar = itemView.findViewById(R.id.ratingbar);

                tv_item_mechanism_fragment2_addcomment.setOnClickListener(v -> {
                            Intent intent = new Intent(getContext(), AddCommentActivity.class);
                            intent.putExtra("id", id);
                            startActivityForResult(intent, 1);
                        }
                );
            }

            private void initData() {
                if (dataList.size() != 0) {
                    ratingbar.setStar((float) score);

                    tv_item_mechanism_fragment2_score.setText((float)score + "分");
                } else {
                    ratingbar.setStar(0);

                    tv_item_mechanism_fragment2_score.setText("暂无评分");
                }
            }
        }


        class ViewHolder2 extends RecyclerView.ViewHolder {
            ImageView iv_item_mechanism_fragment2_head;
            TextView tv_item_mechanism_fragment2_head;
            TextView tv_item_mechanism_fragment2_date;
            TextView tv_item_mechanism_fragment2_score;
            TextView tv_item_mechanism_fragment2_content;
            View view_item_mechanism_fragment2_divider;
            View view_item_mechanism_fragment2_dividertop;
            RatingBar ratingBar;

            ViewHolder2(View itemView) {
                super(itemView);
                iv_item_mechanism_fragment2_head = itemView.findViewById(R.id.iv_item_mechanism_fragment2_head);
                tv_item_mechanism_fragment2_head = itemView.findViewById(R.id.tv_item_mechanism_fragment2_head);
                tv_item_mechanism_fragment2_date = itemView.findViewById(R.id.tv_item_mechanism_fragment2_date);
                tv_item_mechanism_fragment2_score = itemView.findViewById(R.id.tv_item_mechanism_fragment2_score);
                tv_item_mechanism_fragment2_content = itemView.findViewById(R.id.tv_item_mechanism_fragment2_content);
                view_item_mechanism_fragment2_divider = itemView.findViewById(R.id.view_item_mechanism_fragment2_divider);
                view_item_mechanism_fragment2_dividertop = itemView.findViewById(R.id.view_item_mechanism_fragment2_dividertop);
                ratingBar = itemView.findViewById(R.id.ratingBar);
            }

            void initData() {
                if (getAdapterPosition() == 1) {
                    view_item_mechanism_fragment2_dividertop.setVisibility(View.VISIBLE);
                } else {
                    view_item_mechanism_fragment2_dividertop.setVisibility(View.GONE);
                }

                GetCommentResponse.ListBean listBean = dataList.get(getAdapterPosition() - 1);

                if (listBean.getHeadUrl() == null && listBean.getHeadUrl().length() == 0) {
                    iv_item_mechanism_fragment2_head.setImageResource(R.drawable.default_avatar);
                } else {
                    BitmapUtil.loadCircleImg(iv_item_mechanism_fragment2_head, listBean.getHeadUrl(), R.drawable.default_avatar);
                }

                tv_item_mechanism_fragment2_head.setText(listBean.getUser_name());
                tv_item_mechanism_fragment2_content.setText(listBean.getContent());
                tv_item_mechanism_fragment2_date.setText(listBean.getDate());

                ratingBar.setStar(listBean.getScore());
            }
        }
    }
}
