package com.zhishen.aixuexue.activity.fragment.worldfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;

/**
 * Created by Jerome on 2018/7/13
 */
public class WorldFakeActivity extends BaseActivity {

    public static final String CIRCLE_TYPE = "circle_type";
    public static final String CIRCLE_UID = "circle_uid";
    public static final int MINE_WORLD_CIRCLE = 1; //获取自己的朋友圈动态,则展示所有好友
    private WorldFragment worldFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fakeworld);



        if (worldFragment == null){
            Bundle bundle = new Bundle();
            worldFragment = new WorldFragment();
            bundle.putInt(CIRCLE_TYPE, getIntent().getIntExtra(CIRCLE_TYPE, 0));
            bundle.putString(CIRCLE_UID, getIntent().getStringExtra(CIRCLE_UID));
            worldFragment.setArguments(bundle);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.flContainer, worldFragment, WorldFragment.class.getSimpleName())
                .commitAllowingStateLoss();
    }
}
