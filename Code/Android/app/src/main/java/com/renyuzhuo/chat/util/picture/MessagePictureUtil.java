package com.renyuzhuo.chat.util.picture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.detail.ChatBaseDetailActivity;
import com.renyuzhuo.chat.detail.ChatDetailActivity;
import com.renyuzhuo.chat.detail.adapter.ChatDetailAdapter;
import com.renyuzhuo.chat.model.Message;
import com.renyuzhuo.chat.sql.MessageSQL;
import com.renyuzhuo.chat.util.Dialog;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.QiNiuUtil;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by RENYUZHUO on 2016/5/1.
 */
public class MessagePictureUtil extends PictureUtil {
    ChatDetailActivity chatDetailActivity;
    int toId;
    String s;
    ChatDetailAdapter chatDetailAdapter;

    public MessagePictureUtil(ChatDetailActivity chatDetailActivity, int toId, String s, ChatDetailAdapter chatDetailAdapter) {
        this.chatDetailActivity = chatDetailActivity;
        this.toId = toId;
        this.s = s;
        this.chatDetailAdapter = chatDetailAdapter;
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

    public File savePhotoBitmap(final Context context, Bitmap mBitmap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        File file = new File(Global.TEMP_PATH + "/" + sdf.format(new Date()) + ".jpg");
        FileOutputStream fOut = null;
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
            String url = Global.QINIU_PIC_BASIC_URL + res.getString("key");
            sendMessage(url);
        } catch (Exception e) {
            LogUtil.log("上传七牛云出错");
        }
    }

    public void sendMessage(String url) {
        Message message = new Message();
        message.setId(--ChatBaseDetailActivity.messageId);
        message.setFromuser(ChatApplication.getUserId());
        message.setTouser(toId);
        message.setMessage(s);
        message.setType(2);
        message.setPath(url);
        message.setToken(ChatApplication.getToken());
        message.setTime(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        message.setFromstate("sending");
        MessageSQL.insertIntoMessage(message);
        ChatService.sendMessage(message);
        chatDetailAdapter.setData(message);
        chatDetailAdapter.notifyDataSetChanged();
        chatDetailActivity.scrollToBottom();
    }
}
