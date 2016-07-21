package com.renyuzhuo.chat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.renyuzhuo.chat.detail.ChatDetailActivity;
import com.renyuzhuo.chat.model.ACK;
import com.renyuzhuo.chat.model.Message;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.model.TeamMessage;
import com.renyuzhuo.chat.model.UserInfo;
import com.renyuzhuo.chat.sql.MessageSQL;
import com.renyuzhuo.chat.sql.TeamMessageGetSQL;
import com.renyuzhuo.chat.sql.TeamMessageSQL;
import com.renyuzhuo.chat.sql.TeamSQL;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.JsonUtils;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.QiNiuUtil;
import com.renyuzhuo.chat.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Service
 * Created by RENYUZHUO on 2016/2/2.
 */
public class ChatService extends Service {

    Context context;

    public static Socket mSocket;

    static {
        try {
            mSocket = IO.socket(Global.CHAT_URL);
        } catch (URISyntaxException e) {
            LogUtil.elog("ChatService Exception");
        }
    }

    public ChatService() {
        context = this;
        mSocket.on(ChatApplication.getUserId() + "", message);
        mSocket.on("err_" + ChatApplication.getToken(), errLogout);
        mSocket.on("connect", connect);
        mSocket.on("ack_" + ChatApplication.getUserId(), ackPrivate);
        mSocket.on("history_" + ChatApplication.getUserId(), historyMessage);
        mSocket.on("teamMessage_" + ChatApplication.getUserId(), teamMessageUnread);
        mSocket.connect();
        mSocket.emit("online", ChatApplication.getToken());
        LogUtil.log("online");
    }

    @Override
    public void onCreate() {
        freshTeamListener();
        if (ChatApplication.ERR) {
            LogUtil.log(getResources().getString(R.string.restart_chat_main_activity));
            Intent intent = new Intent(context, ChatMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            ChatApplication.saveERR(false);
            ToastUtil.showToast(context, getResources().getString(R.string.restart_chat_main_activity));
            QiNiuUtil.putErrLogFile();
        }
    }

    public static void sendMessage(Message message) {
        Gson gson = new Gson();
        JSONObject obj = null;
        try {
            obj = new JSONObject(gson.toJson(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.log("send private message");
        ChatService.mSocket.emit("private", obj);
    }

    public static void sendMessage(TeamMessage teamMessage) {
        Gson gson = new Gson();
        JSONObject obj = null;
        try {
            obj = new JSONObject(gson.toJson(teamMessage));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.log("send private message");
        ChatService.mSocket.emit("team", obj);
    }

    Emitter.Listener message = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    String msg = args[0].toString();
                    Message message = JsonUtils.fromJson(msg, Message.class);
                    LogUtil.log("Message", message.toString());
                    MessageSQL.insertIntoMessage(message);
                }
            }.run();
        }
    };

    Emitter.Listener ackPrivate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    String msg = args[0].toString();
                    ACK ack = JsonUtils.fromJson(msg, ACK.class);
                    if (ack != null) {
                        LogUtil.log("ack:" + ack.toString());
                        MessageSQL.ackMessage(ack);

                    }
                }
            }.run();
        }
    };

    Emitter.Listener historyMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    LogUtil.log("in historyMessage");
                    String msg = args[0].toString();
                    List<Message> messages = JsonUtils.fromJson(msg, new TypeToken<List<Message>>() {
                    });
                    if (messages != null && messages.size() != 0) {
                        MessageSQL.insertIntoMessages(messages);
                    }
                }
            }.run();
        }
    };

    Emitter.Listener teamMessageUnread = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    LogUtil.log("in teamMessageUnread");
                    String msg = args[0].toString();
                    List<TeamMessage> teamMessages = JsonUtils.fromJson(msg, new TypeToken<List<TeamMessage>>(){});
                    if (teamMessages != null && teamMessages.size() != 0) {
                        TeamMessageSQL.insertIntoMessages(teamMessages);
                        TeamMessageGetSQL.updateNewUnread(teamMessages.get(0).getToteam(), teamMessages.get(0).getId());
                    }
                }
            }.run();
        }
    };

    Emitter.Listener errLogout = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    Log.i("ChatService", "Logout");
                    ChatMainActivity.logout();
                }
            }.run();
        }
    };

    Emitter.Listener connect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setId(ChatApplication.getUserId());
                    userInfo.setToken(ChatApplication.getToken());
                    mSocket.emit("history", JsonUtils.transFormObjectToJsonObject(userInfo));
                    ChatMainActivity.getAllTeamUnreadMessage();
                    LogUtil.log("reconnect");
                }
            }.run();
        }
    };

    static Emitter.Listener teamMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    String msg = args[0].toString();
                    LogUtil.log("收到群消息:" + msg);
                    TeamMessage teamMessage = JsonUtils.fromJson(msg, TeamMessage.class);
                    if (teamMessage != null) {
                        TeamMessageSQL.insertIntoMessage(teamMessage);
                        TeamMessageGetSQL.updateNewUnread(teamMessage.getToteam(), teamMessage.getId());
                    }
                }
            }.run();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void freshTeamListener() {
        LogUtil.log("ChatService.freshTeamListener()");
        offTeamListener();
        ChatApplication.teams = TeamSQL.getAllTeam();
        onTeamListener();
    }

    private static void offTeamListener() {
        LogUtil.log("ChatService.offTeamListener()");
        if (ChatApplication.teams != null) {
            for (Team team : ChatApplication.teams) {
                mSocket.off("team_" + team.getTeamid());
            }
        }
    }

    private static void onTeamListener() {
        LogUtil.log("ChatService.onTeamListener()");
        if (ChatApplication.teams != null) {
            for (Team team : ChatApplication.teams) {
                mSocket.on("team_" + team.getTeamid(), teamMessage);
            }
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.log("Service onDestroy");
        super.onDestroy();
    }

    public static void offSocket() {
        mSocket.off(ChatApplication.getUserId() + "");
        mSocket.off("err_" + ChatApplication.getToken());
        mSocket.off("connect");
        mSocket.off("ack_" + ChatApplication.getUserId());
        mSocket.off("history_" + ChatApplication.getUserId());
        mSocket.close();
    }
}
