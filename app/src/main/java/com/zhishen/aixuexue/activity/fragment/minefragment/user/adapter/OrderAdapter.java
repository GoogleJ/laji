package com.zhishen.aixuexue.activity.fragment.minefragment.user.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zhishen.aixuexue.activity.fragment.minefragment.user.fragment.OrderAllFragment;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.fragment.OrderOverFragment;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.fragment.OrderPaidFragment;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.fragment.PendingPaymentFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome on 09:35
 */
public class OrderAdapter extends FragmentStatePagerAdapter {

    private List<String> titles;
    private OrderAllFragment orderAllFragment;
    private OrderPaidFragment orderPaidFragment;
    private OrderOverFragment orderOverFragment;
    private PendingPaymentFragment paymentFragment;

    public OrderAdapter(FragmentManager sfm, List<String> titles) {
        super(sfm);
        this.titles = new ArrayList<>();
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        if (position >= 0 && position < titles.size()) {
            switch (position) {
                case 0: //全部
                    if (orderAllFragment == null) {
                        orderAllFragment = OrderAllFragment.getInstance();
                    }
                    return orderAllFragment;
                case 1://待付款
                    if (paymentFragment == null) {
                        paymentFragment = PendingPaymentFragment.getInstance();
                    }
                    return paymentFragment;
                case 2://已支付
                    if (orderPaidFragment == null) {
                        orderPaidFragment = OrderPaidFragment.getInstance();
                    }
                    return orderPaidFragment;
                case 3: //已完成
                    if (orderOverFragment == null) {
                        orderOverFragment = OrderOverFragment.getInstance();
                    }
                    return orderOverFragment;
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles == null ? 0 : titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= 0 && position < titles.size()) {
            return titles.get(position);
        }
        return null;
    }
}
