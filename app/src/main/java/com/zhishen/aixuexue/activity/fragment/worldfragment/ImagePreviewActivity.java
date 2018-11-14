package com.zhishen.aixuexue.activity.fragment.worldfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.adapter.ImagePreviewAdapter;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.bean.LocalMedia;

import java.util.List;

/**
 * Created by Jerome on 2018/7/19
 */
public class ImagePreviewActivity extends BaseActivity {

    private int itemPosition;
    private List<LocalMedia> imageList;
    private ViewPager viewPager;
    private LinearLayout main_linear;
    private boolean mIsReturning;
    private int mStartPosition;
    private int mCurrentPosition;
    private ImagePreviewAdapter adapter;

    public static final String START_ITEM_POSITION = "start_item_position";//初始的Item位置
    public static final String START_IAMGE_POSITION = "start_item_image_position"; //初始的图片位置
    public static final String CURRENT_ITEM_POSITION = "current_item_position";
    public static final String CURRENT_IAMGE_POSITION = "current_item_image_position";
    public static final String PREVIEW_LIST = "imageList";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        main_linear = findViewById(R.id.main_linear);
        viewPager = findViewById(R.id.mViewPager);

        if (getIntent() != null) {
            mStartPosition = getIntent().getIntExtra(START_IAMGE_POSITION, 0);
            mCurrentPosition = mStartPosition;
            itemPosition = getIntent().getIntExtra(START_ITEM_POSITION, 0);
            imageList = (List<LocalMedia>) getIntent().getSerializableExtra(PREVIEW_LIST);
        }
        if (imageList == null)
            return;
        if (imageList.size() == 1) {
            main_linear.setVisibility(View.GONE);
        } else {
            main_linear.setVisibility(View.VISIBLE);
        }
        View view;
        for (LocalMedia pic : imageList) {

            //创建底部指示器(小圆点)
            view = new View(ImagePreviewActivity.this);
            view.setBackgroundResource(R.drawable.select_guide_indicator);
            view.setEnabled(false);
            //设置宽高
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
            //设置间隔
            if (!pic.equals(imageList.get(0))) {
                layoutParams.leftMargin = 20;
            }
            //添加到LinearLayout
            main_linear.addView(view, layoutParams);
        }
        main_linear.getChildAt(mCurrentPosition).setEnabled(true);

        viewPager.setAdapter(adapter = new ImagePreviewAdapter(this, imageList, itemPosition));
        viewPager.setCurrentItem(mCurrentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hideAllIndicator(position);
                main_linear.getChildAt(position).setEnabled(true);
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setPageTransformer(false, (page, position) -> {
            final float normalizedPosition = Math.abs(Math.abs(position) - 1);
            page.setScaleX(normalizedPosition / 2 + 0.5f);
            page.setScaleY(normalizedPosition / 2 + 0.5f);
        });
    }

    private void hideAllIndicator(int position) {
        for (int i = 0; i < imageList.size(); i++) {
            if (i == position) {
                main_linear.getChildAt(mCurrentPosition).setEnabled(false);
            }
        }
    }

    @Override
    public void finishAfterTransition() {
        mIsReturning = true;
        Intent data = new Intent();
        data.putExtra(START_IAMGE_POSITION, mStartPosition);
        data.putExtra(CURRENT_IAMGE_POSITION, mCurrentPosition);
        data.putExtra(CURRENT_ITEM_POSITION, itemPosition);
        setResult(RESULT_OK, data);
        super.finishAfterTransition();
    }

}
