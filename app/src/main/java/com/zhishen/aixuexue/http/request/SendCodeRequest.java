package com.zhishen.aixuexue.http.request;

import com.zhishen.aixuexue.http.BaseAesRequestData;

/**
 * Created by yangfaming on 2018/7/22.
 */

public class SendCodeRequest extends BaseAesRequestData {
    private String tel;

    public SendCodeRequest(String tel) {
        this.tel = tel;
    }
}
