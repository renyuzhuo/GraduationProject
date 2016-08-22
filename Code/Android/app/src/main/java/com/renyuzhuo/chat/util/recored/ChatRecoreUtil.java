package com.renyuzhuo.chat.util.recored;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.detail.BaseDetailActivity;
import com.renyuzhuo.chat.detail.ChatBaseDetailActivity;
import com.renyuzhuo.chat.detail.adapter.ChatDetailAdapter;
import com.renyuzhuo.chat.model.Message;
import com.renyuzhuo.chat.sql.MessageSQL;

import org.joda.time.DateTime;

/**
 * Created by RENYUZHUO on 2016/4/30.
 */
public class ChatRecoreUtil extends RecoreSaveUtil {

    BaseDetailActivity context;
    int toid;
    String messageStr;
    ChatDetailAdapter adapter;

    public ChatRecoreUtil(BaseDetailActivity context, int toid, ChatDetailAdapter adapter) {
        this.context = context;
        this.toid = toid;
        this.messageStr = messageStr;
        this.adapter = adapter;
    }

    @Override
    public void sendMessage(String url, String name) {
        Message message = new Message();
        message.setId(--ChatBaseDetailActivity.messageId);
        message.setFromuser(ChatApplication.getUserId());
        message.setTouser(toid);
        message.setMessage(name);
        message.setType(3);
        message.setPath(url);
        message.setToken(ChatApplication.getToken());
        message.setTime(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        message.setFromstate("sending");
        MessageSQL.insertIntoMessage(message);
        ChatService.sendMessage(message);
        adapter.setData(message);
        adapter.notifyDataSetChanged();
        context.scrollToBottom();
    }
}
