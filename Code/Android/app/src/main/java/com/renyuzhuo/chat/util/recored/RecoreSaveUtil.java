package com.renyuzhuo.chat.util.recored;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.QiNiuUtil;
import com.renyuzhuo.chat.util.ZipUtil;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by RENYUZHUO on 2016/4/30.
 */
public abstract class RecoreSaveUtil {

    private static UploadManager uploadManager;

    public void saveRecord(final File file, final String name) {
        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }
        uploadManager.put(file, null, QiNiuUtil.videoToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                try {
                    String keyurl = res.getString("key");
                    String url = Global.QINIU_VIDEO_BASIC_URL + keyurl;
                    if (file.renameTo(new File(Global.VIDEO_PATH + "/" + keyurl))) {
                        LogUtil.log("录音重命名成功");
                    }
                    sendMessage(url, name);
                } catch (Exception e) {
                    LogUtil.log("上传录音失败");
                }
            }
        }, null);
    }

    public void saveRecord(String path, String name) {
        path = ZipUtil.zip(path, Global.voicePassword);
        if (path != null) {
            LogUtil.log(path);
            File file = new File(path);
            saveRecord(file, name);
        }
    }

    public abstract void sendMessage(String url, String name);

}
