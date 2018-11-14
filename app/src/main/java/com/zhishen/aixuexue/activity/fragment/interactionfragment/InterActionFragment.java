package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.MechanismActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserDetailNewActivity;
import com.zhishen.aixuexue.activity.newfriend.AddFriendsFinalActivity;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.LocationUtil;
import com.zhishen.aixuexue.util.ScreenUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 世界界面
 * Created by yangfaming on 2018/6/15.
 */

public class InterActionFragment extends RxFragment implements AMap.OnMyLocationChangeListener, AMap.OnMarkerClickListener {

    View view_notice_empty;

    LinearLayout ll_bottom1;
    LinearLayout ll_bottom2;

    List<HomeBean.MapPageConfigBean.TopCatagoryBean> topList;

    View view;

    private int currentMode = 1; //1:map  2:list
    private int currentIndex;

    ImageView iv_interaction_locate;
    ImageView iv_interaction_group;
    LinearLayout ll_interaction_bottom;
    ListView listview_interaction;
    RecyclerView listview_interaction_toptab;

    MapView map;
    AMap aMap;
    private List<Marker> marks = new ArrayList<>();

    private List<GetNearByResponse> dateBeans = new ArrayList<>();

    private Adapter adapter;
    private RecyclerView.Adapter adapter1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        topList = LocalUserManager.getInstance().getAppconfig().getMap_pageConfig().getTopCatagory();
        view = inflater.inflate(R.layout.fragment_interaction, container, false);
        initView(savedInstanceState);
        return view;
    }


    private void initView(@Nullable Bundle savedInstanceState) {
        view_notice_empty = view.findViewById(R.id.view_notice_empty);
        TextView textView = view_notice_empty.findViewById(R.id.tv_empty_tips);
        textView.setText("暂无数据");
        ll_bottom1 = view.findViewById(R.id.ll_bottom1);
        ll_bottom2 = view.findViewById(R.id.ll_bottom2);

        TextView textView1 = ll_bottom1.findViewById(R.id.tv_interaction_bottom1);
        TextView textView2 = ll_bottom2.findViewById(R.id.tv_interaction_bottom2);
        textView1.setText(LocalUserManager.getInstance().getAppconfig().getMap_pageConfig().getBottomCatagory().get(0).getTitle());
        textView2.setText(LocalUserManager.getInstance().getAppconfig().getMap_pageConfig().getBottomCatagory().get(1).getTitle());

        ll_bottom2.setOnClickListener(view -> {
            if (onClickBottomListener != null) onClickBottomListener.onClick();
        });
        ll_bottom1.setOnClickListener(view -> {
            if (onClickBottomListener != null) onClickBottomListener.onClick();
        });
        ll_interaction_bottom = view.findViewById(R.id.ll_interaction_bottom);
        listview_interaction = view.findViewById(R.id.listview_interaction);
        listview_interaction_toptab = view.findViewById(R.id.listview_interaction_toptab);

        listview_interaction.setOnItemClickListener((adapterView, view, i, l) -> {
            if (dateBeans.size() != 0) {
                showFunction(dateBeans.get(i));
            }
        });

        adapter1 = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_interaction_toptab, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.width = ScreenUtil.getWindowWidth(getActivity()) / getItemCount();

                HomeBean.MapPageConfigBean.TopCatagoryBean topCatagoryBean = topList.get(position);

                ViewHolder holder1 = (ViewHolder) holder;

                holder1.tv_item_interaction_toptab.setText(topCatagoryBean.getTitle());

                if (position == currentIndex) {
                    holder1.divider_item_interaction_toptab.setVisibility(View.VISIBLE);
                    holder1.tv_item_interaction_toptab.setTextColor(ContextCompat.getColor(getContext(), R.color.themecolor));
                } else {
                    holder1.divider_item_interaction_toptab.setVisibility(View.GONE);
                    holder1.tv_item_interaction_toptab.setTextColor(ContextCompat.getColor(getContext(), R.color.textcolor1));
                }

                holder1.itemView.setOnClickListener(v -> {
                    if (isClear) {
                        return;
                    }
                    currentIndex = holder1.getAdapterPosition();
                    notifyDataSetChanged();
                    getNearBy();
                });
            }

            @Override
            public int getItemCount() {
                return topList.size();
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                TextView tv_item_interaction_toptab;
                View divider_item_interaction_toptab;

                ViewHolder(View itemView) {
                    super(itemView);
                    tv_item_interaction_toptab = itemView.findViewById(R.id.tv_item_interaction_toptab);
                    divider_item_interaction_toptab = itemView.findViewById(R.id.divider_item_interaction_toptab);
                }
            }
        };

        listview_interaction_toptab.setAdapter(adapter1);

        listview_interaction_toptab.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter = new Adapter();
        listview_interaction.setAdapter(adapter);

        iv_interaction_locate = view.findViewById(R.id.iv_interaction_locate);
        iv_interaction_group = view.findViewById(R.id.iv_interaction_group);

        iv_interaction_locate.setOnClickListener(v -> {
            if (null != currentLocation) {
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 14.5f, 0, 0)));
            }
        });

        iv_interaction_group.setOnClickListener(view -> changeMode());

        initMapView(savedInstanceState);
    }

    AnimatorSet mapMode;
    AnimatorSet listMode;
    boolean isAniming;

    private void changeMode() {
        if (isAniming) {
            return;
        }
        mapMode = new AnimatorSet();
        listMode = new AnimatorSet();
        mapMode.setDuration(350);
        listMode.setDuration(350);
        mapMode.setInterpolator(new AccelerateInterpolator());
        listMode.setInterpolator(new AccelerateInterpolator());

        listMode.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAniming = false;
                map.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isAniming = true;
                iv_interaction_group.setImageResource(R.drawable.ic_changemode_map);
                listview_interaction.setScaleX(0);
                listview_interaction.setScaleY(0);
                iv_interaction_locate.setVisibility(View.GONE);
                ll_interaction_bottom.setVisibility(View.GONE);
                listview_interaction.setVisibility(View.VISIBLE);
//                if (valueAnimator != null) {
//                    valueAnimator.cancel();
//                }
            }
        });

        mapMode.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAniming = false;
                listview_interaction.setVisibility(View.INVISIBLE);
                iv_interaction_locate.setVisibility(View.VISIBLE);
                ll_interaction_bottom.setVisibility(View.VISIBLE);
//                if (valueAnimator != null) {
//                    valueAnimator.start();
//                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isAniming = true;
                iv_interaction_group.setImageResource(R.drawable.ic_interaction_group);
                map.setScaleX(0);
                map.setScaleY(0);
                map.setVisibility(View.VISIBLE);
            }
        });

        if (currentMode == 2) {
            currentMode = 1;
            mapMode.play(ObjectAnimator.ofFloat(map, "scaleX", 0, 1))
                    .with(ObjectAnimator.ofFloat(map, "scaleY", 0, 1))
                    .after(ObjectAnimator.ofFloat(listview_interaction, "scaleX", 1, 0))
                    .after(ObjectAnimator.ofFloat(listview_interaction, "scaleY", 1, 0));
            mapMode.start();
        } else {
            currentMode = 2;
            listMode.play(ObjectAnimator.ofFloat(map, "scaleX", 1, 0))
                    .with(ObjectAnimator.ofFloat(map, "scaleY", 1, 0))
                    .before(ObjectAnimator.ofFloat(listview_interaction, "scaleX", 0, 1))
                    .before(ObjectAnimator.ofFloat(listview_interaction, "scaleY", 0, 1));
            listMode.start();
        }
    }


    private void initMapView(@Nullable Bundle savedInstanceState) {
        map = view.findViewById(R.id.map);
        map.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = map.getMap();
            aMap.setOnMyLocationChangeListener(this);
            MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
            myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            myLocationStyle.radiusFillColor(Color.parseColor("#304fc6e0"));
            myLocationStyle.strokeWidth(0);
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_dot));
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.setOnMarkerClickListener(this);

            aMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo
        }
    }


    boolean firstInit = true;
    Location currentLocation;
//    private Circle c;
//    ValueAnimator valueAnimator;

    LatLng firstLatLng; //50米刷新周围数据一次的初始点 位置变化50米后重新赋值一次

    @Override
    public void onMyLocationChange(Location aMapLocation) {

        currentLocation = aMapLocation;

        LatLng mylocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

        if (firstInit) {
            firstInit = false;
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 14.5f));
//            c = aMap.addCircle(new CircleOptions().center(mylocation)
//                    .fillColor(Color.parseColor("#184fc6e0"))
//                    .radius(3000)
//                    .strokeWidth(0));
//
//            valueAnimator = ValueAnimator.ofInt(0, 3000);
//            valueAnimator.setRepeatCount(-1);
//            valueAnimator.setRepeatMode(ValueAnimator.RESTART);
//            valueAnimator.setDuration(1500);
//            valueAnimator.addUpdateListener(animation -> c.setRadius((int) animation.getAnimatedValue()));
//            valueAnimator.start();

            firstLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            Location location = new Location("");
            location.setLatitude(aMapLocation.getLatitude());
            location.setLongitude(aMapLocation.getLongitude());
            LocalUserManager.getInstance().setLatlng(new Location(location));
            getNearBy();

            CommonUtils.cencelDialog();
        } else {
//            c.setCenter(mylocation);

            float distance = AMapUtils.calculateLineDistance(firstLatLng, new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
            if (distance > 50) {
                firstLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                getNearBy();
            }
        }

    }

    Disposable disposable;

    private boolean isClear;

    @SuppressLint("CheckResult")
    private void getNearBy() {

        GetNearByRequest request = new GetNearByRequest();
        if (firstLatLng != null) {
            GetNearByRequest.UserVoBean userVoBean = new GetNearByRequest.UserVoBean();
            userVoBean.setId(AiApp.getInstance().getUsername());
            userVoBean.setLat(firstLatLng.latitude);
            userVoBean.setLon(firstLatLng.longitude);
            userVoBean.setRaidus(3000);
            request.setListStatus(topList.get(currentIndex).getId());
            request.setUserVo(userVoBean);

            if (null != disposable && !disposable.isDisposed()) {
                disposable.dispose();
            }
            if (!firstInit) {
                CommonUtils.showDialog(getActivity(), R.string.loading);
            }
            dateBeans.clear();
            adapter.notifyDataSetChanged();
            view_notice_empty.setVisibility(View.VISIBLE);
            disposable = ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, getContext())
                    .getNearBy(request.decode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle())
                    .subscribe(getNearByResponseBaseResponseDataT -> {
                        CommonUtils.cencelDialog();
                        if (!firstInit) {
                            isClear = true;
                            for (int j = 0; j < marks.size(); j++) {
                                marks.get(j).remove();
                            }
                            marks.clear();

                            isClear = false;
                        }

                        List<GetNearByResponse> data = getNearByResponseBaseResponseDataT.data;

                        if (data.size() == 0) {
                            view_notice_empty.setVisibility(View.VISIBLE);
                        } else {
                            view_notice_empty.setVisibility(View.INVISIBLE);
                        }

                        dateBeans.addAll(getNearByResponseBaseResponseDataT.data);
                        adapter.notifyDataSetChanged();

                        for (int i = 0; i < data.size(); i++) {
                            GetNearByResponse listBean = data.get(i);
                            int finalI = i;
                            String userAvatar = listBean.getHeadUrl();
                            CommonUtils.customizeMarkerIcon(getActivity(), TextUtils.isEmpty(userAvatar) || "false".equals(userAvatar) ? R.drawable.default_avatar : userAvatar, (view, descriptor) -> {
                                MarkerOptions markerOption = new MarkerOptions();
                                markerOption.position(new LatLng(listBean.getLat(), listBean.getLon()));
                                markerOption.draggable(false);
                                markerOption.setFlat(true);
                                markerOption.icon(descriptor);
                                Marker marker = aMap.addMarker(markerOption);
                                marker.setObject(finalI);
                                marks.add(marker);
                            });
                        }
                    }, throwable -> {
                        CommonUtils.cencelDialog();
                    });
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onDestroyView() {
        map.onDestroy();
//        valueAnimator.cancel();
        super.onDestroyView();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int index = (int) marker.getObject();
        GetNearByResponse listBean = dateBeans.get(index);
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(listBean.getLat(), listBean.getLon()), aMap.getCameraPosition().zoom, 0, 0)));
        showFunction(listBean);
        return true;
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dateBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return dateBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                holder = new ViewHolder();

                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interaction, parent, false);

                holder.tv_item_interaction_distance = convertView.findViewById(R.id.tv_item_interaction_distance);
                holder.iv_item_interaction_head = convertView.findViewById(R.id.iv_item_interaction_head);
                holder.tv_item_interaction_name = convertView.findViewById(R.id.tv_item_interaction_name);
                holder.tv_item_interaction_tag1 = convertView.findViewById(R.id.tv_item_interaction_tag1);
                holder.tv_item_interaction_tag2 = convertView.findViewById(R.id.tv_item_interaction_tag2);
                holder.tv_item_interaction_tag3 = convertView.findViewById(R.id.tv_item_interaction_tag3);
                holder.tv_item_interaction_tag4 = convertView.findViewById(R.id.tv_item_interaction_tag4);
                holder.tv_item_interaction_tag5 = convertView.findViewById(R.id.tv_item_interaction_tag5);
                holder.tv_item_interaction_summary = convertView.findViewById(R.id.tv_item_interaction_summary);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GetNearByResponse listBean = dateBeans.get(position);

            String d;
            if (listBean.getDistance() < 1000) {
                d = (int) listBean.getDistance() + "m";
            } else {
                d = new DecimalFormat("0.0").format(listBean.getDistance()/1000) + "km";
            }
            holder.tv_item_interaction_distance.setText(d);

            holder.tv_item_interaction_name.setText(listBean.getTitle());

            holder.tv_item_interaction_summary.setText(listBean.getDesc());

            String sex = listBean.getSex();

            Drawable drawable = null;
            if (sex != null && sex.equals("男")) {
                drawable = getResources().getDrawable(R.drawable.ic_interaction_male);
            } else if (sex != null && sex.equals("女")) {
                drawable = getResources().getDrawable(R.drawable.ic_interaction_female);
            }

            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.tv_item_interaction_name.setCompoundDrawables(null, null, drawable, null);
            } else {
                holder.tv_item_interaction_name.setCompoundDrawables(null, null, null, null);
            }

            BitmapUtil.loadCircleImg(holder.iv_item_interaction_head,
                    listBean.getHeadUrl(), R.drawable.default_avatar);

            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_item_interaction_distance;
        ImageView iv_item_interaction_head;
        TextView tv_item_interaction_name;

        TextView tv_item_interaction_tag1;
        TextView tv_item_interaction_tag2;
        TextView tv_item_interaction_tag3;
        TextView tv_item_interaction_tag4;
        TextView tv_item_interaction_tag5;

        TextView tv_item_interaction_summary;
    }

    PopupWindow mPopWindow;
    View popup;
    TextView tv_popup_interaction_distance;
    TextView tv_popup_interaction_name;
    TextView tv_popup_interaction_id;
    TextView tv_popup_interaction_summary;
    TextView tv_popup_interaction_detail;
    TextView tv_popup_interaction_chat;
    TextView tv_popup_interaction_add;
    TextView tv_popup_interaction_add2;
    ImageView iv_popup_interaction_head;

    private void showFunction(GetNearByResponse listBean) {
        if (mPopWindow == null) {
            popup = View.inflate(getContext(), R.layout.popup_interaction, null);
            tv_popup_interaction_distance = popup.findViewById(R.id.tv_popup_interaction_distance);
            tv_popup_interaction_name = popup.findViewById(R.id.tv_popup_interaction_name);
            tv_popup_interaction_id = popup.findViewById(R.id.tv_popup_interaction_id);
            tv_popup_interaction_summary = popup.findViewById(R.id.tv_popup_interaction_summary);
            tv_popup_interaction_detail = popup.findViewById(R.id.tv_popup_interaction_detail);
            tv_popup_interaction_chat = popup.findViewById(R.id.tv_popup_interaction_chat);
            tv_popup_interaction_add = popup.findViewById(R.id.tv_popup_interaction_add);
            tv_popup_interaction_add2 = popup.findViewById(R.id.tv_popup_interaction_add2);
            iv_popup_interaction_head = popup.findViewById(R.id.iv_popup_interaction_head);
            mPopWindow = new PopupWindow(popup, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        mPopWindow.setBackgroundDrawable(new ColorDrawable());
        mPopWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        handlePopup(popup, mPopWindow, listBean);
        showPopup(popup);
    }

    //给popup设置监听
    @SuppressLint("CheckResult")
    private void handlePopup(View popup, PopupWindow mPopWindow, GetNearByResponse listBean) {

        tv_popup_interaction_name.setText(listBean.getTitle());

        String d;
        if (listBean.getDistance() < 1000) {
            d = (int) listBean.getDistance() + "m";
        } else {
            d = new DecimalFormat("0.0").format(listBean.getDistance()/1000) + "km";
        }
        tv_popup_interaction_distance.setText(d);

        tv_popup_interaction_summary.setText(listBean.getDesc());
        tv_popup_interaction_id.setText(listBean.getId());

        BitmapUtil.loadCircleImg(iv_popup_interaction_head, listBean.getHeadUrl(),
                R.drawable.default_avatar);

        View ll_popup_window = popup.findViewById(R.id.ll_popup_window);

        HomeBean.MapPageConfigBean.TopCatagoryBean topCatagoryBean = topList.get(currentIndex);

        String id = topCatagoryBean.getId();

        if (id.equals("nearPerson")) {
            //附近的人、附近的老师
            boolean isFriend = isFriend(listBean.getId());

            tv_popup_interaction_summary.setLines(1);
            tv_popup_interaction_id.setVisibility(View.VISIBLE);

            if (isFriend) {
                tv_popup_interaction_add2.setVisibility(View.INVISIBLE);
                tv_popup_interaction_add.setText("关注此人");
                Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_popup_interaction_4);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_popup_interaction_add.setCompoundDrawables(drawable, null, null, null);
            } else {
                tv_popup_interaction_add2.setVisibility(View.VISIBLE);
                tv_popup_interaction_add.setText("添加好友");
                Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_popup_interaction_3);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_popup_interaction_add.setCompoundDrawables(drawable, null, null, null);
            }

        } else {
            tv_popup_interaction_summary.setLines(2);
            tv_popup_interaction_id.setVisibility(View.GONE);
            tv_popup_interaction_add.setText("关注机构");
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_popup_interaction_4);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_popup_interaction_add.setCompoundDrawables(drawable, null, null, null);
            tv_popup_interaction_add2.setVisibility(View.INVISIBLE);
        }

        tv_popup_interaction_detail.setOnClickListener(view -> {
            Intent intent;
            if (topList.get(currentIndex).getId().equals("nearPerson")) {
                intent = new Intent(getContext(), UserDetailNewActivity.class);
                intent.putExtra(Constant.JSON_KEY_HXID, listBean.getId());
            } else {
                intent = new Intent(getContext(), MechanismActivity.class);
                intent.putExtra(MechanismActivity.MECHAIN_ID, listBean.getId());
            }
            startActivity(intent);
            mPopWindow.dismiss();
        });

        tv_popup_interaction_add.setOnClickListener(view -> {
            if (id.equals("nearPerson")) {
                if (isFriend(listBean.getId())) {
                    addNoticeOnPerson(listBean);
                } else {
                    startActivity(new Intent(getActivity(), AddFriendsFinalActivity.class).putExtra("userId", listBean.getId()));
                }
            } else {
                //关注机构
                BlackAndWhiteRequest blackAndWhiteRequest = new BlackAndWhiteRequest();
                blackAndWhiteRequest.setFromUserid(AiApp.getInstance().getUsername());
                blackAndWhiteRequest.setToContentid(listBean.getId());
                blackAndWhiteRequest.setOperateType("2");
                blackAndWhiteRequest.setContentType("3");
                String decode = blackAndWhiteRequest.decode();
                CommonUtils.showDialog(getActivity(), R.string.loading);
                ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, getContext())
                        .addBlackAndWhite(decode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindToLifecycle())
                        .subscribe(response -> {
                            CommonUtils.cencelDialog();
                            if (response.code != 0) {
                                CommonUtils.showToastShort(getContext(), response.msg);
                                return;
                            }

                            CommonUtils.showToastShort(getContext(), "已关注");

                        }, throwable -> {
                            CommonUtils.cencelDialog();
                        });
            }
            mPopWindow.dismiss();
        });

        tv_popup_interaction_add2.setOnClickListener(view -> {
            //关注用户
            addNoticeOnPerson(listBean);
            mPopWindow.dismiss();
        });

        tv_popup_interaction_chat.setOnClickListener(view -> {
            if (id.equals("nearPerson")) {
                //和人匿名聊天
                startActivity(new Intent(getContext(), ChatActivity.class).putExtra("userId", listBean.getId()).putExtra("userNick", listBean.getTitle()));
            } else {
                //和机构客服？聊天
                if (TextUtils.isEmpty(listBean.getServiceMobile())) {
                    CommonUtils.showToastShort(getContext(), "暂时无法聊天");
                    return;
                }
                startActivity(new Intent(getContext(), ChatActivity.class).putExtra("userId", listBean.getServiceMobile()).putExtra("userNick", listBean.getTitle()));
            }
            mPopWindow.dismiss();
        });

        ll_popup_window.setOnClickListener(view -> mPopWindow.dismiss());

        mPopWindow.setOnDismissListener(() -> backgroundAlpha(1f));
    }

    @SuppressLint("CheckResult")
    private void addNoticeOnPerson(GetNearByResponse listBean) {
        BlackAndWhiteRequest blackAndWhiteRequest = new BlackAndWhiteRequest();
        blackAndWhiteRequest.setFromUserid(AiApp.getInstance().getUsername());
        blackAndWhiteRequest.setToContentid(listBean.getId());
        blackAndWhiteRequest.setOperateType("2");
        blackAndWhiteRequest.setContentType("5");
        String decode = blackAndWhiteRequest.decode();

        ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, getContext())
                .addBlackAndWhite(decode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(response -> {
                    if (response.code != 0) {
                        CommonUtils.showToastShort(getContext(), response.msg);
                        return;
                    }

                    CommonUtils.showToastShort(getContext(), "已关注");

                }, throwable -> {

                });
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    private void showPopup(View popup) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnimation.setDuration(300);
        popup.startAnimation(translateAnimation);
        backgroundAlpha(0.6f);
    }

    public interface OnClickBottomListener {
        void onClick();
    }

    private OnClickBottomListener onClickBottomListener;

    public void setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
    }


    public boolean isFriend(String userId) {
        return ContactsManager.getInstance().getContactList().containsKey(userId);
    }
}
