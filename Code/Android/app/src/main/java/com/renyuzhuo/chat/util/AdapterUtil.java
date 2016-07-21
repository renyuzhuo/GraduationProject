package com.renyuzhuo.chat.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class AdapterUtil {

    public static View getView(Context context, int resource) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resource, null);
    }
}
