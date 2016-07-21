package com.renyuzhuo.chat.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.adapter.TeamDetailAdapter;
import com.renyuzhuo.chat.detail.team.TeamInformationActivity;
import com.renyuzhuo.chat.layout.RecordButton;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.model.TeamMessage;
import com.renyuzhuo.chat.sql.TeamMessageSQL;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.ToastUtil;
import com.renyuzhuo.chat.util.picture.TeamMessagePictureUtil;
import com.renyuzhuo.chat.util.recored.TeamRecoreUtil;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TeamDetailActivity extends TeamBaseDetailActivity {

    ActionBar actionBar;

    public TeamDetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        this.context = this;
        keepOneDetailActivity(this);

        page = 0;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViewIds();
        initOnClickListener();

        teamMessages = TeamMessageSQL.getDetailMessages(getToId(), page);
        teamDetailAdapter = new TeamDetailAdapter(this, teamMessages);
        listView.setAdapter(teamDetailAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToBottom();
            }
        }, 100);
    }

    @Override
    public void initViewIds() {
        super.initViewIds();
        if (!haveChatTo()) {
            try {
                Thread.sleep(1000);
                ToastUtil.showToast(context, getResources().getString(R.string.err));
                finish();
                LogUtil.elog("聊天详情界面出错，isChatToErr()==true");
            } catch (InterruptedException e) {
                LogUtil.elog("聊天详情界面Exception");
            }
        }

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getToName());
        }
    }

    @Override
    public void initOnClickListener() {
        super.initOnClickListener();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString();
                if (TextUtils.isEmpty(str)) {
                    editText.setText("");
                    editText.setHint(getResources().getString(R.string.please_input));
                    return;
                }
                TeamMessage teamMessage = new TeamMessage();
                teamMessage.setId(--teamMessageId);
                teamMessage.setFromuser(ChatApplication.getUserId());
                teamMessage.setToteam(getToId());
                teamMessage.setMessage(str);
                teamMessage.setType(1);
                teamMessage.setPath("");
                teamMessage.setToken(ChatApplication.getToken());
                teamMessage.setTime(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                TeamMessageSQL.insertIntoMessage(teamMessage);
                ChatService.sendMessage(teamMessage);
                teamDetailAdapter.setData(teamMessage);
                teamDetailAdapter.notifyDataSetChanged();
                editText.setText("");
                editText.setHint(getResources().getString(R.string.say_something));
                scrollToBottom();
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        name = sdf.format(new Date()) + ".amr";
        path = Global.VIDEO_PATH + "/" + name;
        mRecordButton.setSavePath(path);
        mRecordButton.setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath) {
                LogUtil.log("保存录音成功:" + audioPath);
                new TeamRecoreUtil(TeamDetailActivity.this, getToId(), teamDetailAdapter).saveRecord(audioPath, name);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                name = sdf.format(new Date()) + ".amr";
                path = Global.VIDEO_PATH + "/" + name;
                mRecordButton.setSavePath(path);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getToName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                finish();
                Anim.out(context);
                break;
            }
            case R.id.action_detail: {
                TeamInformationActivity.startActivity(context, team);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void scrollToBottom() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (teamMessages != null) {
                    listView.smoothScrollToPosition(teamMessages.size() + 1);
                }
            }
        });
    }

    @Override
    public void refresh() {
        LogUtil.log("onRefresh");
        page++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    void newMessage(TeamMessage message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new TeamMessagePictureUtil(TeamDetailActivity.this, getToId(), "", teamDetailAdapter).onActivityResult(context, requestCode, resultCode, data);
    }

    public static void startTeamDetailActivity(Context context, Team team) {
        TeamBaseDetailActivity.team = team;
        Intent intent = new Intent(context, TeamDetailActivity.class);
        context.startActivity(intent);
        Anim.in(context);
    }
}
