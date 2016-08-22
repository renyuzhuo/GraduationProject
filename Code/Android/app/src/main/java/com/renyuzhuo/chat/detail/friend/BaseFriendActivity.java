package com.renyuzhuo.chat.detail.friend;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.renyuzhuo.chat.util.LogUtil;

/**
 * Created by RENYUZHUO on 2016/4/25.
 */
public class BaseFriendActivity extends AppCompatActivity {

    private static Activity showActivity1;
    private static Activity showActivity2;

    public static void keepOneFriendActivity(Activity activity) {
        LogUtil.log("新的聊天窗口");
        if (showActivity1 != null) {
            showActivity1.finish();
            showActivity2 = activity;
            showActivity1 = null;
        } else {
            showActivity1 = activity;
            if (showActivity2 != null) {
                showActivity2.finish();
                showActivity2 = null;
            }
        }
    }
}
