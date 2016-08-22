package com.renyuzhuo.chat.detail;

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
import com.renyuzhuo.chat.detail.adapter.ChatDetailAdapter;
import com.renyuzhuo.chat.detail.friend.FriendDetailActivity;
import com.renyuzhuo.chat.fragment.ZeroFragment;
import com.renyuzhuo.chat.layout.RecordButton;
import com.renyuzhuo.chat.model.Message;
import com.renyuzhuo.chat.sql.MessageSQL;
import com.renyuzhuo.chat.sql.MessageToReadSQL;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.ToastUtil;
import com.renyuzhuo.chat.util.picture.MessagePictureUtil;
import com.renyuzhuo.chat.util.recored.ChatRecoreUtil;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatDetailActivity extends ChatBaseDetailActivity {

    private int nowChatId;

    public ChatDetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        nowChatId = getToId();

        this.context = this;
        keepOneDetailActivity(this);
        page = 0;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViewIds();
        initOnClickListener();

        messages = MessageSQL.getDetailMessages(getToId(), page);

        chatDetailAdapter = new ChatDetailAdapter(this, messages, getHeap());
        listView.setAdapter(chatDetailAdapter);
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

        ActionBar actionBar = getSupportActionBar();
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
                Message message = new Message();
                message.setId(--messageId);
                message.setFromuser(ChatApplication.getUserId());
                message.setTouser(getToId());
                message.setMessage(str);
                message.setType(1);
                message.setPath("");
                message.setToken(ChatApplication.getToken());
                message.setTime(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                message.setFromstate("sending");
                MessageSQL.insertIntoMessage(message);
                ChatService.sendMessage(message);
                chatDetailAdapter.setData(message);
                chatDetailAdapter.notifyDataSetChanged();
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
                new ChatRecoreUtil(ChatDetailActivity.this, getToId(), chatDetailAdapter).saveRecord(audioPath, name);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                name = sdf.format(new Date()) + ".amr";
                path = Global.VIDEO_PATH + "/" + name;
                mRecordButton.setSavePath(path);
            }
        });
    }

    @Override
    void newMessage(Message message) {
        chatDetailAdapter.setData(message);
        scrollToBottom();
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
                FriendDetailActivity.startActivity(context, chatFriend, chatUserBrief);
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
                if (messages != null) {
                    listView.smoothScrollToPosition(messages.size() + 1);
                }
            }
        });
    }

    @Override
    public void refresh() {
        LogUtil.log("onRefresh");
        page++;
        int itemNumBegin = messages.size();
        messages = MessageSQL.getDetailMessages(getToId(), page);
        int itemNumEnd = messages.size();

        chatDetailAdapter.setDate(messages);
        chatDetailAdapter.notifyDataSetChanged();
        listView.setSelection(itemNumEnd - itemNumBegin);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageToReadSQL.updateZeroToDb(nowChatId);
        ZeroFragment.publicRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new MessagePictureUtil(ChatDetailActivity.this, getToId(), Global.pictureMessage, chatDetailAdapter).onActivityResult(context, requestCode, resultCode, data);
    }
}
