package com.renyuzhuo.chat.sms;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.renyuzhuo.chat.util.Global;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SMS {

    private static final String TAG = "SMS";

    private static EventHandler eventHandler;
    private Message message;

    public static final int
            RESULT_COMPLETE = 0,                // 回调完成
            EVENT_GET_VERIFICATION_CODE = 1,    // 获取验证码成功
            EVENT_SUBMIT_VERIFICATION_CODE = 2, // 提交验证码成功
            EVENT_GET_SUPPORTED_COUNTRIES = 3,  // 返回支持发送验证码的国家列表
            ERR = 4                             // 出错
                    ;
    public static final String CHINA = "86";

    public SMS(Activity context, final Handler handler) {
        SMSSDK.initSDK(context, Global.SMS_APP_KEY, Global.SMS_APP_SECRET);
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    message = new Message();
                    message.what = RESULT_COMPLETE;
                    handler.sendMessage(message);
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        message = new Message();
                        message.what = EVENT_GET_VERIFICATION_CODE;
                        handler.sendMessage(message);
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        message = new Message();
                        message.what = EVENT_SUBMIT_VERIFICATION_CODE;
                        handler.sendMessage(message);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        message = new Message();
                        message.what = EVENT_GET_SUPPORTED_COUNTRIES;
                        handler.sendMessage(message);
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    message = new Message();
                    message.what = ERR;
                    message.obj = "出错了";
                    handler.sendMessage(message);
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    public static void destroy() {
        if (eventHandler != null) {
            Log.d(TAG, "destroy()");
            SMSSDK.unregisterEventHandler(eventHandler);
        }
    }

}
