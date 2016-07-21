package com.renyuzhuo.chat.util.recored;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.detail.BaseDetailActivity;
import com.renyuzhuo.chat.detail.ChatBaseDetailActivity;
import com.renyuzhuo.chat.detail.TeamBaseDetailActivity;
import com.renyuzhuo.chat.detail.adapter.TeamDetailAdapter;
import com.renyuzhuo.chat.model.TeamMessage;
import com.renyuzhuo.chat.sql.TeamMessageSQL;

import org.joda.time.DateTime;

/**
 * Created by RENYUZHUO on 2016/4/30.
 */
public class TeamRecoreUtil extends RecoreSaveUtil {

    BaseDetailActivity context;
    int toid;
    String messageStr;
    TeamDetailAdapter adapter;

    public TeamRecoreUtil(BaseDetailActivity context, int toid, TeamDetailAdapter adapter) {
        this.context = context;
        this.toid = toid;
        this.messageStr = messageStr;
        this.adapter = adapter;
    }

    @Override
    public void sendMessage(String url, String name) {
        TeamMessage teamMessage = new TeamMessage();
        teamMessage.setId(--TeamBaseDetailActivity.teamMessageId);
        teamMessage.setFromuser(ChatApplication.getUserId());
        teamMessage.setToteam(toid);
        teamMessage.setMessage(name);
        teamMessage.setType(3);
        teamMessage.setPath(url);
        teamMessage.setToken(ChatApplication.getToken());
        teamMessage.setTime(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        TeamMessageSQL.insertIntoMessage(teamMessage);
        ChatService.sendMessage(teamMessage);
        adapter.setData(teamMessage);
        adapter.notifyDataSetChanged();
        context.scrollToBottom();
    }
}
