package com.zhishen.aixuexue.http.response;

import com.zhishen.aixuexue.activity.fragment.findfragment.FindResponse;

import java.util.List;

/**
 * Created by yangfaming on 2018/7/14.
 */

public class GroupListRespone {


    /**
     * titledesc : 三公里
     * tyepe : 3
     * list : [{"gid":"1","name":"测试","creator":"70001039","descri":"","imgurl":"","create_date":"1528195284546","version":""}]
     */

    private String titledesc;
    private String type;
    private List<ListBean> list;
    private List<FindResponse> listGroupforAndroid;

    public String getType() {
        return type;
    }

    public List<FindResponse> getListGroupforAndroid() {
        return listGroupforAndroid;
    }

    public String getTitledesc() {
        return titledesc;
    }

    public void setTitledesc(String titledesc) {
        this.titledesc = titledesc;
    }

    public String getTyepe() {
        return type;
    }

    public void setTyepe(String tyepe) {
        this.type = tyepe;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * gid : 1
         * name : 测试
         * creator : 70001039
         * descri :
         * imgurl :
         * create_date : 1528195284546
         * version :
         */

        private String gid;
        private String name;
        private String creator;
        private String descri;
        private String imgurl;
        private String create_date;
        private String version;
        private String type;
        private int userCount;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getUserCount() {
            return userCount;
        }

        public void setUserCount(int userCount) {
            this.userCount = userCount;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public String getDescri() {
            return descri;
        }

        public void setDescri(String descri) {
            this.descri = descri;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
