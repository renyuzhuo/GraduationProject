package com.renyuzhuo.chat.detail;

import com.github.nkzawa.emitter.Emitter;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.detail.adapter.TeamDetailAdapter;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.model.TeamMessage;
import com.renyuzhuo.chat.sql.TeamMessageSQL;
import com.renyuzhuo.chat.util.JsonUtils;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.List;

public abstract class TeamBaseDetailActivity extends BaseDetailActivity {

    public static Team team;
    public static int teamMessageId;
    List<TeamMessage> teamMessages;
    TeamDetailAdapter teamDetailAdapter;

    TeamBaseDetailActivity() {
        teamMessageId = TeamMessageSQL.getMinMassageId();
        ChatService.mSocket.on("team_" + ChatApplication.getUserId(), teamMessage);
    }

    Emitter.Listener teamMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    String msg = args[0].toString();
                    final TeamMessage teamMessage = JsonUtils.fromJson(msg, TeamMessage.class);
                    LogUtil.log("teamMessage", teamMessage.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            newMessage(teamMessage);
                        }
                    });
                }
            }.run();
        }
    };

    int getToId() {
        if (haveChatTo()) {
            return team.getTeamid();
        } else {
            return -1;
        }
    }

    boolean haveChatTo() {
        if (team != null || team != null) {
            return true;
        } else {
            return false;
        }
    }

    String getToName() {
        if (haveChatTo()) {
            return team.getTeamname();
        } else {
            return "ChatTeam";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatService.mSocket.off("team_" + ChatApplication.getUserId(), teamMessage);
    }

    abstract void newMessage(final TeamMessage message);
}
