package com.zhishen.aixuexue.weight;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.SignDetailResponse;
import com.zhishen.aixuexue.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

public class SignLayout extends LinearLayout {

    public List<SignDetailResponse.ListBean> items = new ArrayList<>();

    int defaultBacColor = Color.parseColor("#f2f2f2");
    int checkedBacColor = Color.parseColor("#F74E2B");

    int defaultTextColor = Color.parseColor("#999999");
    int checkedTextColor = Color.parseColor("#FEFEFE");

    Drawable defaultRes = ContextCompat.getDrawable(getContext(), R.drawable.ic_sign_score_unchecked);
    Drawable checkedRes = ContextCompat.getDrawable(getContext(), R.drawable.ic_sign_score_check);

    public SignLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        createItem();
    }

    public void setItemChecked(boolean check, int index) {
        if (index >= items.size()) {
            return;
        }

        LinearLayout linearLayout = (LinearLayout) getChildAt(index);
        LinearLayout linearLayout1 = (LinearLayout) linearLayout.getChildAt(0);

        TextView textView = (TextView) linearLayout1.getChildAt(0);
        ImageView imageView = (ImageView) linearLayout1.getChildAt(1);

        linearLayout1.setBackgroundColor(check ? checkedBacColor : defaultBacColor);
        textView.setTextColor(check ? checkedTextColor : defaultTextColor);
        imageView.setImageDrawable(check ? checkedRes : defaultRes);
    }

    int index;

    public void createItem() {
        if (items.size() == 0) {
            return;
        }

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        linearLayout.setLayoutParams(layoutParams);

        addView(linearLayout);

        LinearLayout linearLayout1 = new LinearLayout(getContext());
        linearLayout1.setOrientation(VERTICAL);
        linearLayout1.setBackgroundColor(defaultBacColor);
        linearLayout1.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout1.setPadding(ScreenUtil.dip2px(getContext(), 6), ScreenUtil.dip2px(getContext(), 4), ScreenUtil.dip2px(getContext(), 6), ScreenUtil.dip2px(getContext(), 4));

        TextView textView = new TextView(getContext());
        textView.setTextColor(defaultTextColor);
        textView.setTextSize(11);
        textView.setText(items.get(index).getScore() + "");
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView imageView = new ImageView(getContext());
        LayoutParams layoutParams1 = new LayoutParams(ScreenUtil.dip2px(getContext(), 16), ScreenUtil.dip2px(getContext(), 16));
        imageView.setImageDrawable(defaultRes);

        linearLayout1.addView(textView);
        linearLayout1.addView(imageView);
        imageView.setLayoutParams(layoutParams1);

        TextView textView1 = new TextView(getContext());
        textView1.setTextColor(Color.parseColor("#333333"));
        textView1.setTextSize(11);
        textView1.setText(items.get(index).getData());
        LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.topMargin = ScreenUtil.dip2px(getContext(), 8);
        textView1.setLayoutParams(layoutParams2);

        linearLayout.addView(linearLayout1);
        linearLayout.addView(textView1);

        linearLayout1.setGravity(Gravity.CENTER);
        linearLayout.setGravity(Gravity.CENTER);


        index += 1;
        if (index != items.size()) {
            createItem();
        } else {
            int i = ScreenUtil.dip2px(getContext(), 8);
            setPadding(0, i, 0, i);
            index = 0;
        }
    }
}
