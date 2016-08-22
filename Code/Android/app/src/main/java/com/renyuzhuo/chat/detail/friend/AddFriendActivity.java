package com.renyuzhuo.chat.detail.friend;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.friend.adapter.AddFriendAdapter;
import com.renyuzhuo.chat.detail.friend.adapter.InviteFriendAdapter;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.SoftkeyboardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity {

    EditText number;
    Button search;
    ListView listView;
    AddFriendAdapter addFriendAdapter;
    private Context context;
    List<Friend> friends;
    private InviteFriendAdapter inviteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        context = this;

        initViewIds();
        initOnClickListener();
        initInvite();
    }

    private void initInvite() {

        friends = FriendSQL.getFriendsOfInviteMe();
        inviteAdapter = new InviteFriendAdapter(context, friends);
        listView.setAdapter(inviteAdapter);

        Map map = new HashMap();
        map.put("myId", ChatApplication.getUserId());
        map.put("token", ChatApplication.getToken());
        NetOption.postDataToUrl(Global.INVITE_URL, map, new NetOptionResponse() {
            @Override
            public Response.Listener<JSONObject> success() {
                return new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray friendArray = response.getJSONArray("friends");
                            FriendSQL.insertInviteFriendIntoChatFriend(friendArray);
                            friends = FriendSQL.getFriendsOfInviteMe();
                            inviteAdapter.setFriends(friends);
                        } catch (JSONException e) {
                            LogUtil.elog("initInvite JSONException");
                        }
                    }
                };
            }
        });
    }

    private void initOnClickListener() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(number.getText().toString())) {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", ChatApplication.getToken());
                    map.put("myId", String.valueOf(ChatApplication.getUserId()));
                    map.put("phone", number.getText().toString());
                    NetOption.postDataToUrl(Global.USERINFO_URL, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("res").equals("success")) {
                                            JSONArray userinfos = response.getJSONArray("userinfos");
                                            List<UserBrief> userBriefs = new ArrayList<>();
                                            for (int i = 0; i < userinfos.length(); i++) {
                                                JSONObject userBrief = userinfos.getJSONObject(i);

                                                UserBrief userBriefObj = new UserBrief();

                                                userBriefObj.setId(userBrief.getInt("id"));
                                                userBriefObj.setNickname(userBrief.getString("nickname"));
                                                userBriefObj.setHeap(userBrief.getString("heap"));
                                                userBriefObj.setIs_friend(false);
                                                if (userBriefObj.getId() != ChatApplication.getUserId() &&
                                                        FriendSQL.getFriendByYourId(userBriefObj.getId()) == null) {
                                                    userBriefs.add(userBriefObj);
                                                }
                                            }

                                            LogUtil.log(userBriefs.toString());

                                            addFriendAdapter = new AddFriendAdapter(context, userBriefs);
                                            listView.setAdapter(addFriendAdapter);

                                            if (userBriefs.size() >= 0) {
                                                SoftkeyboardUtil.hideSoftKeyboard(AddFriendActivity.this, number);
                                            }
                                        }
                                    } catch (Exception e) {
                                        LogUtil.elog("添加好友解析出错");
                                    }
                                }
                            };
                        }
                    });
                } else {
                    friends = FriendSQL.getFriendsOfInviteMe();
                    inviteAdapter = new InviteFriendAdapter(context, friends);
                    listView.setAdapter(inviteAdapter);
                    SoftkeyboardUtil.hideSoftKeyboard(AddFriendActivity.this, number);
                }
            }
        });
    }

    private void initViewIds() {
        number = (EditText) findViewById(R.id.number);
        search = (Button) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.listview);
    }
}
