package com.zhishen.aixuexue.activity.fragment.interactionfragment;

public class GetInstitutionsResponse {

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
     */

    private String id;
    private String headUrl;
    private String ico;
    private String name;
    private boolean focus;
    private String location_name;
    private String customerServiceID;
    private String serviceMobile;
    private String info;

    public String getServiceMobile() {
        return serviceMobile;
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

}
