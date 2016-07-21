package com.renyuzhuo.chat.option;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.UserBriefSQL;
import com.renyuzhuo.chat.util.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserBriefOption {
    public static void getUserBriefById(int id, final String message, final String time) {
        Map<String, String> map = new HashMap<>();
        map.put("token", ChatApplication.getToken());
        map.put("myId", String.valueOf(ChatApplication.getUserId()));
        map.put("sid", String.valueOf(id));
        NetOption.postDataToUrl(Global.FRIEND_URL, map, new NetOptionResponse() {
            @Override
            public Response.Listener<JSONObject> success() {
                return new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray userinfos = response.getJSONArray("userinfos");
                            UserBrief userBrief;
                            for (int i = 0; i < userinfos.length(); i++) {
                                JSONObject userinfo = userinfos.getJSONObject(i);
                                userBrief = new UserBrief(userinfo.getInt("id"), userinfo.getString("nickname"), false, message, time, userinfo.getString("heap"));
                                UserBriefSQL.insertIntoUserBrief(userBrief, message, time);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
        });
    }
}
