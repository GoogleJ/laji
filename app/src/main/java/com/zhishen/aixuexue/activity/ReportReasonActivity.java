package com.zhishen.aixuexue.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zhishen.aixuexue.R;

public class ReportReasonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_reason);
    }

    public void back(View view) {
        finish();
    }

    public void reason1(View view) {
        Intent mIntent = new Intent();
        mIntent.putExtra("reason", "色情低俗");
        this.setResult(1, mIntent);
        finish();
    }

    public void reason2(View view) {
        Intent mIntent = new Intent();
        mIntent.putExtra("reason", "政治敏感");
        this.setResult(1, mIntent);
        finish();
    }

    public void reason3(View view) {
        Intent mIntent = new Intent();
        mIntent.putExtra("reason", "违法犯罪");
        this.setResult(1, mIntent);
        finish();
    }

    public void reason4(View view) {
        Intent mIntent = new Intent();
        mIntent.putExtra("reason", "冒充官员");
        this.setResult(1, mIntent);
        finish();
    }

    public void reason5(View view) {
        Intent mIntent = new Intent();
        mIntent.putExtra("reason", "造谣传谣、涉嫌欺诈");
        this.setResult(1, mIntent);
        finish();
    }

    public void reason6(View view) {
        Intent mIntent = new Intent();
        mIntent.putExtra("reason", "侮辱谩骂");
        this.setResult(1, mIntent);
        finish();
    }

}
