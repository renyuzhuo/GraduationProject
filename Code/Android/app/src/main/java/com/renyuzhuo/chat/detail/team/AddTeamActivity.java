package com.renyuzhuo.chat.detail.team;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatMainActivity;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.friend.adapter.AddFriendAdapter;
import com.renyuzhuo.chat.detail.friend.adapter.InviteFriendAdapter;
import com.renyuzhuo.chat.detail.team.adapter.AddTeamAdapter;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.TeamSQL;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.SoftkeyboardUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTeamActivity extends AppCompatActivity {

    private Context context;
    EditText name;
    Button search;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        context = this;

        initViewIds();
        initOnClickListener();
    }

    private void initViewIds() {
        name = (EditText) findViewById(R.id.number);
        name.setHint(getResources().getString(R.string.please_input_team_name));
        search = (Button) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.listview);
    }

    private void initOnClickListener() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(name.getText().toString())) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", name.getText().toString());
                    NetOption.postDataToUrl(Global.ALL_TEAMS_URL, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("res").equals("success")) {
                                            JSONArray teams = response.getJSONArray("teams");
                                            List<Team> teamList = ChatMainActivity.translateTeamArrayToList(teams, true);
                                            List<Team> teamList1 = new ArrayList<>();
                                            for(int i = 0; i < teamList.size(); i ++){
                                                if(TeamSQL.getTeamById(teamList.get(i).getTeamid()) == null){
                                                    teamList1.add(teamList.get(i));
                                                }
                                            }
                                            AddTeamAdapter addTeamAdapter = new AddTeamAdapter(context, teamList1);
                                            listView.setAdapter(addTeamAdapter);
                                            LogUtil.log("获取所有群组");
                                        }
                                    } catch (Exception e) {
                                        LogUtil.elog("添加好友解析出错");
                                    }
                                }
                            };
                        }
                    });
                } else {
                    SoftkeyboardUtil.hideSoftKeyboard(AddTeamActivity.this, name);
                }
            }
        });
    }
}
