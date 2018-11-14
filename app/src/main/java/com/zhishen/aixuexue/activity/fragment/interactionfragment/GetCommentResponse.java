package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import java.io.Serializable;
import java.util.List;

public class GetCommentResponse {
    /**
     * score : 4.9
     * page : {"pageNo":1,"pageSize":15,"count":15,"totalPage":1,"limit":15,"index":0}
     * list : [{"headUrl":"http://fanxin-file-server.oss-cn-shanghai.aliyuncs.com/E563EDBB-7F51-4E83-B01D-4249881C64F9.png","user_name":"wukong","user_id":"41004238","score":2,"content":"3","date":"null"},{"headUrl":"http://fanxin-file-server.oss-cn-shanghai.aliyuncs.com/E563EDBB-7F51-4E83-B01D-4249881C64F9.png","user_name":"wukong","user_id":"41004238","score":2,"content":"3","date":"null"}]
     */

    private double score;
    private PageBean page;
    private List<ListBean> list;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class PageBean {
        /**
         * pageNo : 1
         * pageSize : 15
         * count : 15
         * totalPage : 1
         * limit : 15
         * index : 0
         */

        private int pageNo;
        private int pageSize;
        private int count;
        private int totalPage;
        private int limit;
        private int index;

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public static class ListBean implements Serializable {
        /**
         * headUrl : http://fanxin-file-server.oss-cn-shanghai.aliyuncs.com/E563EDBB-7F51-4E83-B01D-4249881C64F9.png
         * user_name : wukong
         * user_id : 41004238
         * score : 2
         * content : 3
         * date : null
         */

        private String headUrl;
        private String user_name;
        private String user_id;
        private int score;
        private String content;
        private String date;

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

}
