package com.zhishen.aixuexue.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhishen.aixuexue.R;

import java.util.ArrayList;

/**
 * Created by yangfaming on 2018/6/15.
 */

public class NavagationView extends LinearLayout {
    private int image_height;
    private int image_weigth;
    private float font_size;
    private int[] selectedImage;
    private int[] unselectedImage;
    private Context mContext;
    private ArrayList<TextView> textViews = new ArrayList<>();
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public NavagationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavagationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NavagationView);
        image_weigth = array.getInteger(R.styleable.NavagationView_image_weigth, 20);
        image_height = array.getInteger(R.styleable.NavagationView_image_height, 20);
        font_size = array.getDimension(R.styleable.NavagationView_font_size, 12);
        array.recycle();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setLayout(String[] titles, int[] selectedImage, int[] unselectedImage, int screenWidth, int mHeight, Context context) {
        this.mContext = context;
        this.selectedImage = selectedImage;
        this.unselectedImage = unselectedImage;
        setOrientation(LinearLayout.HORIZONTAL);
        setClipChildren(false);
        if (titles != null && titles.length != 0) {
            int scalWeigth = screenWidth / titles.length;
            for (int i = 0; i < titles.length; i++) {
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER);
                LayoutParams params = new LayoutParams(scalWeigth, LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                if (i == 1) {
//                    layout.setGravity(Gravity.BOTTOM);
                    params.height = 360;
                }
                layout.setLayoutParams(params);


                ImageView imageView = new ImageView(context);
                LayoutParams layoutParams = new LayoutParams(image_weigth, image_height);
                Log.d("1212","  "+image_weigth+"   "+image_height);
                layoutParams.topMargin = 5;
                layoutParams.bottomMargin = 2;
                if (i == 1) {
                    layoutParams.width = 120;
                    layoutParams.height = 120;
                }

                imageView.setImageDrawable(context.getResources().getDrawable(unselectedImage[i]));
                imageView.setLayoutParams(layoutParams);


                TextView textView = new TextView(context);
                LayoutParams textlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                if (i==1){
                    textlp.gravity=Gravity.BOTTOM;
                }
                textView.setText(titles[i]);
                textView.setTextSize(font_size);
                textView.setLayoutParams(textlp);

                layout.addView(imageView);
                layout.addView(textView);
                layout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = (Integer) view.getTag();
                        setColorLing(position);
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                });
                layout.setTag(i);
                addView(layout, scalWeigth, mHeight);
                imageViews.add(imageView);
                textViews.add(textView);

            }
        }
    }

    /**
     * 设置选中图标及颜色
     *
     * @param position
     */
    public void setColorLing(int position) {
        setColorDrak();
        for (int i = 0; i < textViews.size(); i++) {
            if (position == i) {
                textViews.get(i).setTextColor(getResources().getColor(R.color.gloable));
                imageViews.get(i).setImageDrawable(mContext.getResources().getDrawable(selectedImage[i]));
            }
        }
    }

    /**
     * 设置未选中图标及颜色
     */
    public void setColorDrak() {
        for (int i = 0; i < textViews.size(); i++) {
            textViews.get(i).setTextColor(Color.parseColor("#999999"));
            imageViews.get(i).setImageDrawable(mContext.getResources().getDrawable(unselectedImage[i]));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
