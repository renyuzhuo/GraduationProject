package com.renyuzhuo.chat.util.picture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.Response;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.util.Dialog;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.QiNiuUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by RENYUZHUO on 2016/4/30.
 */
public class MePictureUtil extends PictureUtil {

    private final ImageView imageView;

    public MePictureUtil(ImageView imageView) {
        this.imageView = imageView;
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Dialog.PHOTOHRAPH) {
                LogUtil.log(Global.TEMP_PATH + "/" + Dialog.tempPath);
                File picture = new File(Global.TEMP_PATH + "/" + Dialog.tempPath);
                Dialog.startPhotoZoom(context, Uri.fromFile(picture));
            }
            if (requestCode == Dialog.PHOTOZOOM) {
                Dialog.startPhotoZoom(context, data.getData());
            }
            if (requestCode == Dialog.PHOTORESOULT) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    savePhotoBitmap(context, photo);
                }
            }
        }
    }

    /**
     * 保存图片处理
     *
     * @param mBitmap bitmap位图
     */
    public File savePhotoBitmap(final Context context, Bitmap mBitmap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        File file = new File(Global.TEMP_PATH + "/" + sdf.format(new Date()) + ".jpg");
        FileOutputStream fOut;
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }
        uploadManager.put(file, null, QiNiuUtil.token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        completePut(context, key, info, res);
                    }
                }, null);
        return file;
    }

    public void completePut(final Context context, final String key, ResponseInfo info, JSONObject res) {
        LogUtil.log("qiniu:", key + ",\r\n " + info + ",\r\n " + res);
        try {
            final Map<String, String> params = new HashMap<>();

            final String heap = Global.QINIU_PIC_BASIC_URL + res.getString("key");

            params.put("heap", heap);
            params.put("token", ChatApplication.getToken());
            params.put("myId", String.valueOf(ChatApplication.getUserId()));

            NetOption.postDataToUrl(Global.UPDATE_HEAP_URL, params, new NetOptionResponse() {
                @Override
                public Response.Listener<JSONObject> success() {
                    return new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ChatApplication.saveHeap(heap);
                            Picasso.with(context).load(heap).into(imageView);
                        }
                    };
                }
            });
        } catch (Exception e) {
            LogUtil.log("上传七牛云出错");
        }
    }

}
