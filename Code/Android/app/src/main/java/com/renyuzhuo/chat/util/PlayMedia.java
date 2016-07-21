package com.renyuzhuo.chat.util;

import android.media.MediaPlayer;
import android.view.View;

import java.io.IOException;

/**
 * Created by RENYUZHUO on 2016/5/1.
 */
public class PlayMedia implements View.OnClickListener {

    private String playurl;
    private static MediaPlayer mPlayer = null;

    public PlayMedia(String playurl) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        this.playurl = playurl;
        LogUtil.log("播放文件路径:" + playurl);
    }

    @Override
    public void onClick(View v) {
        try {
            LogUtil.log("播放");
            mPlayer.reset();
            mPlayer.setDataSource(playurl);
            mPlayer.prepare();
            mPlayer.start();
        } catch (Exception e) {
            LogUtil.log("播放出错了");
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(playurl);
                mPlayer.prepare();
                mPlayer.start();
                e.printStackTrace();
            } catch (IOException e1) {
            }
        }
    }
}

