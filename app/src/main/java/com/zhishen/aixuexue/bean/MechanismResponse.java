package com.zhishen.aixuexue.bean;

import java.util.List;

public class MechanismResponse {

    /**
     * desc : {"_1":"顶部背景图用头像做高斯模糊效果","_2":"活动详情可以跳转到某个课程详情,此处跳转链接统一处理","id":"唯一标示","headUrl":"用户头像","ico":"用户头像旁边的小logo","name":"名称","focus":"是否关注","location_name":"机构位置","customerServiceID":"客服会话编号","info":"机构介绍信息","teacherList_canappointment":"老师能否预约"}
     * id : 12312511
     * headUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_badge_person.png
     * ico : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_badge_teacher.png
     * name : 广州智深数据服务有限公司
     * focus : true
     * location_name : 山西省西安市雁塔区高新二路14号i创途众创公园inno park205
     * customerServiceID : 1
     * info :  企业文化是企业长期生产、经营、建设、发展过程中所形成的管理思想、管理方式、管理理论、群体意识以及与之相适应的思维方式和行为规范的总和。是企业领导层提倡、上下共同遵守的文化传统和不断革新的一套行为方式，它体现为企业价值观、经营理念和行为规范，渗透于企业的各个领域和全部时空。其核心内容是企业价值观、企业精神、企业经营理念的培育，是企业职工思想道德风貌的提高。通过企业文化的建设实施，使企业人文素质得以优化，归根结底是推进企业竞争力的提高，促进企业经济效益的增长。</h1>
     * teacherList : [{"id":"1231231","headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_teacher.png","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_badge_teacher.png","longitude":null,"latitude":null,"distance":"1.2KM","name":"老师1","sex":"女","course":["语文","数学","英语"],"summary":"1黑格伯爵提供更优秀的数字化...","canappointment":true},{"id":"1231232","headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_teacher.png","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_badge_teacher.png","longitude":0.1,"latitude":0.1,"distance":"1.4KM","name":"老师2","sex":"女","course":["语文","数学"],"summary":"2黑格伯爵提供更优秀的数字化...","canappointment":false}]
     * activityList : [{"id":"0","type":"openWeb","headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_person.png","activityImageUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/guanggao.png","title":"活动标题","url":"https://www.baidu.com"},{"id":"0","type":"openWeb","headUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_person.png","activityImageUrl":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/guanggao.png","title":"活动标题","url":"http://news.sogou.com/"}]
     * courseList : [{"id":"0","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/courseImage1.png","title":"JAVA大牛带你从0到上线开发企业级电商项目","tag":"高级","studyCount":"5636人学习","activityPrice":"¥600","originalPrice":"活动价格：￥300"},{"id":"1","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/courseImage2.png","title":"IOS大牛带你从0到上线开发企业级电商项目","tag":"高级","studyCount":"563人学习","activityPrice":"¥600","originalPrice":"活动价格：￥600"},{"id":"2","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/courseImage3.png","title":"Android大牛带你从0到上线开发企业级电商项目","tag":"高级","studyCount":"56人学习","activityPrice":"¥600","originalPrice":"活动价格：￥300"}]
     */

    private DescBean desc;
    private String id;
    private String headUrl;
    private String ico;
    private String name;
    private boolean focus;
    private String location_name;
    private String customerServiceID;
    private String info;
    private List<TeacherListBean> teacherList;
    private List<ActivityListBean> activityList;
    private List<CourseListBean> courseList;

    public DescBean getDesc() {
        return desc;
    }

    public void setDesc(DescBean desc) {
        this.desc = desc;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getCustomerServiceID() {
        return customerServiceID;
    }

    public void setCustomerServiceID(String customerServiceID) {
        this.customerServiceID = customerServiceID;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<TeacherListBean> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<TeacherListBean> teacherList) {
        this.teacherList = teacherList;
    }

    public List<ActivityListBean> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<ActivityListBean> activityList) {
        this.activityList = activityList;
    }

    public List<CourseListBean> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<CourseListBean> courseList) {
        this.courseList = courseList;
    }

    public static class DescBean {
        /**
         * _1 : 顶部背景图用头像做高斯模糊效果
         * _2 : 活动详情可以跳转到某个课程详情,此处跳转链接统一处理
         * id : 唯一标示
         * headUrl : 用户头像
         * ico : 用户头像旁边的小logo
         * name : 名称
         * focus : 是否关注
         * location_name : 机构位置
         * customerServiceID : 客服会话编号
         * info : 机构介绍信息
         * teacherList_canappointment : 老师能否预约
         */

        private String _1;
        private String _2;
        private String id;
        private String headUrl;
        private String ico;
        private String name;
        private String focus;
        private String location_name;
        private String customerServiceID;
        private String info;
        private String teacherList_canappointment;

        public String get_1() {
            return _1;
        }

        public void set_1(String _1) {
            this._1 = _1;
        }

        public String get_2() {
            return _2;
        }

        public void set_2(String _2) {
            this._2 = _2;
        }

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFocus() {
            return focus;
        }

        public void setFocus(String focus) {
            this.focus = focus;
        }

        public String getLocation_name() {
            return location_name;
        }

        public void setLocation_name(String location_name) {
            this.location_name = location_name;
        }

        public String getCustomerServiceID() {
            return customerServiceID;
        }

        public void setCustomerServiceID(String customerServiceID) {
            this.customerServiceID = customerServiceID;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getTeacherList_canappointment() {
            return teacherList_canappointment;
        }

        public void setTeacherList_canappointment(String teacherList_canappointment) {
            this.teacherList_canappointment = teacherList_canappointment;
        }
    }

    public static class TeacherListBean {
        /**
         * id : 1231231
         * headUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_teacher.png
         * ico : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_badge_teacher.png
         * longitude : null
         * latitude : null
         * distance : 1.2KM
         * name : 老师1
         * sex : 女
         * course : ["语文","数学","英语"]
         * summary : 1黑格伯爵提供更优秀的数字化...
         * canappointment : true
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
        private boolean canappointment;
        private List<String> course;

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

        public boolean isCanappointment() {
            return canappointment;
        }

        public void setCanappointment(boolean canappointment) {
            this.canappointment = canappointment;
        }

        public List<String> getCourse() {
            return course;
        }

        public void setCourse(List<String> course) {
            this.course = course;
        }
    }

    public static class ActivityListBean {
        /**
         * id : 0
         * type : openWeb
         * headUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_person.png
         * activityImageUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/guanggao.png
         * title : 活动标题
         * url : https://www.baidu.com
         */

        private String id;
        private String type;
        private String headUrl;
        private String activityImageUrl;
        private String title;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getActivityImageUrl() {
            return activityImageUrl;
        }

        public void setActivityImageUrl(String activityImageUrl) {
            this.activityImageUrl = activityImageUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class CourseListBean {
        /**
         * id : 0
         * image : http://192.168.10.8:8888/MAMP/zmhphp/education/image/courseImage1.png
         * title : JAVA大牛带你从0到上线开发企业级电商项目
         * tag : 高级
         * studyCount : 5636人学习
         * activityPrice : ¥600
         * originalPrice : 活动价格：￥300
         */

        private String id;
        private String image;
        private String title;
        private String tag;
        private String studyCount;
        private String activityPrice;
        private String originalPrice;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getStudyCount() {
            return studyCount;
        }

        public void setStudyCount(String studyCount) {
            this.studyCount = studyCount;
        }

        public String getActivityPrice() {
            return activityPrice;
        }

        public void setActivityPrice(String activityPrice) {
            this.activityPrice = activityPrice;
        }

        public String getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(String originalPrice) {
            this.originalPrice = originalPrice;
        }
    }
}
