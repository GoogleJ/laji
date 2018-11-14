package com.zhishen.aixuexue.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jerome on 2018/7/5
 */
public class SystemNoticeBean implements Serializable {

    /**
     * title : 系统维护
     * content : 系统将于今晚18时提及维护12小时
     * id : 0
     * actionID : openWeb
     * url : https://www.baidu.com/
     * date : 2018-07-08 16:35
     * ico : http://192.168.10.8:8888/MAMP/zmhphp/education/image/hbgj.png
     */

    PageBean page;
    private String title;
    private String content;
    private String id;
    private String actionID;
    private String url;
    private String date;
    private String ico;
    private boolean isRead;

    private List<SystemNoticeBean> list;


    public PageBean getPage() {
        return page;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getActionID() {
        return actionID;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public String getIco() {
        return ico;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public List<SystemNoticeBean> getList() {
        return list;
    }


}
