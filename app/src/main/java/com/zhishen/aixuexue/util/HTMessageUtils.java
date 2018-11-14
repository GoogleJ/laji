package com.zhishen.aixuexue.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageFileBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageLocationBody;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.IMAction;

/**
 * Created by huangfangyi on 2017/7/8.
 * qq 84543217
 */

public class HTMessageUtils {
    /**
     * 创建撤回消息
     *
     * @param htMessage
     * @return
     */
    public static HTMessage creatWithDrowMsg(HTMessage htMessage) {
        String text = null;
        JSONObject jsonObject = htMessage.getAttributes();
        String userId = jsonObject.getString(Constant.JSON_KEY_HXID);
        String nick = jsonObject.getString(Constant.JSON_KEY_NICK);
        if (AiApp.getInstance().getUsername().equals(userId)) {
            text = AiApp.getContext().getString(R.string.revoke_content);
        } else {
            text = String.format(AiApp.getContext().getString(R.string.revoke_content_someone), nick);
        }
        jsonObject.put("action", 6001);
        HTMessage message1 = HTMessage.createTextSendMessage(htMessage.getUsername(), text);
        message1.setMsgId(htMessage.getMsgId());
        message1.setChatType(htMessage.getChatType());
        message1.setDirect(htMessage.getDirect());
        message1.setAttributes(jsonObject.toJSONString());
        message1.setTime(htMessage.getTime());
        message1.setLocalTime(htMessage.getLocalTime());
        message1.setFrom(htMessage.getFrom());
        message1.setTo(htMessage.getTo());
        message1.setStatus(htMessage.getStatus());
        message1.setExt(htMessage.getExt());
        HTClient.getInstance().messageManager().updateMessageInDB(message1);
        return message1;
    }
    /**
     * 更新红包消息
     *
     * @param htMessage
     * @return
     */
    public static void updateRpMessage(HTMessage htMessage, Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IMAction.RP_IS_HAS_OPEND).putExtra("message", htMessage));
    }


    public interface CallBack {
        void error();

        void completed(String localPath);
    }

    /**
     * 更新消息,收到已读回执
     *
     * @param htMessage
     */
    public static HTMessage updateHTMessage(HTMessage htMessage) {
        htMessage.setStatus(HTMessage.Status.READ);
        HTClient.getInstance().messageManager().updateMessageInDB(htMessage);
        return htMessage;
    }
    /**
     * 更新消息,收到已读回执
     *
     * @param htMessage
     */
    public static void setMessageRead(Context context, HTMessage htMessage) {
        htMessage.setStatus(HTMessage.Status.READ);
        HTClient.getInstance().messageManager().updateMessageInDB(htMessage);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_READ).putExtra("userId", htMessage.getUsername()));
    }
    /**
     * 更新消息已发送已读回执
     *
     * @param htMessage
     */
    public static HTMessage updateHTMessageCmdAsk(HTMessage htMessage) {
        htMessage.setStatus(HTMessage.Status.ACKED);
        HTClient.getInstance().messageManager().updateMessageInDB(htMessage);
        return htMessage;
    }
    /**
     * 下载文件
     *
     * @param htMessage
     * @param chatTo
     * @param context
     * @param callBack
     */
    public static void loadMessageFile(HTMessage htMessage, boolean isVideoThumb, String chatTo, final Activity context, final CallBack callBack) {
        if(!isVideoThumb){
            CommonUtils.showDialog(context,context.getString(R.string.loading));
        }
        HTPathUtils pathUtils = new HTPathUtils(chatTo, context);
        String remotePath = "";
        String fileName = "";
        String filePath = null;
        if (htMessage.getType() == HTMessage.Type.VOICE) {
            HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) htMessage.getBody();
            remotePath = htMessageVoiceBody.getRemotePath();
            fileName = htMessageVoiceBody.getFileName();
            filePath = pathUtils.getVoicePath().getAbsolutePath() + "/" + fileName;
        } else if (htMessage.getType() == HTMessage.Type.IMAGE) {
            HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();
            remotePath = htMessageImageBody.getRemotePath();
            fileName = htMessageImageBody.getFileName();
            filePath = pathUtils.getImagePath().getAbsolutePath() + "/" + fileName;
        } else if (htMessage.getType() == HTMessage.Type.FILE) {
            //TODO 文件消息待处理
            HTMessageFileBody htMessageFileBody = (HTMessageFileBody) htMessage.getBody();
            remotePath = htMessageFileBody.getRemotePath();
            fileName = htMessageFileBody.getFileName();
            filePath = pathUtils.getFilePath().getAbsolutePath() + "/" + fileName;
        } else if (htMessage.getType() == HTMessage.Type.VIDEO) {
            HTMessageVideoBody htMessageVideoBody = (HTMessageVideoBody) htMessage.getBody();
            if(isVideoThumb){
                remotePath = htMessageVideoBody.getThumbnailRemotePath();
                fileName =remotePath.substring(remotePath.lastIndexOf("/")+1);
                filePath = pathUtils.getVideoPath().getAbsolutePath() + "/" + fileName;
            }else {
                remotePath = htMessageVideoBody.getRemotePath();
                fileName = htMessageVideoBody.getFileName();
                filePath = pathUtils.getVideoPath().getAbsolutePath() + "/" + fileName;
            }

        } else if (htMessage.getType() == HTMessage.Type.LOCATION) {
            HTMessageLocationBody htMessageLocationBody = (HTMessageLocationBody) htMessage.getBody();
            remotePath = htMessageLocationBody.getRemotePath();
            fileName = htMessageLocationBody.getFileName();
            filePath = pathUtils.getImagePath().getAbsolutePath() + "/" + fileName;
        }
        final String finalFilePath = filePath;
        new OkHttpUtils(context).loadFile(remotePath, filePath, new OkHttpUtils.DownloadCallBack() {
            @Override
            public void onSuccess() {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        callBack.completed(finalFilePath);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.error();
                        CommonUtils.cencelDialog();
                    }
                });
            }
        });
    }
}
