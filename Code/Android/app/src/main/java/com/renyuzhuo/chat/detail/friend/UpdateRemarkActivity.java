package com.renyuzhuo.chat.detail.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.JsonUtils;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateRemarkActivity extends AppCompatActivity {

    Context context;
    EditText usernameET;
    Intent intent;
    String remark;
    int chatFriendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);
        context = this;
        usernameET = (EditText) findViewById(R.id.username_et);

        intent = getIntent();
        remark = intent.getStringExtra("remark");
        chatFriendId = intent.getIntExtra("chatFriendId", -1);

        usernameET.setText(remark);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.finish: {
                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("myId", String.valueOf(ChatApplication.getUserId()));
                    map.put("token", ChatApplication.getToken());

                    map.put("chatFriendId", String.valueOf(chatFriendId));
                    if (!TextUtils.isEmpty(usernameET.getText())) {
                        map.put("remark", usernameET.getText().toString());
                    } else {
                        map.put("remark", "");
                    }
                    NetOption.postDataToUrl(Global.UPDATE_REMARK_URL, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("res").equals("success")) {
                                            JSONArray friends = response.getJSONArray("friends");
                                            Friend friend = JsonUtils.fromJson(friends.get(0).toString(), Friend.class);
                                            FriendSQL.insertIntoFriend(friend);
                                            intent.putExtra("remark", friend.getRemark());
                                            setResult(FriendDetailActivity.UPDATE_REMARK_SUCCESS_CODE, intent);
                                            LogUtil.log(friend.toString());
                                            ToastUtil.showToast(context, getResources().getString(R.string.update_success));
                                            finish();
                                            Anim.out(context);
                                        }
                                    } catch (Exception e) {

                                    }
                                    return;
                                }
                            };
                        }
                    });
                }catch (Exception e){
                    LogUtil.log("修改用户备注出错");
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
