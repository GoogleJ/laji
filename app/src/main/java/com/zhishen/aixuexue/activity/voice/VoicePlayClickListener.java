package com.zhishen.aixuexue.activity.voice;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.manager.HTClientHelper;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.HTMessageUtils;

import java.io.File;

/**
 * Created by huangfangyi on 2016/12/5.
 * qq 84543217
 */

public class VoicePlayClickListener implements View.OnClickListener {
    private static final String TAG = "VoicePlayClickListener";
    HTMessage message;
    ImageView voiceIconView;

    private AnimationDrawable voiceAnimation = null;
    MediaPlayer mediaPlayer = null;
    ImageView iv_read_status;
    Activity activity;
    private ChatType chatType;
    private BaseAdapter adapter;

    public static boolean isPlaying = false;
    public static VoicePlayClickListener currentPlayListener = null;
    public static String playMsgId;
    String chatTo;

    public VoicePlayClickListener(HTMessage message, String chatTo, ImageView v, ImageView iv_read_status, BaseAdapter adapter, Activity context) {
        this.message = message;
        this.iv_read_status = iv_read_status;
        this.adapter = adapter;
        voiceIconView = v;
        this.activity = context;
        this.chatType = message.getChatType();
        this.chatTo = chatTo;
    }

    public void stopPlayVoice() {
        voiceAnimation.stop();
        if (message.getDirect() == HTMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.ad1);
        } else {
            voiceIconView.setImageResource(R.drawable.adj);
        }
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        CommonUtils.muteAudioFocus(activity, false);
        isPlaying = false;
        playMsgId = null;
        adapter.notifyDataSetChanged();
    }

    public void playVoice(String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }
        Log.e("", "-----播放的地址：" + filePath);
        playMsgId = message.getMsgId();
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
//        if (SettingsManager.getInstance().getSettingMsgSpeaker()) {
        audioManager.setMode(AudioManager.STREAM_SYSTEM);
        audioManager.setSpeakerphoneOn(true);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
//        }
//        else {
//            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
//            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
//            audioManager.setMode(AudioManager.MODE_IN_CALL);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//        }
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                }
            });
            CommonUtils.muteAudioFocus(activity, true);
            isPlaying = true;
            currentPlayListener = this;
            showAnimation();

            if (message.getDirect() == HTMessage.Direct.RECEIVE) {
                if (message.getChatType() == ChatType.groupChat && message.getStatus() != HTMessage.Status.SUCCESS && iv_read_status != null) {
                    // 隐藏自己未播放这条语音消息的标志
                    iv_read_status.setVisibility(View.INVISIBLE);
                    HTClient.getInstance().messageManager().updateSuccess(message);
                } else if (message.getChatType() == ChatType.singleChat && message.getStatus() != HTMessage.Status.ACKED && iv_read_status != null) {
                    iv_read_status.setVisibility(View.INVISIBLE);
                    // 告知对方已读这条消息
                    HTClientHelper.getInstance().sendAndCheckAcked(message);
                    HTClient.getInstance().messageManager().updateSuccess(message);
                }
            }
        } catch (Exception e) {
            Log.e("", "---播放出错:" + e.getMessage());
            System.out.println();
        }
    }

    // show the voice playing animation
    private void showAnimation() {
        // play voice, and start animation
        if (message.getDirect() == HTMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.voice_from_icon);
        } else {
            voiceIconView.setImageResource(R.drawable.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
        voiceAnimation.start();
    }

    @Override
    public void onClick(View v) {
        if (isPlaying) {
            if (playMsgId != null && playMsgId.equals(message.getMsgId())) {
                currentPlayListener.stopPlayVoice();
                return;
            }
            currentPlayListener.stopPlayVoice();
        }
        HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) message.getBody();
        String loaclPath = htMessageVoiceBody.getLocalPath();
        Log.e("", "----localPath---2222:" + loaclPath);
        if (message.getDirect() == HTMessage.Direct.SEND) {
            playVoice(loaclPath);
        } else {
            if (!TextUtils.isEmpty(loaclPath)) {
                if (message.getStatus() == HTMessage.Status.SUCCESS || message.getStatus() == HTMessage.Status.ACKED) {
                    File file = new File(loaclPath);
                    if (file.exists() && file.isFile()) {
                        Log.e("", "----localPath----11111:" + loaclPath);
                        playVoice(loaclPath);
                    } else {
                        Log.e(TAG, "file not exist");
                    }
                }
            } else {
                downLoadVoiceFileFromServer(message);
            }
        }
    }


    private void downLoadVoiceFileFromServer(final HTMessage htMessage) {
        HTMessageUtils.loadMessageFile(htMessage, false, chatTo, activity, new HTMessageUtils.CallBack() {
            @Override
            public void error() {

            }

            @Override
            public void completed(String localPath) {
                playVoice(localPath);
                HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) htMessage.getBody();
                htMessageVoiceBody.setLocalPath(localPath);
                htMessage.setBody(htMessageVoiceBody);
                HTClient.getInstance().messageManager().updateSuccess(htMessage);
            }
        });
    }
}
