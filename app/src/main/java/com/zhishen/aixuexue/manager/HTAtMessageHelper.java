package com.zhishen.aixuexue.manager;

import com.alibaba.fastjson.JSONArray;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.main.AiApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HTAtMessageHelper {
    private List<String> toAtUserList = new ArrayList<String>();
    private Set<String> atMeGroupList = null;
    private static HTAtMessageHelper instance = null;

    public synchronized static HTAtMessageHelper get() {
        if (instance == null) {
            instance = new HTAtMessageHelper();
        }
        return instance;
    }


    private HTAtMessageHelper() {
        if (atMeGroupList == null) {
            atMeGroupList = new HashSet<String>();
        }
    }

    /**
     * add user you want to @
     *
     * @param username
     */
    public void addAtUser(String username) {
        synchronized (toAtUserList) {
            if (!toAtUserList.contains(username)) {
                toAtUserList.add(username);
            }
        }
    }




    public boolean containsAtAll(String content) {
        String atAll = Constant.ATHOLDER + AiApp.getContext().getString(R.string.all_members) + Constant.PLACEHOLDER;
        if (content.equals(atAll)) {
            return true;
        }
        return false;
    }





    /**
     * get groups which I was mentioned
     *
     * @return
     */
    public Set<String> getAtMeGroups() {
        return atMeGroupList;
    }


    /**
     * check if the input groupId in atMeGroupList
     *
     * @param groupId
     * @return
     */
    public boolean hasAtMeMsg(String groupId) {
        return atMeGroupList.contains(groupId);
    }

    public JSONArray atListToJsonArray(List<String> atList) {
        JSONArray jArray = new JSONArray();
        int size = atList.size();
        for (int i = 0; i < size; i++) {
            String username = atList.get(i);
            jArray.add(username);
        }
        return jArray;
    }

    public void cleanToAtUserList() {
        synchronized (toAtUserList) {
            toAtUserList.clear();
        }
    }

}
