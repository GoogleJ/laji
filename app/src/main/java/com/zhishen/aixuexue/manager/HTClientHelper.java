package com.zhishen.aixuexue.manager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.client.HTOptions;
import com.htmessage.sdk.listener.HTConnectionListener;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CallMessage;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.ChatActivity;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.dbsqlite.InviteMessage;
import com.zhishen.aixuexue.dbsqlite.InviteMessgeDao;
import com.zhishen.aixuexue.dbsqlite.MomentsMessage;
import com.zhishen.aixuexue.dbsqlite.MomentsMessageDao;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.main.MainActivity;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.HTMessageUtils;

import java.util.List;

/**
 * Created by yangfaming on 2018/6/22.
 */

public class HTClientHelper {
    private static Context applicationContext;
    private String TAG = HTClientHelper.class.getName();

    private static HTClientHelper htClientHelper;

    public static void init(Context context) {
        htClientHelper = new HTClientHelper(context);
    }

    public HTClientHelper(Context context) {
        this.applicationContext = context;
        HTOptions htOptions = new HTOptions();
        htOptions.setDualProcess(true);
        htOptions.setDebug(true);
        HTClient.init(applicationContext, htOptions);
        HTClient.getInstance().setMessageLisenter(messageLisenter);
        HTClient.getInstance().addConnectionListener(htConnectionListener);
    }

    public static HTClientHelper getInstance() {

        if (htClientHelper == null) {
            throw new RuntimeException("please init first!");
        }
        return htClientHelper;
    }

    private HTConnectionListener htConnectionListener = new HTConnectionListener() {
        @Override
        public void onConnected() {
            notifyConnection(true);
        }

        @Override
        public void onDisconnected() {
            notifyConnection(false);
        }

        @Override
        public void onConflict() {
            notifyConflict(applicationContext);
        }
    };
    private HTClient.MessageLisenter messageLisenter = new HTClient.MessageLisenter() {
        @Override
        public void onHTMessage(HTMessage htMessage) {
            Log.d(TAG, "1212    =======    " + htMessage.toXmppMessageBody());
            if (htMessage.getChatType() == ChatType.groupChat) {
                String username = htMessage.getUsername();
                int action = htMessage.getIntAttribute("action", 0);
                if (action < 2006 && action > 1999) {
                    String groupName = htMessage.getStringAttribute("groupName");
                    String groupDescription = htMessage.getStringAttribute("groupDescription");
                    String groupAvatar = htMessage.getStringAttribute("groupAvatar");
                    HTGroup group = HTClient.getInstance().groupManager().getGroup(username);
                    if (group != null) {
                        if (!TextUtils.isEmpty(groupName)) {
                            group.setGroupName(groupName);
                        }
                        if (!TextUtils.isEmpty(groupAvatar)) {
                            group.setImgUrl(groupAvatar);
                        }
                        if (!TextUtils.isEmpty(groupDescription)) {
                            group.setGroupDesc(groupDescription);
                        }
                        HTClient.getInstance().groupManager().saveGroup(group);
                    }
                    HTClient.getInstance().groupManager().refreshGroupList();
                }
            }
            handleHTMessage(htMessage);

        }

        private void handleHTMessage(HTMessage htMessage) {
            List<HTConversation> list = HTClient.getInstance().conversationManager().getAllConversations();
            int unReadNumber = 0;
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    HTConversation conversation = list.get(i);
                    unReadNumber += conversation.getUnReadCount();
                }
            }
            Intent intent = new Intent(IMAction.ACTION_NEW_MESSAGE);
            intent.putExtra("message", htMessage);
            intent.putExtra("unReadNumber", unReadNumber);
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
            if (ChatActivity.activityInstance != null && htMessage.getUsername().equals(ChatActivity.activityInstance.getToChatUsername())) {
            } else {
                NotifierManager.getInstance().onNewMessage(htMessage);
            }
        }

        @Override
        public void onCMDMessgae(CmdMessage cmdMessage) {

            handleCmdMessage(cmdMessage);

        }

        @Override
        public void onCallMessgae(CallMessage callMessage) {
            handleCallMessage(callMessage);
        }
    };


    private void handleCmdMessage(CmdMessage cmdMessage) {
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(applicationContext);
        String data = cmdMessage.getBody();
        if (data != null) {
            JSONObject dataJSON = JSONObject.parseObject(data);
            if (dataJSON != null && dataJSON.containsKey("action")) {

                int action = dataJSON.getInteger("action");
                if (action == 1000) {
                    //收到好友申请的请求
                    List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                    for (InviteMessage inviteMessage : msgs) {
                        if (inviteMessage.getFrom().equals(cmdMessage.getFrom())) {
                            inviteMessgeDao.deleteMessage(cmdMessage.getFrom());
                        }
                    }
                    InviteMessage msg = new InviteMessage();
                    msg.setFrom(cmdMessage.getFrom());
                    msg.setTime(System.currentTimeMillis());
                    msg.setReason(dataJSON.getJSONObject("data").toJSONString());
                    //    Log.d(TAG, message.getFrom() + "apply to be your friend,reason: " + userInfo);
                    // set invitation status
                    msg.setStatus(InviteMessage.Status.BEINVITEED);
                    notifyNewInviteMessage(msg, null);
                } else if (action == 1001) {
                    //收到好友同意的透传消息
                    List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                    for (InviteMessage inviteMessage : msgs) {
                        if (inviteMessage.getFrom().equals(cmdMessage.getFrom())) {
                            inviteMessgeDao.deleteMessage(cmdMessage.getFrom());

                        }
                    }
                    // save invitation as message
                    InviteMessage msg = new InviteMessage();
                    msg.setFrom(cmdMessage.getFrom());
                    msg.setReason(dataJSON.getJSONObject("data").toJSONString());
                    msg.setTime(System.currentTimeMillis());
                    //   Log.d(TAG, message.getFrom() + "accept your request");
                    msg.setStatus(InviteMessage.Status.BEAGREED);
                    notifyNewInviteMessage(msg, dataJSON.getJSONObject("data"));
                } else if (action == 1002) {
                    //收到好友拒绝的透传消息
                    List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                    for (InviteMessage inviteMessage : msgs) {
                        if (inviteMessage.getFrom().equals(cmdMessage.getFrom())) {
                            inviteMessgeDao.deleteMessage(cmdMessage.getFrom());
                        }
                    }
                    // save invitation as message
                    InviteMessage msg = new InviteMessage();
                    msg.setFrom(cmdMessage.getFrom());
                    msg.setReason(dataJSON.getJSONObject("data").toJSONString());
                    msg.setTime(System.currentTimeMillis());
                    msg.setStatus(InviteMessage.Status.BEREFUSED);
                    notifyNewInviteMessage(msg, null);
                } else if (action == 1003) {
                    //收到删除好友的透传消息
                    //发送广播
                    if (AiApp.getInstance().getUsername().equals(cmdMessage.getTo()) || AiApp.getInstance().getUsername().equals(cmdMessage.getFrom())) {
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.CMD_DELETE_FRIEND).putExtra(Constant.JSON_KEY_HXID, cmdMessage.getFrom()));
                    }
                } else if (action == 2004) {
                    //收到你被踢出某群的消息
                    String groupId = dataJSON.getString("data");
                    HTClient.getInstance().groupManager().deleteGroupLocalOnly(groupId);
                    // notify ui
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_REMOVED_FROM_GROUP).putExtra("groupId", groupId));
                } else if (action == 6000) {//收到撤回消息的透传
                    String msgId = dataJSON.getString("msgId");
                    String chatTo = cmdMessage.getTo();
                    if (cmdMessage.getChatType() == ChatType.singleChat) {
                        chatTo = cmdMessage.getFrom();
                    }
//                    HTClient.getInstance().messageManager().deleteMessage(chatTo, msgId);
                    HTMessage htMessage = HTClient.getInstance().messageManager().getMssage(chatTo, msgId);
                    if (htMessage != null) {
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_WITHDROW).putExtra("msgId", msgId));
                    }
                } else if (action == 7000) {//收到朋友圈点赞或者评论的消息
                    JSONObject momentsData = dataJSON.getJSONObject("data");
                    int typeInt = momentsData.getInteger("type");
                    MomentsMessage.Type type = MomentsMessage.Type.GOOD;
                    if (typeInt == 1) {
                        type = MomentsMessage.Type.COMMENT;
                    } else if (typeInt == 2) {
                        type = MomentsMessage.Type.REPLY_COMMENT;
                    }
                    MomentsMessage momentsMessage = new MomentsMessage();
                    momentsMessage.setTime(System.currentTimeMillis());
                    momentsMessage.setUserNick(momentsData.getString("nickname"));
                    momentsMessage.setUserId(momentsData.getString("userId"));
                    momentsMessage.setMid(momentsData.getString("mid"));
                    momentsMessage.setStatus(MomentsMessage.Status.UNREAD);
                    momentsMessage.setImageUrl(momentsData.getString("imageUrl"));
                    momentsMessage.setUserAvatar(momentsData.getString("avatar"));
                    momentsMessage.setContent(momentsData.getString("content"));
                    momentsMessage.setType(type);
                    MomentsMessageDao momentsMessageDao = new MomentsMessageDao(applicationContext);
                    momentsMessageDao.savaMomentsMessage(momentsMessage);
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_MOMENTS));
                } else if (action == 6999) {//收到附近的朋友圈点赞或者评论的消息
                    JSONObject momentsData = dataJSON.getJSONObject("data");
                    int typeInt = momentsData.getInteger("type");
                    MomentsMessage.Type type = MomentsMessage.Type.GOOD;
                    if (typeInt == 1) {
                        type = MomentsMessage.Type.COMMENT;
                    } else if (typeInt == 2) {
                        type = MomentsMessage.Type.REPLY_COMMENT;
                    }
                    MomentsMessage momentsMessage = new MomentsMessage();
                    momentsMessage.setTime(System.currentTimeMillis());
                    momentsMessage.setUserNick(momentsData.getString("nickname"));
                    momentsMessage.setUserId(momentsData.getString("userId"));
                    momentsMessage.setMid(momentsData.getString("mid"));
                    momentsMessage.setStatus(MomentsMessage.Status.UNREAD);
                    momentsMessage.setImageUrl(momentsData.getString("imageUrl"));
                    momentsMessage.setUserAvatar(momentsData.getString("avatar"));
                    momentsMessage.setContent(momentsData.getString("content"));
                    momentsMessage.setType(type);
                    MomentsMessageDao momentsMessageDao = new MomentsMessageDao(applicationContext);
                    momentsMessageDao.savaMomentsMessage(momentsMessage);
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_MOMENTS_NEAR_BY));
                } else if (action == 30000) {//被禁言
                    String to = cmdMessage.getTo();
                    JSONObject jsonObject = dataJSON.getJSONObject("data");
                    CommonUtils.sendNoTalkBrocast(applicationContext, to, jsonObject);
                } else if (action == 30001) {//解除禁言
                    String to = cmdMessage.getTo();
                    JSONObject jsonObject = dataJSON.getJSONObject("data");
                    CommonUtils.sendCancleNoTalkBrocast(applicationContext, to, jsonObject);
                } else if (action == 8888) {
                    if (cmdMessage.getChatType() != ChatType.singleChat) {
                        return;
                    }
                    String msgId = dataJSON.getString("msgId");
                    String userId = cmdMessage.getFrom();

                    HTMessage htMessage = HTClient.getInstance().messageManager().getMssage(userId, msgId);
                    if (htMessage != null) {
                    }
                } else if (action == 10006) {

                }
            }
        }
    }

    private void handleCallMessage(CallMessage callMessage) {
        String data = callMessage.getBody();
        if (data != null) {
            JSONObject dataJSON = JSONObject.parseObject(data);
            if (dataJSON != null && dataJSON.containsKey("action")) {

                int action = dataJSON.getInteger("action");
                if (action == 3000 || action == 4000 || action == 5000) {

                    //收到语音电话呼叫
                    if (!AiApp.isCalling) {
                        //如果没在通话,进入接听界面
                        if (action == 5000) {
                            JSONObject object = dataJSON.getJSONObject("data");
                            String callId = object.getString("callId");
                            if (object.containsKey("callId_add")) {
                                String callId_add = object.getString("callId_add");
                                if (!callId_add.contains(AiApp.getInstance().getUsername())) {
                                    return;
                                }
                            } else {
                                if (!callId.contains(AiApp.getInstance().getUsername())) {
                                    return;
                                }
                            }
                        }
                        if (action == 4000) {
//                            startToCall(dataJSON, action, VideoIncomingActivity.class);
                        } else {
//                            startToCall(dataJSON, action, VoiceIncomingActivity.class);
                        }
                    } else {
                        if (callMessage.getChatType() == ChatType.singleChat) {
                            //发送繁忙的通知
                            JSONObject reJSON = new JSONObject();
                            JSONObject infoJSON = new JSONObject();
                            infoJSON.put("userId", AiApp.getInstance().getUserJson().getString("userId"));
                            infoJSON.put("nick", AiApp.getInstance().getUserJson().getString("nick"));
                            infoJSON.put("avatar", AiApp.getInstance().getUserJson().getString("avatar"));
                            infoJSON.put("callId", dataJSON.getJSONObject("data").getString("callId"));
                            reJSON.put("action", 3004);
                            reJSON.put("data", infoJSON);
                            CallMessage mCallMessage = new CallMessage();
                            mCallMessage.setBody(reJSON.toJSONString());
                            mCallMessage.setTo(callMessage.getFrom());
                            HTClient.getInstance().chatManager().sendCallMessage(mCallMessage, new HTChatManager.HTMessageCallBack() {
                                @Override
                                public void onProgress() {
                                    Log.d("action3004", "onProgress");
                                }

                                @Override
                                public void onSuccess() {
                                    Log.d("action3004", "onSuccess");
                                }

                                @Override
                                public void onFailure() {
                                    Log.d("action3004", "onFailure");
                                }
                            });
                        }
                    }
                } else if (action == 3001 || action == 3002 || action == 4002 || action == 4001) {
                    //3001对方取消了呼叫
                    //3002对方拒绝了你的语音电话
                    //3005对方挂断了电话
                    // 4002对方拒绝的语音申请
                    //4001对方取消了呼叫
                    if (AiApp.isCalling) {
                        //如果没在通话,进入接听界面
                        if (action == 3001) {
//                            startToCall(dataJSON, action, VoiceIncomingActivity.class);
                        } else if (action == 3002) {
//                            startToCall(dataJSON, action, VoiceOutgoingActivity.class);
                        } else if (action == 4002) {
//                            startToCall(dataJSON, action, VideoOutgoingActivity.class);
                        } else if (action == 4001) {
//                            startToCall(dataJSON, action, VideoIncomingActivity.class);
                        }
                    }
                } else if (action == 3003 || action == 4003) {
                    //对方接听了你的语音电话;
                    //对方接听了你的视频通话;
                    if (AiApp.isCalling) {
                        //如果没在通话,进入接听界面
//                        Intent intent = new Intent();
                        if (action == 4003) {
//                            startToCall(dataJSON, action, VideoOutgoingActivity.class);
                        } else {
//                            startToCall(dataJSON, action, VoiceOutgoingActivity.class);
                        }
                    }
                } else if (action == 3005 || action == 4004 || action == 3004 || action == 5002) {
                    //3005-对方双方有一方挂断电话
                    //4004-视频聊天双方有一方切换成了语音通话
                    //3004-对方正在语音或者视频通话,繁忙中
                    //5002-群视频通话时,未接通状态下,发起者关闭呼叫
                    Intent intent = new Intent();

                    intent.putExtra("data", dataJSON.getJSONObject("data").toJSONString());
                    intent.setAction(action + "");
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
                }
            }
        }
    }

    public void notifyConflict(Context context) {
        Intent intent = new Intent(applicationContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IMAction.ACTION_CONFLICT, true);
        context.startActivity(intent);
    }

    /**
     * user has logged into another device
     */
    protected void notifyConnection(boolean isConnected) {
        Intent intent = new Intent(IMAction.ACTION_CONNECTION_CHANAGED);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("state", isConnected);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    private void notifyNewInviteMessage(final InviteMessage msg, final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(applicationContext);
                inviteMessgeDao.saveMessage(msg);
                inviteMessgeDao.saveUnreadMessageCount(1);
                LocalUserManager.getInstance().setHasUnReadInviateNubmer(true);
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_INVITE_MESSAGE));
                NotifierManager.getInstance().vibrateAndPlayTone();
                if (jsonObject != null) {
                    User user = CommonUtils.Json2User(jsonObject);
                    ContactsManager.getInstance().saveContact(user);
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_CONTACT_CHANAGED));
                }
            }
        }).start();
    }

    /**
     * 发送透传
     *
     * @param htMessage
     */
    private void makeHTMessaseAsked(final HTMessage htMessage) {
        CmdMessage cmdMessage = new CmdMessage();
        JSONObject jsonObject = new JSONObject();
        cmdMessage.setTo(htMessage.getFrom());
        jsonObject.put("action", 8888);
        jsonObject.put("msgId", htMessage.getMsgId());
        cmdMessage.setBody(jsonObject.toString());
        HTClient.getInstance().chatManager().sendCmdMessage(cmdMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "----已读透传发送成功");
                HTMessageUtils.updateHTMessageCmdAsk(htMessage);

            }

            @Override
            public void onFailure() {
                Log.d(TAG, "----已读透传发送失败");
            }
        });
    }

    public void sendAndCheckAcked(HTMessage htMessage) {
        //TODO 判断会否发送了透传
        HTMessage.Status status = htMessage.getStatus();
        if (status != HTMessage.Status.ACKED && status != HTMessage.Status.READ && htMessage.getChatType().equals(ChatType.singleChat)) {
            makeHTMessaseAsked(htMessage);
        }
    }
}
