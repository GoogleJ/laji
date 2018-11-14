package com.zhishen.aixuexue.http.response;

import java.util.List;

/**
 * Created by yangfaming on 2018/7/12.
 */

public class HomeAleartBean {

    /**
     * title : 标题
     * message : 下面的按钮会跳转到原生功能
     * actions : [{"title":"去签到","actionID":"sign","paramter":""},{"title":"我的优惠券","actionID":"my_youhuiquan","paramter":""},{"title":"优惠活动列表","actionID":"favourableActivity","paramter":""},{"title":"通讯录","actionID":"my_tongxunlu","paramter":""},{"title":"系统消息","actionID":"my_systemMessage","paramter":""},{"title":"设置重启显示启动图","actionID":"qdt","paramter":""},{"title":"取消","actionID":"","paramter":""}]
     */

    private String title;
    private String message;
    private List<ActionsBean> actions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ActionsBean> getActions() {
        return actions;
    }

    public void setActions(List<ActionsBean> actions) {
        this.actions = actions;
    }

    public static class ActionsBean {
        /**
         * title : 去签到
         * actionID : sign
         * paramter :
         */

        private String title;
        private String actionID;
        private String paramter;

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

        public String getParamter() {
            return paramter;
        }

        public void setParamter(String paramter) {
            this.paramter = paramter;
        }
    }
}
