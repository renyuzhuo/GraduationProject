package com.renyuzhuo.chat.util;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;

import org.json.JSONObject;

import java.io.File;

public abstract class QiNiuUtil {
    public static final String token = getToken();
    public static final String errToken = getErrToken();
    public static final String videoToken = getVideoToken();
    public static final String QINIU_AK = "M2JIGRw1AWb5MP4rJp_hVja73M8kRnWrFf9NUaGP";
    public static final String QINIU_SK = "14zbd0D0x51XggRL_E_IIXwXICEAlnXQb5sSWdzC";
    public static final String QINIU_BUCKNAME = "chat";
    public static final String QINIU_BUCKNAME_ERR = "errlog";
    public static final String QINIU_VIDEO = "video";

    private static String getToken() {
        Mac mac = new Mac(QiNiuUtil.QINIU_AK, QiNiuUtil.QINIU_SK);
        PutPolicy putPolicy = new PutPolicy(QiNiuUtil.QINIU_BUCKNAME);
        putPolicy.returnBody = "{\"name\": $(fname),\"size\": \"$(fsize)\",\"w\": \"$(imageInfo.width)\",\"h\": \"$(imageInfo.height)\",\"key\":$(etag)}";
        try {
            String uptoken = putPolicy.token(mac);
            return uptoken;
        } catch (Exception e) {
            LogUtil.log("获取七牛上传token出错");
        }
        return null;
    }

    private static String getErrToken() {
        Mac mac = new Mac(QiNiuUtil.QINIU_AK, QiNiuUtil.QINIU_SK);
        PutPolicy putPolicy = new PutPolicy(QiNiuUtil.QINIU_VIDEO);
        putPolicy.returnBody = "{\"name\": $(fname),\"size\": \"$(fsize)\",\"w\": \"$(imageInfo.width)\",\"h\": \"$(imageInfo.height)\",\"key\":$(etag)}";
        try {
            String uptoken = putPolicy.token(mac);
            return uptoken;
        } catch (Exception e) {
            LogUtil.log("获取七牛上传token出错");
        }
        return null;
    }

    private static String getVideoToken() {
        Mac mac = new Mac(QiNiuUtil.QINIU_AK, QiNiuUtil.QINIU_SK);
        PutPolicy putPolicy = new PutPolicy(QiNiuUtil.QINIU_VIDEO);
        putPolicy.returnBody = "{\"name\": $(fname),\"size\": \"$(fsize)\",\"w\": \"$(imageInfo.width)\",\"h\": \"$(imageInfo.height)\",\"key\":$(etag)}";
        try {
            String uptoken = putPolicy.token(mac);
            return uptoken;
        } catch (Exception e) {
            LogUtil.log("获取七牛上传token出错");
        }
        return null;
    }

    public static void putErrLogFile() {
        File file = new File(Global.ERROR_LOG_PATH);
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, null, errToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                    }
                }, null);
    }

}
