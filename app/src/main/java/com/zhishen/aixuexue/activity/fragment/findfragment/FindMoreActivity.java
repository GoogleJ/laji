package com.zhishen.aixuexue.activity.fragment.findfragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.utils.MessageUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.ChatActivity;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.response.GroupListRespone;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FindMoreActivity extends BaseActivity {

    private ListView listview_findmore;
    private List<FindResponse> dataList = new ArrayList<>();
    private Adapter adapter;
    private SmartRefreshLayout refreshLayout;
    private int currentIndex;
    private String type;
    private String title;
    private String typeId;

    AppApi retrofitService;
    GetGroupRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_more);

        type = getIntent().getStringExtra("type");
        title = getIntent().getStringExtra("title");
        typeId = getIntent().getStringExtra("typeId");
        setTitle(type);
        retrofitService = ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, FindMoreActivity.this);
        request = new GetGroupRequest(typeId);

        refreshLayout = findViewById(R.id.SmartRefreshLayout);
        listview_findmore = findViewById(R.id.listview_findmore);
        adapter = new Adapter();
        listview_findmore.setAdapter(adapter);
        listview_findmore.setEmptyView(findViewById(R.id.ll_find_emptyview));
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(refreshLayout1 -> {
            currentIndex = 0;
            getData(true);
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout1 -> {
            getData(false);
        });

    }

    private Disposable disposable;

    private void getData(boolean isRefresh) {

        dispose();

        request.setPage(currentIndex + 1);


        disposable = retrofitService.getGroup(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(response -> {
                    if (isRefresh) {

                        refreshLayout.finishRefresh();
                    } else {
                        refreshLayout.finishLoadMore();
                    }

                    if (response.code != 0) {
                        CommonUtils.showToastShort(FindMoreActivity.this, response.msg);
                        return;
                    }

                    GroupListRespone data = response.data;
                    if (isRefresh) {
                        dataList.clear();
                    }
                    for (GroupListRespone.ListBean bean : data.getList()) {
                        FindResponse findResponse = new FindResponse();
                        findResponse.setTitle(bean.getName());
                        findResponse.setSummary(bean.getDescri());
                        findResponse.setType(title);
                        findResponse.setId(bean.getGid());
                        findResponse.setHeadUrl(bean.getImgurl());
                        findResponse.setGroupCount(bean.getUserCount());
                        findResponse.setGroupType(bean.getType());
                        findResponse.setCreator(bean.getCreator());
                        findResponse.setName(bean.getName());
                        dataList.add(findResponse);
                    }
                    if (data.getList().size() <= 0) {
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                    adapter.notifyDataSetChanged();
                    currentIndex += 1;
                }, throwable -> {
                    if (isRefresh) {
                        refreshLayout.finishRefresh();
                    } else {
                        refreshLayout.finishLoadMore();
                    }
                });
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(FindMoreActivity.this, R.layout.item_find, null);
                holder.divider_item_find_hottop = convertView.findViewById(R.id.divider_item_find_hottop);
                holder.ll_item_find_titleview = convertView.findViewById(R.id.ll_item_find_titleview);
                holder.divider_item_find_titleview = convertView.findViewById(R.id.divider_item_find_titleview);
                holder.tv_item_find_titleview_title = convertView.findViewById(R.id.tv_item_find_titleview_title);
                holder.tv_item_find_titleview_more = convertView.findViewById(R.id.tv_item_find_titleview_more);
                holder.cir_item_find_head = convertView.findViewById(R.id.cir_item_find_head);
                holder.tv_item_find_apply = convertView.findViewById(R.id.tv_item_find_apply);
                holder.tv_item_find_contentview_title = convertView.findViewById(R.id.tv_item_find_contentview_title);
                holder.tv_item_find_contentview_groupcount = convertView.findViewById(R.id.tv_item_find_contentview_groupcount);
                holder.tv_item_find_type_pay = convertView.findViewById(R.id.tv_item_find_type_pay);
                holder.tv_item_find_type_private = convertView.findViewById(R.id.tv_item_find_type_private);
                holder.tv_item_find_contentview_summary = convertView.findViewById(R.id.tv_item_find_contentview_summary);
                holder.divider_bottom = convertView.findViewById(R.id.divider_bottom);

                holder.tv_item_find_titleview_more.setVisibility(View.INVISIBLE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            FindResponse findResponse = dataList.get(position);


            if (position == 0) {
                holder.ll_item_find_titleview.setVisibility(View.VISIBLE);
                holder.divider_item_find_titleview.setVisibility(View.VISIBLE);
                holder.divider_item_find_hottop.setVisibility(View.GONE);
            } else {
                if (findResponse.getType().equals(dataList.get(position - 1).getType())) {
                    holder.ll_item_find_titleview.setVisibility(View.GONE);
                    holder.divider_item_find_titleview.setVisibility(View.GONE);
                    holder.divider_item_find_hottop.setVisibility(View.GONE);
                } else {
                    holder.ll_item_find_titleview.setVisibility(View.VISIBLE);
                    holder.divider_item_find_titleview.setVisibility(View.VISIBLE);
                    holder.divider_item_find_hottop.setVisibility(View.VISIBLE);
                }
            }

            if (position == getCount() - 1) {
                holder.divider_bottom.setVisibility(View.INVISIBLE);
            } else {
                if (findResponse.getType().equals(dataList.get(position + 1).getType())) {
                    holder.divider_bottom.setVisibility(View.VISIBLE);
                } else {
                    holder.divider_bottom.setVisibility(View.INVISIBLE);
                }
            }

            holder.tv_item_find_contentview_groupcount.setText(findResponse.getGroupCount() + "");

            if (findResponse.getGroupType().equals("公开群")) {
                holder.tv_item_find_type_pay.setVisibility(View.VISIBLE);
                holder.tv_item_find_type_private.setVisibility(View.GONE);
            } else {
                holder.tv_item_find_type_pay.setVisibility(View.GONE);
                holder.tv_item_find_type_private.setVisibility(View.VISIBLE);
            }

            BitmapUtil.loadCircleImg(holder.cir_item_find_head, findResponse.getHeadUrl(), R.drawable.default_avatar);
            holder.tv_item_find_titleview_title.setText(findResponse.getType());
            holder.tv_item_find_contentview_title.setText(findResponse.getTitle());
            holder.tv_item_find_contentview_summary.setText(TextUtils.isEmpty(findResponse.getSummary()) ? "暂无简介" : findResponse.getSummary());


            holder.tv_item_find_apply.setOnClickListener(v -> {
                //进入群聊
                Map<String, String> map = new HashMap<>();
                map.put(AiApp.getInstance().getUsername(), AiApp.getInstance().getUserNick());
                HTClient.getInstance().groupManager().addMembers(map, findResponse.getCreator(), findResponse.getId(), new GroupManager.CallBack() {
                    @Override
                    public void onSuccess(String data) {
                        Log.d("1212", "data" + data);
                        Intent intent = new Intent(mActivity, ChatActivity.class);
                        intent.putExtra("userId", findResponse.getId());
                        intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
                        intent.putExtra("groupName", findResponse.getName());
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure() {
                    }
                });
            });

            holder.ll_item_find_titleview.setOnClickListener(v -> {
                //防止点击title时跳转activity
            });

            return convertView;
        }
    }

    static class ViewHolder {
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
    }

    @Override
    protected void onDestroy() {
        dispose();
        super.onDestroy();
    }
}
