package com.zhishen.aixuexue.bean;

import com.zhishen.aixuexue.weight.layout.ImageInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome on 2018/7/16
 */
public class NewsBean implements Serializable {

    private PageBean page;
    private List<NewsBean> list;

    private String style;
    private String id;
    private String title;
    private List<String> images;
    private String source;
    private String comments;
    private String date;
    private String url;
    private String desc;
    private String contentType;
    private String actionid;
    private String actionidparameter;
    private String up;//点赞数

    //点赞专用
    private boolean flag = true;

    public int getStyle() {
        switch (style) {
            case "styleA":
                return MultiTypeItem.HOME_NEWS_LEFT;
            case "styleB":
                return MultiTypeItem.HOME_NEWS_IMG;
            case "styleC":
                return MultiTypeItem.HOME_NEWS_BOTTOM_IMGS;
            case "styleD":
                return MultiTypeItem.HOME_NEWS_VIDEO;
            case "styleE":
                return MultiTypeItem.HOME_NEWS_TXT;
            default:
                break;
        }
        return 0;
    }

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<NewsBean> getList() {
        return list;
    }


    public void setList(List<NewsBean> list) {
        this.list = list;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ImageInfo> getImageList(){
        List<ImageInfo> data = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            data.add(new ImageInfo(images.get(i),images.get(i)));
        }
        return data;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getActionid() {
        return actionid;
    }

    public void setActionid(String actionid) {
        this.actionid = actionid;
    }

    public String getActionidparameter() {
        return actionidparameter;
    }

    public void setActionidparameter(String actionidparameter) {
        this.actionidparameter = actionidparameter;
    }

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

}
