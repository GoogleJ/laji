package com.zhishen.aixuexue.bean;

import java.util.List;

public class SignResponse {

    private List<Bean> data;

    public List<Bean> getData() {
        return data;
    }

    public static class Bean {
        //特殊情况：当天且已签到：显示一张图片（圆形对勾）

        private String content; //显示的内容（不只是日期 可能是积分）
        private boolean hasSigned; //是否签到过
        private boolean hasScore;  //是否有积分

        public void setContent(String content) {
            this.content = content;
        }

        public void setHasSigned(boolean hasSigned) {
            this.hasSigned = hasSigned;
        }

        public void setHasScore(boolean hasScore) {
            this.hasScore = hasScore;
        }

        public String getContent() {
            return content;
        }

        public boolean isHasSigned() {
            return hasSigned;
        }

        public boolean isHasScore() {
            return hasScore;
        }
    }
}
