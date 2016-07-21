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
import com.renyuzhuo.chat.detail.TeamDetailActivity;
import com.renyuzhuo.chat.detail.adapter.TeamDetailAdapter;
import com.renyuzhuo.chat.model.TeamMessage;
import com.renyuzhuo.chat.sql.TeamMessageSQL;
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
public class TeamMessagePictureUtil extends PictureUtil {
    TeamDetailActivity teamDetailActivity;
    int toId;
    String s;
    TeamDetailAdapter teamDetailAdapter;

    public TeamMessagePictureUtil(TeamDetailActivity teamDetailActivity, int toId, String s, TeamDetailAdapter teamDetailAdapter) {
        this.teamDetailActivity = teamDetailActivity;
        this.toId = toId;
        this.s = s;
        this.teamDetailAdapter = teamDetailAdapter;
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
            String url = Global.QINIU_PIC_BASIC_URL + res.getString("key");
            sendMessage(url);
        } catch (Exception e) {
            LogUtil.log("上传七牛云出错");
        }
    }

    public void sendMessage(String url) {
        TeamMessage teamMessage = new TeamMessage();
        teamMessage.setId(--ChatBaseDetailActivity.messageId);
        teamMessage.setFromuser(ChatApplication.getUserId());
        teamMessage.setToteam(toId);
        teamMessage.setMessage(s);
        teamMessage.setType(2);
        teamMessage.setPath(url);
        teamMessage.setToken(ChatApplication.getToken());
        teamMessage.setTime(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        teamMessage.setFromstate("sending");
        TeamMessageSQL.insertIntoMessage(teamMessage);
        ChatService.sendMessage(teamMessage);
        teamDetailAdapter.setData(teamMessage);
        teamDetailAdapter.notifyDataSetChanged();
        teamDetailActivity.scrollToBottom();
    }
}
