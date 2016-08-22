package com.renyuzhuo.chat.detail.team;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.TeamDetailActivity;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.TeamSQL;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Dialog;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.ToastUtil;
import com.renyuzhuo.chat.util.picture.TeamPictureUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TeamInformationActivity extends BaseTeamInformationActivity {

    Context context;
    static Team team;
    ImageView teamHeap;
    TextView teamname;
    Button sendMessage;
    Button addTeam;
    Button pullFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_information);
        context = this;
        keepOneFriendActivity(this);

        initViewsByIds();
        Picasso.with(context).load(team.getHeap()).error(R.drawable.ic_taiji_normal).placeholder(R.drawable.ic_taiji_normal).into(teamHeap);
        teamname.setText(team.getTeamname());
        if(TeamSQL.getTeamById(team.getTeamid()) == null){
            addTeam.setVisibility(View.VISIBLE);
            addTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", ChatApplication.getToken());
                    map.put("myId", String.valueOf(ChatApplication.getUserId()));
                    map.put("teamId", String.valueOf(team.getTeamid()));
                    NetOption.postDataToUrl(Global.ADD_TEAM_URL, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    LogUtil.log("添加成功");
                                    ToastUtil.showToast(context, getResources().getString(R.string.add_team_success));
                                }
                            };
                        }

                        @Override
                        public Response.ErrorListener error() {
                            return new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 401) {
                                        ToastUtil.showToast(context, getResources().getString(R.string.add_team_err));
                                    }
                                }
                            };
                        }
                    });
                }
            });
            sendMessage.setVisibility(View.GONE);
        }else {
            initOnClickListener();
            sendMessage.setVisibility(View.VISIBLE);
        }

    }

    private void initOnClickListener() {
        teamHeap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.setPhotoDialog(context);
            }
        });
        teamname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTeamNameActivity();
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamDetailActivity.startTeamDetailActivity(context, team);
            }
        });
        LogUtil.log("team:" + team.toString());
        if(team.getType() == 1){
            LogUtil.log("私有群");
            pullFriend.setVisibility(View.VISIBLE);
            pullFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PullFriendIntoTeam.startPullFriendActivity(context, team);
                }
            });
        }else{
            LogUtil.log("team.getType() == " + team.getType());
        }
    }

    private void updateTeamNameActivity() {
        UpdateTeamNameActivity.startUpdateNameActivity(context, team);
    }

    private void initViewsByIds() {
        teamHeap = (ImageView) findViewById(R.id.my_heap);
        teamname = (TextView) findViewById(R.id.teamname);
        sendMessage = (Button) findViewById(R.id.send_message);
        addTeam = (Button) findViewById(R.id.add_team);
        pullFriend = (Button) findViewById(R.id.pull_friend);
    }

    public static void startActivity(Context context, Team team) {
        Intent intent = new Intent(context, TeamInformationActivity.class);
        TeamInformationActivity.team = team;
        context.startActivity(intent);
        Anim.in(context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new TeamPictureUtil(teamHeap, team.getTeamid()).onActivityResult(TeamInformationActivity.this, requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Picasso.with(context).load(team.getHeap()).error(R.drawable.ic_taiji_normal).placeholder(R.drawable.ic_taiji_normal).into(teamHeap);
        teamname.setText(team.getTeamname());
    }
}
