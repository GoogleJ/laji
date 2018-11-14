package com.zhishen.aixuexue.weight.filterview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.weight.filterview.adapter.FilterLeftAdapter;
import com.zhishen.aixuexue.weight.filterview.adapter.FilterOneAdapter;
import com.zhishen.aixuexue.weight.filterview.adapter.FilterRightAdapter;


/**
 * Created by sunfusheng on 16/4/20.
 */
public class FilterView extends LinearLayout implements View.OnClickListener {

    private View viewMaskBg;
    private TextView tvNear, tvLanguage, tvSort;
    private LinearLayout llNear, llLanguage, llSort;
    private ImageView ivNearArrow, ivLanguageArrow, ivSortArrow;
    private LinearLayout llHeadLayout, llContentListView;
    private ListView lvLeft, lvRight;

    private Context mContext;

    private int filterPosition = -1;
    private int lastFilterPosition = -1;
    public static final int POSITION_NEAR = 0; // 分类的位置
    public static final int POSITION_LANGUAGE = 1;//语言位置
    public static final int POSITION_SORT = 2; // 排序的位置

    private boolean isShowing = false;
    private int panelHeight;
    private FilterData filterData;

    private FilterLeftAdapter leftAdapter;
    private FilterRightAdapter rightAdapter;
    private FilterOneAdapter sortAdapter;

    private FilterTwoEntity leftSelectedNearEntity; //附近
    private FilterEntity rightSelectedNearEntity;
    private FilterTwoEntity leftSelectedLanguageEntity; //语言
    private FilterEntity rightSelectedLanguageEntity;

    private FilterEntity selectedSortEntity; // 排序项

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View v = LayoutInflater.from(context).inflate(R.layout.view_filter_layout, this);

        llNear = v.findViewById(R.id.llNear);
        llLanguage = v.findViewById(R.id.llLanguage);
        llSort = v.findViewById(R.id.llSort);

        tvNear = v.findViewById(R.id.tvNear);
        tvLanguage = v.findViewById(R.id.tvLanguage);
        tvSort = v.findViewById(R.id.tvSort);

        ivNearArrow = v.findViewById(R.id.ivNearArrow);
        ivLanguageArrow = v.findViewById(R.id.ivLanguageArrow);
        ivSortArrow = v.findViewById(R.id.ivSortArrow);

        viewMaskBg = v.findViewById(R.id.view_mask_bg);
        llHeadLayout = v.findViewById(R.id.llContainer);
        llContentListView = v.findViewById(R.id.llContent);
        lvLeft = v.findViewById(R.id.lvLeft);
        lvRight = v.findViewById(R.id.lvRight);

        initView();
        initListener();
    }

    private void initView() {
        viewMaskBg.setVisibility(GONE);
        llContentListView.setVisibility(GONE);
    }

    private void initListener() {
        llNear.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        llSort.setOnClickListener(this);
        viewMaskBg.setOnClickListener(this);
        llContentListView.setOnTouchListener((v, event) -> true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llNear:
                filterPosition = 0;
                break;
            case R.id.llLanguage:
                filterPosition = 1;
                break;
            case R.id.llSort:
                filterPosition = 2;
                break;
            case R.id.view_mask_bg:
                hide();
                break;
        }
        if (onFilterClickListener != null && filterPosition != -1) {
            onFilterClickListener.onFilterClick(filterPosition);
        }
    }

    // 复位筛选的显示状态
    public void resetViewStatus() {
        unSelectTitleStatus(tvNear, ivNearArrow);
        unSelectTitleStatus(tvLanguage, ivLanguageArrow);
        unSelectTitleStatus(tvSort, ivSortArrow);
    }

    private void unSelectTitleStatus(TextView tv, ImageView iv) {
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.font_black_2));
        iv.setImageResource(R.mipmap.home_down_arrow);
    }

    // 复位所有的状态
    public void resetAllStatus() {
        resetViewStatus();
        hide();
    }

    //设置附近
    private void setNearsAdapter() {
        lvLeft.setVisibility(VISIBLE);
        lvRight.setVisibility(VISIBLE);

        // 左边列表视图
        leftAdapter = new FilterLeftAdapter(mContext, filterData.getNears());
        lvLeft.setAdapter(leftAdapter);
        if (leftSelectedNearEntity == null) {
            leftSelectedNearEntity = leftAdapter.getItem(0);
        }
        leftAdapter.setSelectedEntity(leftSelectedNearEntity);

        lvLeft.setOnItemClickListener((parent, view, position, id) -> {
            leftSelectedNearEntity = leftAdapter.getItem(position);
            leftAdapter.setSelectedEntity(leftSelectedNearEntity);

            // 右边列表视图
            rightAdapter = new FilterRightAdapter(mContext, leftSelectedNearEntity.getList());
            lvRight.setAdapter(rightAdapter);
            rightAdapter.setSelectedEntity(rightSelectedNearEntity);
        });

        // 右边列表视图
        rightAdapter = new FilterRightAdapter(mContext, leftSelectedNearEntity.getList());
        lvRight.setAdapter(rightAdapter);
        rightAdapter.setSelectedEntity(rightSelectedNearEntity);
        lvRight.setOnItemClickListener((parent, view, position, id) -> {
            rightSelectedNearEntity = rightAdapter.getItem(position);
            rightAdapter.setSelectedEntity(rightSelectedNearEntity);
            if (onItemNearClickListener != null) {
                tvNear.setText(rightSelectedNearEntity.getKey());
                onItemNearClickListener.onItemNearClick(leftSelectedNearEntity, rightSelectedNearEntity);
            }
            hide();
        });
    }


    // 语言
    private void setLanguageAdapter() {
        lvLeft.setVisibility(VISIBLE);
        lvRight.setVisibility(VISIBLE);

        // 左边列表视图
        leftAdapter = new FilterLeftAdapter(mContext, filterData.getLanguage());
        lvLeft.setAdapter(leftAdapter);
        if (leftSelectedLanguageEntity == null) {
            leftSelectedLanguageEntity = leftAdapter.getItem(0);
        }
        leftAdapter.setSelectedEntity(leftSelectedLanguageEntity);

        lvLeft.setOnItemClickListener((parent, view, position, id) -> {
            leftSelectedLanguageEntity = leftAdapter.getItem(position);
            leftAdapter.setSelectedEntity(leftSelectedLanguageEntity);

            // 右边列表视图
            rightAdapter = new FilterRightAdapter(mContext, leftSelectedLanguageEntity.getList());
            lvRight.setAdapter(rightAdapter);
            rightAdapter.setSelectedEntity(rightSelectedLanguageEntity);
        });

        // 右边列表视图
        rightAdapter = new FilterRightAdapter(mContext, leftSelectedLanguageEntity.getList());
        lvRight.setAdapter(rightAdapter);
        rightAdapter.setSelectedEntity(rightSelectedLanguageEntity);
        lvRight.setOnItemClickListener((parent, view, position, id) -> {
            rightSelectedLanguageEntity = rightAdapter.getItem(position);
            rightAdapter.setSelectedEntity(rightSelectedLanguageEntity);
            if (onItemLanguageClickListener != null) {
                tvLanguage.setText(rightSelectedLanguageEntity.getKey());
                onItemLanguageClickListener.onItemLanguageClick(leftSelectedLanguageEntity, rightSelectedLanguageEntity);
            }
            hide();
        });
    }

    // 排序
    private void setSortAdapter() {
        lvLeft.setVisibility(GONE);
        lvRight.setVisibility(VISIBLE);

        sortAdapter = new FilterOneAdapter(mContext, filterData.getSorts());
        lvRight.setAdapter(sortAdapter);

        lvRight.setOnItemClickListener((parent, view, position, id) -> {
            selectedSortEntity = sortAdapter.getItem(position);
            sortAdapter.setSelectedEntity(selectedSortEntity);
            if (onItemSortClickListener != null) {
                tvSort.setText(selectedSortEntity.getKey());
                onItemSortClickListener.onItemSortClick(selectedSortEntity);
            }
            hide();
        });
    }


    // 动画显示
    public void show(int position) {
        if (isShowing && lastFilterPosition == position) {
            hide();
            return;
        } else if (!isShowing) {
            viewMaskBg.setVisibility(VISIBLE);
            llContentListView.setVisibility(VISIBLE);
            llContentListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    llContentListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    panelHeight = llContentListView.getHeight();
                    ObjectAnimator.ofFloat(llContentListView, "translationY", -panelHeight, 0).setDuration(200).start();
                }
            });
        }
        isShowing = true;
        resetViewStatus();
        rotateArrowUp(position);
        rotateArrowDown(lastFilterPosition);
        lastFilterPosition = position;

        switch (position) {
            case POSITION_NEAR:
                selectedTitleStatus(tvNear, ivNearArrow);
                setNearsAdapter();
                break;
            case POSITION_LANGUAGE:
                selectedTitleStatus(tvLanguage, ivLanguageArrow);
                setLanguageAdapter();
                break;
            case POSITION_SORT:
                selectedTitleStatus(tvSort, ivSortArrow);
                setSortAdapter();
                break;
        }
    }

    private void selectedTitleStatus(TextView tv, ImageView iv) {
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        iv.setImageResource(R.mipmap.home_down_arrow);
    }

    // 隐藏动画
    public void hide() {
        isShowing = false;
        resetViewStatus();
        rotateArrowDown(filterPosition);
        rotateArrowDown(lastFilterPosition);
        filterPosition = -1;
        lastFilterPosition = -1;
        llContentListView.setVisibility(View.GONE);
        viewMaskBg.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(llContentListView, "translationY", 0, -panelHeight).setDuration(200).start();
    }

    // 设置筛选数据
    public void setFilterData(Context ctx, FilterData filterData) {
        this.mContext = ctx;
        this.filterData = filterData;
    }

    // 是否显示
    public boolean isShowing() {
        return isShowing;
    }

    public int getFilterPosition() {
        return filterPosition;
    }

    // 旋转箭头向上
    private void rotateArrowUp(int position) {
        switch (position) {
            case POSITION_NEAR:
                rotateArrowUpAnimation(ivNearArrow);
                break;
            case POSITION_LANGUAGE:
                rotateArrowUpAnimation(ivLanguageArrow);
                break;
            case POSITION_SORT:
                rotateArrowUpAnimation(ivSortArrow);
                break;
        }
    }

    // 旋转箭头向下
    private void rotateArrowDown(int position) {
        switch (position) {
            case POSITION_NEAR:
                rotateArrowDownAnimation(ivNearArrow);
                break;
            case POSITION_LANGUAGE:
                rotateArrowDownAnimation(ivLanguageArrow);
                break;
            case POSITION_SORT:
                rotateArrowDownAnimation(ivSortArrow);
                break;
        }
    }

    // 旋转箭头向上
    public static void rotateArrowUpAnimation(final ImageView iv) {
        if (iv == null)
            return;
        RotateAnimation animation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        iv.startAnimation(animation);
    }

    // 旋转箭头向下
    public static void rotateArrowDownAnimation(final ImageView iv) {
        if (iv == null)
            return;
        RotateAnimation animation = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        iv.startAnimation(animation);
    }

    // 筛选视图点击
    private OnFilterClickListener onFilterClickListener;

    public void setOnFilterClickListener(OnFilterClickListener onFilterClickListener) {
        this.onFilterClickListener = onFilterClickListener;
    }

    /**
     * 业务区
     */
    public interface OnFilterClickListener {
        void onFilterClick(int position);
    }

    //附近
    private OnItemNearClickListener onItemNearClickListener;

    public void setOnItemNearClickListener(OnItemNearClickListener onItemNearClickListener) {
        this.onItemNearClickListener = onItemNearClickListener;
    }

    public interface OnItemNearClickListener {
        void onItemNearClick(FilterTwoEntity leftEntity, FilterEntity rightEntity);
    }

    //语言
    private OnItemLanguageClickListener onItemLanguageClickListener;

    public void setOnItemLanguageClickListener(OnItemLanguageClickListener onItemLanguageClickListener) {
        this.onItemLanguageClickListener = onItemLanguageClickListener;
    }

    public interface OnItemLanguageClickListener {

        void onItemLanguageClick(FilterTwoEntity leftEntity, FilterEntity rightEntity);
    }

    // 排序
    private OnItemSortClickListener onItemSortClickListener;

    public void setOnItemSortClickListener(OnItemSortClickListener onItemSortClickListener) {
        this.onItemSortClickListener = onItemSortClickListener;
    }

    public interface OnItemSortClickListener {
        void onItemSortClick(FilterEntity entity);
    }


}
