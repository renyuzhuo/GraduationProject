package com.renyuzhuo.chat.detail.team;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatMainActivity;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateTeamNameActivity extends AppCompatActivity {

    Context context;
    private static Team team;
    EditText usernameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);
        context = this;
        usernameET = (EditText) findViewById(R.id.username_et);
        usernameET.setText(team.getTeamname());
    }

    public static void startUpdateNameActivity(Context context, Team team){
        UpdateTeamNameActivity.team = team;
        Intent intent = new Intent(context, UpdateTeamNameActivity.class);
        context.startActivity(intent);
        Anim.in(context);
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
                    map.put("name", usernameET.getText().toString());
                    map.put("teamId", String.valueOf(team.getTeamid()));
                    NetOption.postDataToUrl(Global.UPDATE_TEAM_NAME_URL, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getString("res").equals("success")){
                                            ChatMainActivity.getTeamList();
                                            team.setTeamname(usernameET.getText().toString());
                                            finish();
                                            Anim.out(context);
                                        }
                                    }catch (Exception e){
                                        LogUtil.log("修改群组名称解析返回值解析失败");
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
