package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import java.util.List;

public class GetUserInfoResponse {

    /**
     * id : 1231251
     * headUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_person.png
     * ico : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_badge_teacher.png
     * longitude : null
     * latitude : null
     * distance : 1.2KM
     * name : 人名1
     * sex : 女
     * summary : 个性签名
     * address : 陕西省西安市雁塔区高新二路14号i创途众创公园inno park205
     * historyCourse : [{"headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-145614.png","id":"","title":"黑格伯爵口语练习","actionID":"打开课程信息"},{"headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-145621.png","id":"","title":"2黑格伯爵口语练习","actionID":"打开课程信息"},{"headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-145614.png","id":"","title":"黑格伯爵口语练习","actionID":"打开课程信息"},{"headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-145621.png","id":"","title":"黑格伯爵口语练习","actionID":"打开课程信息"}]
     * CircleFriends : [{"headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-150057.png","id":"","title":"黑格伯爵口语练习","actionID":"打开课程信息"},{"headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-150117.png","id":"","title":"2黑格伯爵口语练习","actionID":"打开课程信息"},{"headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-150139.png","id":"","title":"黑格伯爵口语练习","actionID":"打开课程信息"},{"headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-145621.png","id":"","title":"黑格伯爵口语练习","actionID":"打开课程信息"}]
     */

    private String id;
    private String headUrl;
    private String ico;
    private Object longitude;
    private Object latitude;
    private String distance;
    private String name;
    private String sex;
    private String summary;
    private String address;
    private List<Bean> historyCourse;
    private List<Bean> CircleFriends;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Bean> getHistoryCourse() {
        return historyCourse;
    }

    public void setHistoryCourse(List<Bean> historyCourse) {
        this.historyCourse = historyCourse;
    }

    public List<Bean> getCircleFriends() {
        return CircleFriends;
    }

    public void setCircleFriends(List<Bean> CircleFriends) {
        this.CircleFriends = CircleFriends;
    }

    public static class Bean {
        /**
         * headUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-145614.png
         * id :
         * title : 黑格伯爵口语练习
         * actionID : 打开课程信息
         */

        private String headUrl;
        private String id;
        private String title;
        private String actionID;

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
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

        public String getActionID() {
            return actionID;
        }

        public void setActionID(String actionID) {
            this.actionID = actionID;
        }
    }
}
