package com.renyuzhuo.chat.net;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by RENYUZHUO on 2016/4/6.
 */
public class NetOption {

    private static Context context;
    private static RequestQueue mQueue;

    public NetOption(Context context) {
        this.context = context;
        mQueue = Volley.newRequestQueue(context);
    }

    public static void postDataToUrl(String url, Map<String, String> params, NetOptionResponse netOptionResponse) {
        if(params != null){
            mQueue.add(new JsonObjectRequest(url, new JSONObject(params), netOptionResponse.success(), netOptionResponse.error()));
        }else{
            mQueue.add(new JsonObjectRequest(url, null, netOptionResponse.success(), netOptionResponse.error()));
        }
    }

}
