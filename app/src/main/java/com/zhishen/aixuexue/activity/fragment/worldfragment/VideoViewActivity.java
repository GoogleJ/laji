package com.zhishen.aixuexue.activity.fragment.worldfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.util.CommonUtils;

/**
 * Created by Jerome on 2018/7/22
 */
public class VideoViewActivity extends BaseActivity{


    private VideoView mVideoView;
    private RelativeLayout rlContent;
    public static final String VIDEO_PATH = "video_path";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);


        String path = getIntent().getStringExtra(VIDEO_PATH);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


        if (!TextUtils.isEmpty(path)) {
            CommonUtils.showDialog(this, "加载中..");
            mVideoView = findViewById(R.id.videoView);
            rlContent  = findViewById(R.id.rlContent);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.setVideoPath(path);
            mVideoView.setLayoutParams(layoutParams);

            mVideoView.setOnPreparedListener(mp -> {
                CommonUtils.cencelDialog();
                mVideoView.start();
            });
            mVideoView.setOnErrorListener((mp, what, extra) -> {
                mVideoView.stopPlayback();
                return true;
            });
            mVideoView.setOnCompletionListener(mediaPlayer -> {
                toast("播放完成");
                mVideoView.stopPlayback();
                onBackPressed();
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!mVideoView.isPlaying()) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView.canPause()) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

}
