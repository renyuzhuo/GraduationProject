package com.renyuzhuo.chat.util;

import android.app.Activity;
import android.content.Context;

import com.renyuzhuo.chat.R;

public class Anim {
    public static void in(Context context) {
        if (context != null && context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }

    public static void out(Context context) {
        if (context != null && context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }
}
