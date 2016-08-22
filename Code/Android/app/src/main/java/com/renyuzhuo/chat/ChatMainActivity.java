package com.renyuzhuo.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.renyuzhuo.chat.detail.friend.AddFriendActivity;
import com.renyuzhuo.chat.detail.friend.FriendDetailActivity;
import com.renyuzhuo.chat.detail.team.AddTeamActivity;
import com.renyuzhuo.chat.detail.team.CreateTeamActivity;
import com.renyuzhuo.chat.fragment.OneFragment;
import com.renyuzhuo.chat.fragment.SectionsPagerAdapter;
import com.renyuzhuo.chat.fragment.ThreeFragment;
import com.renyuzhuo.chat.fragment.TwoFragment;
import com.renyuzhuo.chat.fragment.ZeroFragment;
import com.renyuzhuo.chat.main.FeedbackActivity;
import com.renyuzhuo.chat.main.LoginActivity;
import com.renyuzhuo.chat.model.Feedback;
import com.renyuzhuo.chat.model.Me;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.model.TeamUnreadMessage;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.net.MyErrorListener;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.scan.zxing.activity.CaptureActivity;
import com.renyuzhuo.chat.setting.MeActivity;
import com.renyuzhuo.chat.setting.about.AboutMainActivity;
import com.renyuzhuo.chat.share.Share;
import com.renyuzhuo.chat.sql.FeedBackSQL;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.sql.MessageToReadSQL;
import com.renyuzhuo.chat.sql.TeamMessageGetSQL;
import com.renyuzhuo.chat.sql.TeamSQL;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.JsonUtils;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.viewpager.MyViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import pl.droidsonroids.gif.GifImageView;

public class ChatMainActivity extends AppCompatActivity {

    static Context context;

    private MyViewPager mViewPager;

    List<Fragment> fragments;
    private TabLayout tabs;

    static Intent serviceIntent;

    GifImageView gifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        context = this;

        gifView = (GifImageView) findViewById(R.id.gif);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ChatApplication.screenWidth = displayMetrics.widthPixels;
        ChatApplication.screenHeight = displayMetrics.heightPixels;

        if (ChatApplication.ISLOGIN) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mViewPager = (MyViewPager) findViewById(R.id.container);
            mViewPager.setCanScrollTo(false);

            tabs = (TabLayout) findViewById(R.id.tabs);

            MessageToReadSQL.getMessageToRead();

            getFriendList();
            getTeamList();
            getFeedbackMessages();
            showMainUI();

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
            }, 200);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_me: {
                startMeActivity();
                break;
            }
            case R.id.logout: {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        logout();
                    }
                }, 100);
                break;
            }
            case R.id.share: {
                new Share(this);
                break;
            }
            case R.id.feedback: {
                feedback();
                break;
            }
            case R.id.scan: {
                scan();
                break;
            }
            case R.id.about: {
                about();
                break;
            }
            case R.id.add_friend: {
                addFriend();
                break;
            }
            case R.id.add_team: {
                addTeam();
                break;
            }
            case R.id.create_team: {
                createTeam();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void createTeam() {
        Intent intent = new Intent(context, CreateTeamActivity.class);
        startActivity(intent);
        Anim.in(context);
    }

    private void addFriend() {
        Intent intent = new Intent(context, AddFriendActivity.class);
        startActivity(intent);
        Anim.in(context);
    }

    private void addTeam() {
        Intent intent = new Intent(context, AddTeamActivity.class);
        startActivity(intent);
        Anim.in(context);
    }

    private void scan() {
        Intent openCameraIntent = new Intent(context, CaptureActivity.class);
        startActivityForResult(openCameraIntent, 0);
    }

    private void feedback() {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
        Anim.in(this);
    }

    private void about() {
        Intent intent = new Intent(this, AboutMainActivity.class);
        startActivity(intent);
        Anim.in(this);
    }

    private void startMeActivity() {
        Intent intent = new Intent(this, MeActivity.class);
        startActivity(intent);
        Anim.in(this);
    }

    public void getFriendList() {
        final Map<String, String> params = new HashMap<>();

        params.put("myId", String.valueOf(ChatApplication.getUserId()));
        params.put("token", ChatApplication.getToken());

        NetOption.postDataToUrl(Global.FRIENDS_URL, params, new NetOptionResponse() {
            @Override
            public Response.Listener<JSONObject> success() {
                return new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray friends = response.getJSONArray("friends");
                            FriendSQL.refreshChatFriend(friends);
                        } catch (JSONException e) {
                            LogUtil.elog("getFriends JSONException");
                        }
                    }
                };
            }

            @Override
            public Response.ErrorListener error() {
                return new MyErrorListener(context);
            }
        });

    }

    public static void getTeamList() {
        final Map<String, String> params = new HashMap<>();

        params.put("myId", String.valueOf(ChatApplication.getUserId()));
        params.put("token", ChatApplication.getToken());

        NetOption.postDataToUrl(Global.TEAMS_URL, params, new NetOptionResponse() {
            @Override
            public Response.Listener<JSONObject> success() {
                return new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray teams = response.getJSONArray("teams");
                            List<Team> teamList = translateTeamArrayToList(teams, false);
                            TeamSQL.refreshChatTeam(teamList);
                            getTeamUnreadMessage(teamList);
                        } catch (JSONException e) {
                            LogUtil.elog("getTeams JSONException");
                        }
                    }
                };
            }

            @Override
            public Response.ErrorListener error() {
                return new MyErrorListener(context);
            }
        });

        NetOption.postDataToUrl(Global.DEFAULT_TEAMS_URL, params, new NetOptionResponse() {
            @Override
            public Response.Listener<JSONObject> success() {
                return new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray teams = response.getJSONArray("defaultTeams");
                            List<Team> teamList = translateTeamArrayToList(teams, true);
                            TeamSQL.refreshDefaultTeams(teamList);
                            getTeamUnreadMessage(teamList);
                        } catch (JSONException e) {
                            LogUtil.elog("getTeams JSONException");
                        }
                    }
                };
            }

            @Override
            public Response.ErrorListener error() {
                return new MyErrorListener(context);
            }
        });
    }

    private static void getTeamUnreadMessage(List<Team> teamList) {
        for (int i = 0; i < teamList.size(); i++) {
            Team team = teamList.get(i);
            int id = TeamMessageGetSQL.getMaxHaveReadIdByTeam(team);
            TeamUnreadMessage teamUnreadMessage = new TeamUnreadMessage(ChatApplication.getToken(), team.getTeamid(), id, ChatApplication.getUserId());
            ChatService.mSocket.emit("teamUnreadMessage", JsonUtils.transFormObjectToJsonObject(teamUnreadMessage));
        }
    }

    public static void getAllTeamUnreadMessage() {
        List<Team> teamList = TeamSQL.getAllTeam();
        getTeamUnreadMessage(teamList);
    }

    /**
     * 将服务器获取到的群组解析为list
     *
     * @param teams 从服务器获取的teams
     * @return 解析结果
     */
    public static List<Team> translateTeamArrayToList(JSONArray teams, boolean isDefault) {
        List<Team> teamList = new ArrayList<>();
        try {
            for (int i = 0; i < teams.length(); i++) {
                JSONObject team = teams.getJSONObject(i);

                Team teamObj = new Team();

                teamObj.setTeamid(team.getInt("teamid"));
                teamObj.setTeamname(team.getString("name"));
                teamObj.setHeap(team.getString("heap"));
                teamObj.setType(team.getInt("type"));
                if (isDefault) {
                    teamObj.setRoomid(-1);
                    teamObj.setUser(ChatApplication.getUserId());
                } else {
                    teamObj.setRoomid(team.getInt("roomid"));
                    teamObj.setUser(team.getInt("user"));
                }
                teamList.add(teamObj);
            }
        } catch (Exception e) {
            LogUtil.elog("获取Teams解析错误");
            e.printStackTrace();
        }
        return teamList;
    }

    private void showMainUI() {
        LogUtil.log("showMainUI");
        fragments = new ArrayList<>();
        fragments.add(new ZeroFragment());
        fragments.add(new OneFragment());
        fragments.add(new TwoFragment());
        fragments.add(new ThreeFragment());

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabs.setupWithViewPager(mViewPager);

        serviceIntent = new Intent(ChatMainActivity.this, ChatService.class);
        startService(serviceIntent);
        ChatApplication.setServiceIntent(serviceIntent);

        gifView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    public static void logout() {
        offSocket();
        context.stopService(serviceIntent);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatApplication.logout();
                Intent intent = new Intent(context, LoginActivity.class);
                ((Activity) context).finish();
                context.startActivity(intent);
            }
        });
    }

    private static void offSocket() {
        ChatService.offSocket();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            LogUtil.log("扫描结果:" + scanResult);
            Me me = JsonUtils.fromJson(scanResult, Me.class);
            if (me != null) {
                UserBrief userBrief = new UserBrief();
                userBrief.setId(me.getUserId());
                userBrief.setNickname(me.getNickname());
                userBrief.setHeap(me.getHeap());
                FriendDetailActivity.startActivity(context, null, userBrief);
            }
        }
    }

    public static void getFeedbackMessages() {
        Map<String, String> map = new HashMap<>();
        map.put("maxid", String.valueOf(FeedBackSQL.getMaxId()));
        NetOption.postDataToUrl(Global.GET_ALL_FEEDBACK_URL, map, new NetOptionResponse() {
            @Override
            public Response.Listener<JSONObject> success() {
                return new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.get("res").equals("success")) {
                                JSONArray feedbacksObj = response.getJSONArray("feedbacks");
                                List<Feedback> feedbacks = new ArrayList<>();
                                for (int i = 0; i < feedbacksObj.length(); i++) {
                                    Feedback feedback = new Feedback();
                                    feedback.setId(feedbacksObj.getJSONObject(i).getInt("id"));
                                    feedback.setUserinfo_id(feedbacksObj.getJSONObject(i).getInt("userinfo_id"));
                                    feedback.setMessage(feedbacksObj.getJSONObject(i).getString("message"));
                                    feedback.setTime(feedbacksObj.getJSONObject(i).getString("time"));
                                    feedback.setNickname(feedbacksObj.getJSONObject(i).getString("nickname"));
                                    feedback.setUsername(feedbacksObj.getJSONObject(i).getString("username"));
                                    feedback.setHeap(feedbacksObj.getJSONObject(i).getString("heap"));
                                    feedback.setType(feedbacksObj.getJSONObject(i).getInt("type"));
                                    feedbacks.add(feedback);
                                }
                                FeedBackSQL.insertFeedbacks(feedbacks);
                            }
                        } catch (Exception e) {
                            LogUtil.log("获取反馈出错");
                        }
                    }
                };
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteVoiceFile();
    }

    private void deleteVoiceFile() {
        LogUtil.log("退出主程序，删除所有语音解压文件");
        File file = new File(Global.VIDEO_PATH);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.exists() && f.getPath().contains(".amr")) {
                        f.delete();
                    }
                }
            }
        }
    }
}
