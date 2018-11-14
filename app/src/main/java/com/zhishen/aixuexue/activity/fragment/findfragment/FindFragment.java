package com.zhishen.aixuexue.activity.fragment.findfragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.utils.MessageUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.group.CreateGroupsActivity;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.ChatActivity;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.GroupListRequest;
import com.zhishen.aixuexue.http.response.GroupListRespone;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 互动界面
 * Created by yangfaming on 2018/6/15.
 */

public class FindFragment extends Fragment {
    RecyclerView listview_find;
    Adapter adapter;
    ArrayList<FindResponse> dataList = new ArrayList<>();
    TextView tv_find_create_subject;
    View view;
    private SmartRefreshLayout refreshLayout;
    private HTGroup htGroup;
    private List<HTGroup> allGroups;
    private HeadAdapter headAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find, container, false);
        initView();
        return view;
    }

    private void initView() {
        listview_find = view.findViewById(R.id.listview_find);
        tv_find_create_subject = view.findViewById(R.id.tv_find_create_subject);
        refreshLayout = view.findViewById(R.id.SmartRefreshLayout);
        adapter = new Adapter();
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(refreshLayout1 -> {
            allGroups = HTClient.getInstance().groupManager().getAllGroups();
            initData();
        });
        tv_find_create_subject.setOnClickListener(view1 -> {
            startActivityForResult(new Intent(getContext(), CreateGroupsActivity.class), 10001);
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout1 -> refreshLayout.finishLoadMoreWithNoMoreData());
    }

    @SuppressLint("CheckResult")
    private void initData() {
        Location latlng = LocalUserManager.getInstance().getLatlng();
        double latitude = 34.237139;
        double longitude = 108.895641;
        if (latlng != null) {
            latitude = latlng.getLatitude();
            longitude = latlng.getLongitude();
        }
        GroupListRequest request = new GroupListRequest(AiApp.getInstance().getUsername(), latitude, longitude);

        ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, getContext())
                .listGroup(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    refreshLayout.finishRefresh();
                    List<GroupListRespone> data = response.data;
                    dataList.clear();
                    for (GroupListRespone bean : data) {
                        dataList.addAll(bean.getListGroupforAndroid());
                    }
                    listview_find.setAdapter(adapter);
                    listview_find.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                }, throwable -> {
                    refreshLayout.finishRefresh();
                });
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final int TYPE_HEAD = 1;
        final int TYPE_NORMAL = 2;

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEAD;
            }
            return TYPE_NORMAL;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEAD) {
                return new HeadHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_head, parent, false));
            } else {
                return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HEAD) {
                HeadHolder headHolder = (HeadHolder) holder;
                headHolder.initData();
            } else {
                NormalHolder normarl = (NormalHolder) holder;
                normarl.initData(dataList.get(position - 1));
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size() + 1;
        }
    }

    class NormalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View divider_item_find_hottop;
        View divider_bottom;
        LinearLayout ll_item_find_titleview;
        View divider_item_find_titleview;
        TextView tv_item_find_titleview_title;
        TextView tv_item_find_titleview_more;
        ImageView cir_item_find_head;
        TextView tv_item_find_apply;
        TextView tv_item_find_contentview_title;
        TextView tv_item_find_contentview_groupcount;
        TextView tv_item_find_type_pay;
        TextView tv_item_find_type_private;
        TextView tv_item_find_contentview_summary;
        RelativeLayout rl_item_find_content;

        NormalHolder(View itemView) {
            super(itemView);
            divider_item_find_hottop = itemView.findViewById(R.id.divider_item_find_hottop);
            rl_item_find_content = itemView.findViewById(R.id.rl_item_find_content);
            divider_bottom = itemView.findViewById(R.id.divider_bottom);
            ll_item_find_titleview = itemView.findViewById(R.id.ll_item_find_titleview);
            divider_item_find_titleview = itemView.findViewById(R.id.divider_item_find_titleview);
            tv_item_find_titleview_title = itemView.findViewById(R.id.tv_item_find_titleview_title);
            tv_item_find_titleview_more = itemView.findViewById(R.id.tv_item_find_titleview_more);
            cir_item_find_head = itemView.findViewById(R.id.cir_item_find_head);
            tv_item_find_apply = itemView.findViewById(R.id.tv_item_find_apply);
            tv_item_find_contentview_title = itemView.findViewById(R.id.tv_item_find_contentview_title);
            tv_item_find_contentview_groupcount = itemView.findViewById(R.id.tv_item_find_contentview_groupcount);
            tv_item_find_type_pay = itemView.findViewById(R.id.tv_item_find_type_pay);
            tv_item_find_type_private = itemView.findViewById(R.id.tv_item_find_type_private);
            tv_item_find_contentview_summary = itemView.findViewById(R.id.tv_item_find_contentview_summary);
        }

        private void initData(FindResponse findResponse) {

            if (getAdapterPosition() >= 2 && findResponse.getType().equals(dataList.get(getAdapterPosition() - 2).getType())) {
                ll_item_find_titleview.setVisibility(View.GONE);
                divider_item_find_titleview.setVisibility(View.GONE);
                divider_item_find_hottop.setVisibility(View.GONE);
            } else {
                ll_item_find_titleview.setVisibility(View.VISIBLE);
                divider_item_find_titleview.setVisibility(View.VISIBLE);
                divider_item_find_hottop.setVisibility(View.VISIBLE);
            }

            if (getAdapterPosition() == dataList.size()) {
                divider_bottom.setVisibility(View.INVISIBLE);
            } else {
                if (findResponse.getType().equals(dataList.get(getAdapterPosition()).getType())) {
                    divider_bottom.setVisibility(View.VISIBLE);
                } else {
                    divider_bottom.setVisibility(View.INVISIBLE);
                }
            }

            tv_item_find_contentview_groupcount.setText(findResponse.getGroupCount() + "");

            if (findResponse.getGroupType().equals("公开群")) {
                tv_item_find_type_pay.setVisibility(View.VISIBLE);
                tv_item_find_type_private.setVisibility(View.GONE);
            } else {
                tv_item_find_type_pay.setVisibility(View.GONE);
                tv_item_find_type_private.setVisibility(View.VISIBLE);
            }

            BitmapUtil.loadCircleImg(cir_item_find_head, findResponse.getHeadUrl(), R.drawable.default_avatar);
            tv_item_find_titleview_title.setText(findResponse.getType());
            tv_item_find_contentview_title.setText(findResponse.getTitle());
            tv_item_find_contentview_summary.setText(TextUtils.isEmpty(findResponse.getSummary()) ? "暂无简介" : findResponse.getSummary());

            tv_item_find_titleview_more.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), FindMoreActivity.class);
                intent.putExtra("type", findResponse.getType());
                intent.putExtra("typeId", findResponse.getTypeId());
                intent.putExtra("title", findResponse.getTitle());
                startActivity(intent);
            });

            tv_item_find_apply.setOnClickListener(this);
            rl_item_find_content.setOnClickListener(this);

            ll_item_find_titleview.setOnClickListener(v -> {
                //防止点击title时跳转activity
            });
        }

        @Override
        public void onClick(View view) {
            FindResponse findResponse = dataList.get(getAdapterPosition() - 1);
            Map<String, String> map = new HashMap<>();
            map.put(AiApp.getInstance().getUsername(), AiApp.getInstance().getUserNick());
            CommonUtils.showDialog(getActivity(), R.string.loading);
            htGroup = HTClient.getInstance().groupManager().getGroup(findResponse.getId());
            if (htGroup != null) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userId", findResponse.getId());
                intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
                intent.putExtra("groupName", findResponse.getName());
                CommonUtils.cencelDialog();
                startActivity(intent);
            } else {
                htGroup = new HTGroup();
                HTClient.getInstance().groupManager().addMembers(map, findResponse.getCreator(), findResponse.getId(), new GroupManager.CallBack() {
                    @Override
                    public void onSuccess(String data) {
                        Log.d("1212", "data" + data);
                        CommonUtils.cencelDialog();
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("userId", findResponse.getId());
                        intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
                        intent.putExtra("groupName", findResponse.getName());
                        htGroup.setGroupId(findResponse.getId());
                        htGroup.setGroupName(findResponse.getName());
                        htGroup.setGroupDesc(findResponse.getSummary());
                        htGroup.setOwner(findResponse.getCreator());
                        htGroup.setImgUrl(findResponse.getHeadUrl());
                        HTClient.getInstance().groupManager().saveGroup(htGroup);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getActivity(), R.string.Server_busy);
                    }
                });
            }
        }
    }

    class HeadHolder extends RecyclerView.ViewHolder {
        RecyclerView horizontallistview;

        HeadHolder(View itemView) {
            super(itemView);
            horizontallistview = itemView.findViewById(R.id.horizontallistview);
        }

        private void initData() {
            headAdapter = new HeadAdapter();
            horizontallistview.setAdapter(headAdapter);
            horizontallistview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    class HeadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HeadItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.find_iitem_headitem, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            HeadItemHolder holder1 = (HeadItemHolder) holder;
            Log.d("1212", "size" + position);
            HTGroup htGroup = allGroups.get(position);
            holder1.tvTitle.setText(htGroup.getGroupName());
            BitmapUtil.loadCircleImg(holder1.iv_find_avatar, htGroup.getImgUrl(), R.drawable.user_icon_chat_default);
            holder1.itemView.setOnClickListener(view1 -> {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userId", htGroup.getGroupId());
                intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
                intent.putExtra("groupName", htGroup.getGroupName());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return allGroups.size();
        }
    }

    class HeadItemHolder extends RecyclerView.ViewHolder {
        ImageView iv_find_avatar;
        TextView tvTitle;

        HeadItemHolder(View itemView) {
            super(itemView);
            iv_find_avatar = itemView.findViewById(R.id.iv_find_avatar);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001 && resultCode == 10000) {
            Log.d("1212", "++++++++++++++++++++++");
            HTGroup group = data.getParcelableExtra("Group");
            allGroups.add(0, group);
            headAdapter.notifyItemChanged(0);
        }
    }
}
