package com.renyuzhuo.chat.setting;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateNicknameActivity extends AppCompatActivity {

    Context context;
    EditText usernameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);
        context = this;
        usernameET = (EditText) findViewById(R.id.username_et);
        usernameET.setText(ChatApplication.getNickname());
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
                if(!TextUtils.isEmpty(usernameET.getText())){
                    Map<String, String> map = new HashMap<>();
                    map.put("myId", String.valueOf(ChatApplication.getUserId()));
                    map.put("token", ChatApplication.getToken());
                    map.put("nickname", usernameET.getText().toString());
                    NetOption.postDataToUrl(Global.UPDATE_NICKNAME_URL, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("res").equals("success")) {
                                            ChatApplication.setNickname(usernameET.getText().toString());
                                            ChatApplication.saveUserInfo();
                                            ToastUtil.showToast(context, getResources().getString(R.string.update_success));
                                            finish();
                                            Anim.out(context);
                                        }
                                    }catch (Exception e){

                                    }
                                    return;
                                }
                            };
                        }
                    });
                } else {
                    usernameET.setHint(getResources().getString(R.string.unable_empty));
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
