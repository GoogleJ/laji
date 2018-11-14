package com.zhishen.aixuexue.http;

import java.util.List;

/**
 * Created by FangJie on 16/5/8.
 */
public class BaseResponseDataList<E> {
    public String msg;
    public int code;
    public List<E> data;
}
