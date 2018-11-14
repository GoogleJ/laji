package com.zhishen.aixuexue.util;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.model.HTMessage;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.main.AiApp;

/**
 * Created by huangfangyi on 2017/2/5.
 * qq 84543217
 */

public class GroupNoticeMessageUtils {
//    创建群：             	群普通消息  message.ext = {"action":"2000","groupName":"群名称","groupDescription":"群描述","groupAvatar":"群头像"}
//               			message.body.content ="\"某某群\"创建成功"
//    更新群名称：			群普通消息  message.ext = {"action":"2001","groupName":"群名称","groupAvatar":"群头像","groupDescription":"群描述","uid":"用户id","nickName":"用户昵称"}  									UI展示为：群主自己 -> "你修改了群信息"  群成员 -> ""某人"修改群信息"
//    往群里加人：          群普通消息  message.ext = {"action":"2003","groupName":"群名称","groupDescription":"群描述","groupAvatar":"群头像","members":[{"uid":"第一个用户id","nickName":"第一个用户昵称"},{"uid":"第二个用户id","nickName":"第二个用户昵称"}]}
//    从群里移除群成员：	群普通消息  message.ext = {"action":"2004","uid":"被移除的群成员id","nickName":"被移除的群成员昵称"}
//    个人透传              message.body = {"action":"2004":"data":"当前群id"}
    private final static String ACTION="action";
    private final static String GROUP_NAME="groupName";
    private final static String GROUP_DESCRIPTION="groupDescription";
    private final static String GROUP_AVATAR="groupAvatar";
    private final static String UID="uid";
    private final static String NICK_NAME="nickName";
    private final static String MEMBERS="members";


    public static String getGroupNoticeContent(HTMessage htMessage, Context context){
        String content="";
         //附加字段json
        int action=htMessage.getIntAttribute(ACTION,0);
        if(action!=0){
            String groupName=htMessage.getStringAttribute("groupName");
            String uid=htMessage.getStringAttribute("uid");
            String nickName=htMessage.getStringAttribute("nickName");
            JSONArray members=htMessage.getJSONArrayAtrribute("members");
            switch (action){
                case 2000:
                    content=context.getString(R.string.group_chat)+" "+"\""+groupName+"\""+" "+context.getString(R.string.create_success);
                    break;
                case 2001:
                    if(AiApp.getInstance().getUsername().equals(uid)){
                        content=context.getString(R.string.you_change_group_info);
                    }else{
                        content="\""+nickName+"\""+" "+context.getString(R.string.change_group_info);
                    }
                    break;
                case 2003:
                    String membersList="";
                    if(members!=null){
                        for(int i=0;i<members.size();i++){
                            JSONObject jsonObject=members.getJSONObject(i);
                            if(i==0){
                                membersList+="\""+jsonObject.getString(NICK_NAME);
                            }else {
                                membersList+="、"+jsonObject.getString(NICK_NAME);
                            }
                        }
                    }
                    content=membersList+"\""+" "+context.getString(R.string.join_the_group);
                    break;
                case 2004:
                    content="\""+nickName+"\""+" "+context.getString(R.string.remove_group);
                    break;
                case 3000:
                    content=context.getString(R.string.promotion_msg);
                    break;
                case 6001:
                    if(AiApp.getInstance().getUsername().equals(uid)){
                        content= String.format(context.getString(R.string.revoke_one_msg),context.getString(R.string.you));
                    }else{
                        content= String.format(context.getString(R.string.revoke_one_msg),"\""+nickName+"\""+"");
                    }
                    break;
            }
         }
        return content;
    }
}
