package com.renyuzhuo.chat.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.github.nkzawa.emitter.Emitter;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatMainActivity;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.TeamBaseDetailActivity;
import com.renyuzhuo.chat.detail.TeamDetailActivity;
import com.renyuzhuo.chat.fragment.Adapter.TwoAdapter;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.TeamSQL;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;

import org.json.JSONObject;

import java.net.ContentHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwoFragment extends Fragment {

    SwipeMenuListView listView;
    PullRefreshLayout pullRefreshLayout;
    List<Team> teams;
    Team team;
    private static TwoAdapter twoAdapter;

    public TwoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);

        initViewIds(rootView);

        teams = TeamSQL.getAllTeam();
        twoAdapter = new TwoAdapter(getActivity(), teams);
        listView.setAdapter(twoAdapter);

        initOnClickListener();
        onTeamListener();
        return rootView;
    }

    private void initViewIds(View rootView) {
        pullRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        listView = (SwipeMenuListView) rootView.findViewById(R.id.xlistview);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu swipeMenu, int index) {
                switch (index) {
                    case 0: {
                        team = teams.get(position);
                        Map<String, String> map = new HashMap<>();
                        map.put("token", ChatApplication.getToken());
                        map.put("myId", String.valueOf(ChatApplication.getUserId()));
                        map.put("teamId", String.valueOf(team.getTeamid()));
                        NetOption.postDataToUrl(Global.QUIT_TEAM_URL, map, new NetOptionResponse() {
                            @Override
                            public Response.Listener<JSONObject> success() {
                                return new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                };
                            }
                        });
                        break;
                    }
                }
                return false;
            }
        });
    }

    private void initOnClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.log("positon:" + position);
                team = teams.get(position);
                if (team == null) {
                    return;
                }
                LogUtil.log("Team:" + team.toString());
                TeamDetailActivity.startTeamDetailActivity(getActivity(), team);
            }
        });
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_CIRCLES);
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem openItem = new SwipeMenuItem(getContext());
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
            openItem.setWidth(150);
            openItem.setTitle(getResources().getString(R.string.quit));
            openItem.setTitleSize(18);
            openItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(openItem);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.log("TwoFragment onResume");
    }

    public void refresh() {
        LogUtil.log("onRefresh");
        ChatMainActivity.getTeamList();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                freshTeamMessageList();
                pullRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    Emitter.Listener teamMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            freshTeamMessageList();
                        }
                    });
                }
            }.run();
        }
    };

    private void freshTeamMessageList() {
        teams = TeamSQL.getAllTeam();
        twoAdapter.setTeams(teams);
    }

    private void onTeamListener() {
        LogUtil.log("ChatService.onTeamListener()");
        if (ChatApplication.teams != null) {
            for (Team team : ChatApplication.teams) {
                ChatService.mSocket.on("team_" + team.getTeamid(), teamMessage);
            }
        }
    }

}