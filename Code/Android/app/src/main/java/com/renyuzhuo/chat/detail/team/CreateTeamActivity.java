package com.renyuzhuo.chat.detail.team;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateTeamActivity extends AppCompatActivity {

//    RadioButton type0;
//    RadioButton type1;
    EditText teamname;
    Button create;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        context = this;

        teamname = (EditText) findViewById(R.id.teamname);
//        type0 = (RadioButton) findViewById(R.id.type0);
//        type1 = (RadioButton) findViewById(R.id.type1);
        create = (Button) findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(teamname.getText() == null || teamname.getText().toString() == null || teamname.getText().toString().length() == 0){
                    ToastUtil.showToast(context, getResources().getString(R.string.not_null_teamname));
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("token", ChatApplication.getToken());
                map.put("myId", String.valueOf(ChatApplication.getUserId()));
                map.put("name", teamname.getText().toString());
                map.put("type", "1");
                NetOption.postDataToUrl(Global.CREATE_TEAM_URL, map, new NetOptionResponse() {
                    @Override
                    public Response.Listener<JSONObject> success() {
                        return new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("res").equals("success")) {
                                        finish();
                                        Anim.out(context);
                                    }
                                } catch (JSONException e) {
                                    LogUtil.elog("新建群组解析出错");
                                }
                            }
                        };
                    }
                });
            }
        });


    }
}
