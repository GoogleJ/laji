package com.zhishen.aixuexue.weight;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhishen.aixuexue.R;

import java.util.List;

/**
 * Created by yangfaming on 2018/7/7.
 */

public class HomeAlertDialog extends Dialog {
    private Context context;
    private String title;
    private String content;
    private TextView tvTitle;
    private TextView tvContent;
    private ListView listView;
    private List<String> items;

    public HomeAlertDialog(Context context, String title, String content, List<String> items, int style) {
        super(context, style);
        this.context = context;
        this.title = title;
        this.items = items;
        this.content = content;
    }

    public HomeAlertDialog(Context context, String title, String content, List<String> items, int style, OnItemClickListner onItemClickListnerint) {
        super(context, style);
        setContentView(R.layout.home_dialog_alert);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        params.width = (int) (d.widthPixels * 0.7);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        if (title != null) {
            tvTitle = window.findViewById(R.id.tv_homedialog_title);
            tvContent = window.findViewById(R.id.tv_homedialog_content);
            listView = window.findViewById(R.id.listview);
            tvTitle.setText(title);
            tvContent.setText(content);

        }
        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView et = (TextView) view;
                et.setGravity(Gravity.CENTER);
                et.setTextColor(context.getResources().getColor(R.color.font_blue));
                view = (View) et;
                return view;
            }
        });
        listView.setDivider(context.getResources().getDrawable(R.drawable.drawable_line));
        listView.setOnItemClickListener((adapterView, view, i, l) -> onItemClickListnerint.onClick(i, HomeAlertDialog.this));
    }

    public interface OnItemClickListner {
        void onClick(int position, HomeAlertDialog homeAlertDialog);
    }
}
