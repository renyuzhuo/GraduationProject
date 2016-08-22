package com.renyuzhuo.chat.net;

import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by RENYUZHUO on 2016/4/6.
 */
public abstract class NetOptionResponse {
    public abstract Listener<JSONObject> success();

    public ErrorListener error() {
        return new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        };
    }
}
