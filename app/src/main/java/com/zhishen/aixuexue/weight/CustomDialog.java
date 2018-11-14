package com.zhishen.aixuexue.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhishen.aixuexue.R;

/**
 * Created by yangfaming on 2018/6/25.
 */

public class CustomDialog extends Dialog {
    private String string;
    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog(@NonNull Context context, int themeResId,String string) {
        super(context, themeResId);
        this.string=string;
    }

    protected CustomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView tvContent = view.findViewById(R.id.tv_dialog_content);
        tvContent.setText(string);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(view);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }
}
