package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import com.zhishen.aixuexue.http.BaseAesRequestData;

public class GetCommentRequest extends BaseAesRequestData {
    private String targetType;
    private String targetId;
    private int pageNo;

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}
