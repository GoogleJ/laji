package com.zhishen.aixuexue.activity.fragment.findfragment;

public class FindResponse {
    private String id;
    private String type;  //titleview的类型 三公里聊天群、热门话题
    private String headUrl;
    private String title;
    private String groupMembers; //群员数量
    private String summary;  //概要
    private String typeId;
    private String creator;
    private String name;

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

    private int groupCount;
    private String groupType; //群类型 私密、付费 返回0，1，2 0代表私密+付费 1代表私密 2代表付费

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getGroupType() {
        return groupType;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getGroupMembers() {
        return groupMembers;
    }

    public String getSummary() {
        return summary;
    }
}
