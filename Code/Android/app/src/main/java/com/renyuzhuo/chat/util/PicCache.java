package com.renyuzhuo.chat.util;

import android.content.Context;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * 图片本地缓存
 * Created by RENYUZHUO on 2016/4/30.
 */
public class PicCache {
    /**
     *设置本地图片缓存路径
     */
    public static void loadImageCache(Context context) {
        final String imageCacheDir = Global.TEMP_PATH;
        Picasso picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(new File(imageCacheDir))).build();
        Picasso.setSingletonInstance(picasso);
    }

}
