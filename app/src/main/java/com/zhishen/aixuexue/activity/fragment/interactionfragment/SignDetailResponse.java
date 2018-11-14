package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import java.util.List;

public class SignDetailResponse {

    /**
     * integral : 14
     * describe : 1、签到当日获得的积分实时累加，不清零；
     * 2、连续签到，获得积分加速，如终端签到则从5积分从新开始加速；
     * 3、签到获得的积分可以用来兑换商城里的礼品，礼品会定期更新，具体兑换规则以单个礼品介绍里详细规则尾椎；
     * 4、签到是需要保持登陆状态，领取的积分会绑定到该账户上。
     * list : [{"score":3,"data":"07-08","isSignIn":"true"},{"score":1,"data":"07-10","isSignIn":"true"},{"score":3,"data":"07-11","isSignIn":"true"},{"score":"4","data":"07-12","isSignIn":"false"},{"score":"5","data":"07-13","isSignIn":"false"},{"score":"6","data":"07-14","isSignIn":"false"}]
     * integralDesc : 今日签到+3!连续签到3天
     */

    private String integral;
    private String describe;
    private String integralDesc;
    private List<ListBean> list;
    private boolean isSignIn;

    public boolean isSignIn() {
        return isSignIn;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getIntegralDesc() {
        return integralDesc;
    }

    public void setIntegralDesc(String integralDesc) {
        this.integralDesc = integralDesc;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * score : 3
         * data : 07-08
         * isSignIn : true
         */

        private int score;
        private String data;
        private String isSignIn;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getIsSignIn() {
            return isSignIn;
        }

        public void setIsSignIn(String isSignIn) {
            this.isSignIn = isSignIn;
        }
    }
}
