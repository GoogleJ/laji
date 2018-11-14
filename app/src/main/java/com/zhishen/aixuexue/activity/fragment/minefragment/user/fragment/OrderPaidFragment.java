package com.zhishen.aixuexue.activity.fragment.minefragment.user.fragment;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseLazyFragment;

/**
 * Created by Jerome on 2018/7/3
 */
public class OrderPaidFragment extends BaseLazyFragment {


    public static OrderPaidFragment getInstance() {
        return new OrderPaidFragment();
    }

    @Override
    protected void initDependencies() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_paid;
    }

    @Override
    protected void initEventView() {

    }

    @Override
    protected void getDataFromServer() {

    }
}
