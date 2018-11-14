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
public class TakePopWindow extends PopupWindow implements View.OnClickListener{

    private TextView tvCamera, tvPhoto, tvCancel;
    private View mView;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TakePopWindow(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.view_pop_bottom, null);
        tvCamera = mView.findViewById(R.id.tvCamera);
        tvPhoto = mView.findViewById(R.id.tvPhoto);
        tvCancel = mView.findViewById(R.id.tvCancel);
        setPopupWindow();
        tvCamera.setOnClickListener(this);
        tvPhoto.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @SuppressLint({"InlinedApi", "ClickableViewAccessibility"})
    private void setPopupWindow() {
        this.setContentView(mView);// 设置View
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.share_pop_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0xb0000000));// 设置背景透明
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
            onItemClickListener.setOnItemClick(v);
        }
    }

    public interface OnItemClickListener {
        void setOnItemClick(View v);
    }
}
