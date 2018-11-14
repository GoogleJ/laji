package com.zhishen.aixuexue.bean;

/**
 * Created by Jerome on 2018/7/2
 */
public class CouponBean {

    /**
     * price : 50$
     * title : 黑格伯爵口语训练课时抵用券
     * data : 有效时间：2018-06-30到期
     * desc : 仅限口语训练课时抵用
     * source : 黑格伯爵保留用户解释权
     */

    private String price;
    private String title;
    private String date;
    private String desc;
    private String source;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
