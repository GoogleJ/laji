package com.zhishen.aixuexue.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangfaming on 2018/6/25.
 */

public class HomeBean {


    /**
     * version : 1
     * tabbar : [{"desc":"底部导航","title":"世界","id":"tabbar_world","Image":"tabbar_world_image","selectImage":"tabbar_world_select_image","url":""},{"desc":"底部导航","title":"互动","id":"tabbar_interactive","Image":"tabbar_interactive_image","selectImage":"tabbar_interactive_select_image","url":""},{"desc":"底部导航","title":"发现","id":"tabbar_found","Image":"tabbar_found_image","selectImage":"tabbar_found_select_image","url":""},{"desc":"底部导航","title":"朋友圈","id":"tabbar_home","Image":"tabbar_home_image","selectImage":"tabbar_home_select_image","url":""},{"desc":"底部导航","title":"我的","id":"tabbar_my","Image":"tabbar_my_image","selectImage":"tabbar_my_select_image","url":""}]
     * world_banner : [{"image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/banner1.jpg","url":"https://m.baidu.com/s?word=12","id":"openWeb","desc":""},{"image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/banner2.jpg","url":"https://m.baidu.com/s?word=13","id":"openNativeBrowser","desc":""},{"image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/banner3.jpg","url":"https://m.baidu.com/s?word=14","id":"openNativeBrowser","desc":""}]
     * world_functional_areas : [{"id":"world_functional_areas_institutions","title":"机构","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/world_functional_areas_institutions_image.png"},{"id":"world_functional_areas_teacher","title":"老师","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/world_functional_areas_teacher_image.png"},{"id":"world_functional_areas_course","title":"课程","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/world_functional_areas_course_image.png"},{"id":"world_functional_areas_onlineTest","title":"在线测试","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/world_functional_areas_onlineTest_image.png"},{"id":"world_functional_areas_appointmentCourse","title":"约课","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/world_functional_areas_appointmentCourse_image.png"},{"id":"world_functional_areas_activity","title":"活动","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/world_functional_areas_activity_image.png"},{"id":"world_functional_areas_homework","title":"作业","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/world_functional_areas_homework_image.png"},{"id":"world_functional_areas_more","title":"更多","image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/world_functional_areas_more_image.png"}]
     * world_notice : {"image":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/world_notice_image.png","list":[{"title":"你有一张课程优惠券","id":"coupons","date":""},{"title":"继续播放小学三年级上册课程","id":"course","date":""},{"title":"请重新设置你的密码","id":"resetPassword","date":""}]}
     * world_information : [{"style":"styleA","id":"openWeb","title":"习近平会见全球首席执行官委员会代表","images":["http://192.168.10.8:8888/MAMP/zmhphp/education/image/image5.jpg"],"source":"来源","typeDesc":"","comments":"100评论","date":"一小时前","url":"https://m.baidu.com/s?word=0","desc":"左图右标题,用app浏览器打开"},{"style":"styleB","id":"openNativeBrowser","title":" 诚聘市场营销专员+晋升快+高提成+包住宿+岗位培训 ","images":["http://192.168.10.8:8888/MAMP/zmhphp/education/image/guanggao.png"],"source":"来源","typeDesc":"广告","comments":"","date":"","url":"https://m.baidu.com/s?word=1","desc":"上标题下面一张图,用手机浏览器打开"},{"style":"styleC","id":"openWeb","title":"月薪7000的外卖小哥，纷纷离职转行，外卖小哥说出了背后的原因！","images":["http://192.168.10.8:8888/MAMP/zmhphp/education/image/image1.jpg","http://192.168.10.8:8888/MAMP/zmhphp/education/image/image2.jpg","http://192.168.10.8:8888/MAMP/zmhphp/education/image/image3.jpg"],"source":"来源","typeDesc":"","comments":"100评论","date":"一小时前","url":"https://m.baidu.com/s?word=2","desc":"上标题下面三张图"},{"style":"styleD","id":"openVideo","title":"openWeb","images":["http://192.168.10.8:8888/MAMP/zmhphp/education/imageone.png"],"source":"来源","typeDesc":"","comments":"100评论","date":"一小时前","url":"https://m.baidu.com/s?word=3","desc":"样子和styleB相似,在图片正中间添加视频播放按钮,表示这是一个视频"}]
     * my : [{"groupTitle":"","list":[{"id":"my_shoucang","title":"我的收藏","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00003.png"},{"id":"my_jigou","title":"关注的机构","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00001.png"},{"id":"my_laoshi","title":"关注的老师","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00001.png"},{"id":"my_youhuiquan","title":"优惠券","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00001.png"}]},{"groupTitle":"","list":[{"id":"my_kechen","title":"我的课程","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00003.png"},{"id":"my_huihua","title":"我的会话","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00001.png"},{"id":"my_qunliao","title":"群聊会话","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00009.png"},{"id":"my_tongxunlu","title":"通讯录","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00011.png"}]},{"groupTitle":"我的课程","list":[{"id":"my_zuoye","title":"我的作业","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00017.png"},{"id":"my_qianbao","title":"我的钱包","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00008.png"},{"id":"my_dingdan","title":"我的订单","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00000.png"},{"id":"my_libao","title":"礼包中心","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00005.png"},{"id":"my_pinglun","title":"我的评论","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00018.png"},{"id":"my_lianxiwomen","title":"联系我们","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00006.png"},{"id":"my_shezhi","title":"设置","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00010.png"}]},{"groupTitle":"必备工具","list":[{"id":"my_jiancebaogao","title":"检测报告","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00002.png"},{"id":"my_yimiaozhushou","title":"疫苗助手","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00014.png"},{"id":"my_kechenbiao","title":"课程表","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00004.png"},{"id":"my_zuoxishijian","title":"作息时间","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00016.png"},{"id":"my_yuyinpingce","title":"语音评测","value":"0","badge":"","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/my_ico_00015.png"}]}]
     * map_pageConfig : {"topCatagory_desc":[{"key_desc":"用id区分不同类型"}],"topCatagory":[{"title":"附近的人","id":"nearPerson"},{"title":"附近的机构","id":"nearInstitutions"},{"title":"附近的老师","id":"nearTeacher"}],"bottomCatagory":[{"title":"附近的话题","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/remen.png","id":"id3"},{"title":"热门话题","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/fujing.png","id":"id4"}],"rightCatagory":[{"title":"附近的话题","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_dingwei.png","id":"id5"},{"title":"热门话题","ico":"http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_liebiao.png","id":"id6"}]}
     * notice : ["项目中按钮的背景图需要既能加载本地图标,又能加载网络图标,判断根据图片地址是否包含http判定"]
     */
    private String version;
    private MapPageConfigBean map_pageConfig;
    private List<ToolbarEntity> tabbar;
    private Share share;
    private List<BannerBean> world_banner;
    private List<Menu> world_functional_areas;
    private List<UserMenuBean> my;  //个人中心
    private List<HomeNewsCategory> news_category; //新闻资讯
    private String weixinNumber;
    private String weixinName;
    private String gvrp;

    public String getGvrp() {
        return gvrp;
    }

    public void setGvrp(String gvrp) {
        this.gvrp = gvrp;
    }

    public static class Share {
        private String title;
        private String url;
        private String content;
        private String image;

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public String getContent() {
            return content;
        }

        public String getImage() {
            return image;
        }
    }

    public static class Menu implements Serializable {
        private String id;
        private String title;
        private String image;
        private String url;

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public static class BannerBean implements Serializable {
        private String id;
        private String desc;
        private String url;
        private String image;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    public static class MapPageConfigBean {
        private List<TopCatagoryDescBean> topCatagory_desc;
        private List<TopCatagoryBean> topCatagory;
        private List<BottomCatagoryBean> bottomCatagory;


        public List<TopCatagoryDescBean> getTopCatagory_desc() {
            return topCatagory_desc;
        }

        public void setTopCatagory_desc(List<TopCatagoryDescBean> topCatagory_desc) {
            this.topCatagory_desc = topCatagory_desc;
        }

        public List<TopCatagoryBean> getTopCatagory() {
            return topCatagory;
        }

        public void setTopCatagory(List<TopCatagoryBean> topCatagory) {
            this.topCatagory = topCatagory;
        }

        public List<BottomCatagoryBean> getBottomCatagory() {
            return bottomCatagory;
        }

        public void setBottomCatagory(List<BottomCatagoryBean> bottomCatagory) {
            this.bottomCatagory = bottomCatagory;
        }

        public class TopCatagoryDescBean {
            /**
             * key_desc : 用id区分不同类型
             */

            private String key_desc;

            public String getKey_desc() {
                return key_desc;
            }

            public void setKey_desc(String key_desc) {
                this.key_desc = key_desc;
            }
        }

        public class TopCatagoryBean {
            /**
             * title : 附近的人
             * id : id0
             */

            private String title;
            private String id;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }

        public class BottomCatagoryBean {
            /**
             * title : 附近的话题
             * ico : icoid4
             * id : id3
             */

            private String title;
            private String ico;
            private String id;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getIco() {
                return ico;
            }

            public void setIco(String ico) {
                this.ico = ico;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }

    public static class ToolbarEntity {
        private String id;
        private String title;
        private String Image;
        private String selectImage;
        private String url;

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

        public String getImage() {
            return Image;
        }

        public void setImage(String image) {
            Image = image;
        }

        public String getSelectImage() {
            return selectImage;
        }

        public void setSelectImage(String selectImage) {
            this.selectImage = selectImage;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class HomeNewsCategory {

        public String title;
        public String typeid;

    }

    public static class UserMenuBean {

        public static final int USER_MENU_INFO = 1;
        public static final int USER_MENU = 2;
        public static final int USER_MENU_COURSE = 3;
        public static final int USER_MENU_TOOLS = 4;

        private String groupTitle = "";
        private int groupTitleid;
        private List<UserMenu> list;

        public String getGroupTitle() {
            return groupTitle;
        }

        public void setGroupTitle(String groupTitle) {
            this.groupTitle = groupTitle;
        }

        public int getGroupTitleid() {
            return groupTitleid;
        }

        public void setGroupTitleid(int groupTitleid) {
            this.groupTitleid = groupTitleid;
        }

        public List<UserMenu> getList() {
            return list;
        }

        public void setList(List<UserMenu> data) {

            this.list = data;
        }

        public static class UserMenu implements Serializable {
            private String id;
            private String title;
            private String count;
            private String badge;
            private String value;
            private String ico;
            private String url;

            public void setUrl(String url) {
                this.url = url;
            }

            public String getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getCount() {
                return count;
            }

            public String getBadge() {
                return badge;
            }

            public String getValue() {
                if (!TextUtils.isEmpty(this.getCount())) {
                    this.value = this.count;
                }
                return value;
            }

            public String getIco() {
                return ico;
            }

            public String getUrl() {
                return url;
            }
        }
    }

    public String getWeixinNumber() {
        return weixinNumber;
    }

    public void setWeixinNumber(String weixinNumber) {
        this.weixinNumber = weixinNumber;
    }

    public String getWeixinName() {
        return weixinName;
    }

    public void setWeixinName(String weixinName) {
        this.weixinName = weixinName;
    }

    public Share getShare() {
        return share;
    }

    public String getVersion() {
        return version;
    }

    public List<BannerBean> getHomeBanner() {
        return world_banner;
    }

    public List<Menu> getHomeMenu() {
        return world_functional_areas;
    }

    public MapPageConfigBean getMap_pageConfig() {
        return map_pageConfig;
    }

    public List<ToolbarEntity> getTabbar() {
        return tabbar;
    }

    public List<UserMenuBean> getUserMenus() {
        return my;
    }

    public List<HomeNewsCategory> getNewsCategory() {
        return news_category;
    }
}



