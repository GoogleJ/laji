package com.zhishen.aixuexue.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhishen.aixuexue.R;

import java.lang.reflect.Field;

/**
 * Created by Jerome on 2018/6/27
 */
public class ViewUtils {

    public static View getCommEmpty(Activity activity, RecyclerView recyclerView,String hint) {
        View view = activity.getLayoutInflater().inflate(R.layout.view_empty, (ViewGroup) recyclerView.getParent(),false);
        TextView textView = view.findViewById(R.id.tv_empty_tips);
        textView.setText(TextUtils.isEmpty(hint)?"暂无内容":hint);
        return view;
    }

    public static View getCommEmptyRes(Activity activity,RecyclerView recyclerView,String hint,int resId){
        View view = activity.getLayoutInflater().inflate(R.layout.view_empty, (ViewGroup) recyclerView.getParent(),false);
        TextView textView = view.findViewById(R.id.tv_empty_tips);
        Drawable drawable = ContextCompat.getDrawable(activity, resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null,drawable,null,null);
        textView.setText(TextUtils.isEmpty(hint)?"暂无内容":hint);
        return view;
    }

    public static void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }
}
