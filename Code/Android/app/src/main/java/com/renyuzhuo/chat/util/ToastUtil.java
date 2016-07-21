package com.renyuzhuo.chat.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by RENYUZHUO on 2016/4/7.
 */
public class ToastUtil {
    private static Toast mToast;

    public static void showToast(Context context, String text) {
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
