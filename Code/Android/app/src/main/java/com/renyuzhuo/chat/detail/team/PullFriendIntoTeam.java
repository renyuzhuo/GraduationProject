package com.renyuzhuo.chat.detail.team;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.team.adapter.PullFriendIntoTeamAdapter;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.List;

public class PullFriendIntoTeam extends AppCompatActivity {

    static Team team;
    static Context context;
    ListView listview;
    List<Friend> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_friend_into_team);
        context = this;
        LogUtil.log("team:" + team.toString());

        listview = (ListView) findViewById(R.id.listview);
        friends = FriendSQL.getFriends("friend");
        PullFriendIntoTeamAdapter pullFriendIntoTeamAdapter = new PullFriendIntoTeamAdapter(context, friends, team);
        listview.setAdapter(pullFriendIntoTeamAdapter);
    }

    public static void startPullFriendActivity(Context context, Team team) {
        PullFriendIntoTeam.team = team;
        Intent intent = new Intent(context, PullFriendIntoTeam.class);
        context.startActivity(intent);
        Anim.in(context);
    }
}
