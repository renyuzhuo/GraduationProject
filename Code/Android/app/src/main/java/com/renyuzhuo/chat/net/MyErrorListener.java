package com.renyuzhuo.chat.net;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.renyuzhuo.chat.ChatMainActivity;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.ToastUtil;

/**
 * Created by RENYUZHUO on 2016/4/29.
 */
public class MyErrorListener implements Response.ErrorListener {

    private final Context context;

    public MyErrorListener(Context context) {
        this.context = context;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        int statusCode;
        if (error != null && error.networkResponse != null) {
            statusCode = error.networkResponse.statusCode;
            LogUtil.elog(context.getResources().getString(R.string.server_still_work));
        } else {
            LogUtil.elog(context.getResources().getString(R.string.error_network));
            return;
        }
        LogUtil.elog("getFriendErr: statusCode:" + statusCode);
        switch (statusCode) {
            case 403: {
                ToastUtil.showToast(context, context.getResources().getString(R.string.server_403));
                ChatMainActivity.logout();
                break;
            }
            case 500: {
                ToastUtil.showToast(context, context.getResources().getString(R.string.server_500));
                break;
            }
        }
    }
}