package com.zhishen.aixuexue.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhishen.aixuexue.R;

/**
 * Created by Jerome on 2018/7/7
 */
public class GroupPopWindow extends PopupWindow implements View.OnClickListener {

    private TextView tv_popup_publicgroup, tv_popup_privategroup, tv_popup_freegroup;
    private View mView;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public GroupPopWindow(Context context, int layout) {
        super(context);
        this.context = context;
        init(context, layout);
    }

    private void init(Context context, int layout) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(layout, null);
        tv_popup_publicgroup = mView.findViewById(R.id.tv_popup_publicgroup);
        tv_popup_privategroup = mView.findViewById(R.id.tv_popup_privategroup);
        tv_popup_freegroup = mView.findViewById(R.id.tv_popup_freegroup);
        setPopupWindow();
        tv_popup_publicgroup.setOnClickListener(this);
        tv_popup_privategroup.setOnClickListener(this);
        tv_popup_freegroup.setOnClickListener(this);
    }

    @SuppressLint({"InlinedApi", "ClickableViewAccessibility"})
    private void setPopupWindow() {
        this.setContentView(mView);// 设置View
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.share_pop_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.translucence)));// 设置背景透明
        // 如果触摸位置在窗口外面则销毁
        mView.setOnTouchListener((v, event) -> {
            int height = mView.findViewById(R.id.id_pop_layout).getTop();
            int y = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (y < height) {
                    dismiss();
                }
            }
            return true;
        });
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            switch (v.getId()) {
                case R.id.tv_popup_publicgroup:
                    onItemClickListener.setOnItemClick(v, tv_popup_publicgroup.getText().toString());
                    break;
                case R.id.tv_popup_privategroup:
                    onItemClickListener.setOnItemClick(v, tv_popup_privategroup.getText().toString());
                    break;
                case R.id.tv_popup_freegroup:
                    onItemClickListener.setOnItemClick(v, tv_popup_freegroup.getText().toString());
                    break;
            }
        }
    }

    public interface OnItemClickListener {
        void setOnItemClick(View v, String s);
    }
}
