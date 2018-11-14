package com.zhishen.aixuexue.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Jerome on 2018/7/22
 */
public class MVideoView extends VideoView {

    public MVideoView(Context context) {
        this(context, null);
    }

    public MVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public MVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    //重写测量方法，让视频按照布局显示
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

}
