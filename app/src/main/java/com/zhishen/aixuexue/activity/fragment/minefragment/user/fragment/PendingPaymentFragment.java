package com.zhishen.aixuexue.activity.fragment.minefragment.user.fragment;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseLazyFragment;

/**
 * Created by Jerome on 2018/7/3
 */
public class PendingPaymentFragment extends BaseLazyFragment {

    public static PendingPaymentFragment getInstance() {
        return new PendingPaymentFragment();
    }

    @Override
    protected void initDependencies() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_pendingpay;
    }

    @Override
    protected void initEventView() {

    }

    @Override
    protected void getDataFromServer() {

    }
}
