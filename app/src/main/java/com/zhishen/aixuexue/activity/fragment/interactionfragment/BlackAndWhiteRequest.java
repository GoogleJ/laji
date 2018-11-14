package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import com.zhishen.aixuexue.http.BaseAesRequestData;

public class BlackAndWhiteRequest extends BaseAesRequestData {
    private String fromUserid;
    private String toContentid;
    private String operateType;
    private String contentType;

    public void setFromUserid(String fromUserid) {
        this.fromUserid = fromUserid;
    }

    public void setToContentid(String toContentid) {
        this.toContentid = toContentid;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
