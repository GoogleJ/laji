package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import com.zhishen.aixuexue.weight.filterview.FilterEntity;
import com.zhishen.aixuexue.weight.filterview.FilterTwoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome on 2018/7/7
 */
public class DataServer {

    public static List<FilterTwoEntity> getNearData() {
        List<FilterTwoEntity> list = new ArrayList<>();
        list.add(new FilterTwoEntity("附近", getFilterNear()));
        list.add(new FilterTwoEntity("海门市", getFilterData()));
        list.add(new FilterTwoEntity("启东市", getFilterData()));
        return list;
    }

    public static List<FilterTwoEntity> getLanguage() {
        List<FilterTwoEntity> list = new ArrayList<>();
        list.add(new FilterTwoEntity("全部", getLanguageChildren()));
        list.add(new FilterTwoEntity("学习培训", getLanguageChildren()));
        list.add(new FilterTwoEntity("语言培训", getLanguageChildren()));
        list.add(new FilterTwoEntity("音乐培训", getLanguageChildren()));
        list.add(new FilterTwoEntity("职业技术", getLanguageChildren()));
        list.add(new FilterTwoEntity("升学辅导", getLanguageChildren()));
        return list;
    }

    private static List<FilterEntity> getFilterNear() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("附近", "0"));
        list.add(new FilterEntity("1km", "1"));
        list.add(new FilterEntity("3km", "2"));
        list.add(new FilterEntity("5km", "3"));
        list.add(new FilterEntity("10km", "4"));
        list.add(new FilterEntity("全城", "5"));
        return list;
    }

    public static List<FilterEntity> getFilterData() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("崇川区", "1"));
        list.add(new FilterEntity("港闸区", "2"));
        list.add(new FilterEntity("通州区", "3"));
        list.add(new FilterEntity("如皋区", "4"));
        list.add(new FilterEntity("海安县", "5"));
        list.add(new FilterEntity("如东县", "6"));
        return list;
    }

    public static List<FilterEntity> getLanguageChildren() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("全部", "0"));
        list.add(new FilterEntity("英语", "1"));
        list.add(new FilterEntity("汉语", "2"));
        list.add(new FilterEntity("日语", "3"));
        list.add(new FilterEntity("韩语", "4"));
        list.add(new FilterEntity("德语", "5"));
        return list;
    }

    public static List<FilterEntity> getSortData() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("智能排序", "1"));
        list.add(new FilterEntity("离我最近", "2"));
        list.add(new FilterEntity("好评优先", "3"));
        list.add(new FilterEntity("人气最高", "4"));
        return list;
    }
}
