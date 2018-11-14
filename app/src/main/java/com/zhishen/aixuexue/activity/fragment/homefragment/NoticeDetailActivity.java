package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.bean.HomeNoticeBean;
import com.zhishen.aixuexue.bean.SystemNoticeBean;

/**
 * Created by Jerome on 2018/7/27
 */
public class NoticeDetailActivity extends BaseActivity {

    private Object obj;
    private String title,content;
    public static final String NOTICE_INFO = "notice_info";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_dt);

        obj = getIntent().getSerializableExtra(NOTICE_INFO);
        if (obj == null) return;

        if (obj instanceof HomeNoticeBean.NoticeBean) {
            HomeNoticeBean.NoticeBean noticeBean = (HomeNoticeBean.NoticeBean)obj;
            title = noticeBean.getTitle();
            content = noticeBean.getDesc();
        } else if (obj instanceof SystemNoticeBean) {
            SystemNoticeBean sysNoticeBean = (SystemNoticeBean) obj;
            title = sysNoticeBean.getTitle();
            content = sysNoticeBean.getContent();
        }

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        TextView tvContent = findViewById(R.id.tvContent);
        tvContent.setText(content);

    }
}
