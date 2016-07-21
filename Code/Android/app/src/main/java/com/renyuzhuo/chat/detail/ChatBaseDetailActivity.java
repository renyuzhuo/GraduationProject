package com.renyuzhuo.chat.detail;

import com.github.nkzawa.emitter.Emitter;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.detail.adapter.ChatDetailAdapter;
import com.renyuzhuo.chat.model.ACK;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.Message;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.sql.MessageSQL;
import com.renyuzhuo.chat.util.JsonUtils;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.List;

public abstract class ChatBaseDetailActivity extends BaseDetailActivity {

    public static Friend chatFriend = null;
    public static UserBrief chatUserBrief = null;
    List<Message> messages;
    ChatDetailAdapter chatDetailAdapter;

    public static int messageId;

    ChatBaseDetailActivity() {
        messageId = MessageSQL.getMinMassageId();
        ChatService.mSocket.on(ChatApplication.getUserId() + "", message);
        ChatService.mSocket.on("ack_" + ChatApplication.getUserId(), ackPrivate);
    }

    Emitter.Listener message = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Runnable() {
                @Override
                public void run() {
                    String msg = args[0].toString();
                    final Message message = JsonUtils.fromJson(msg, Message.class);
                    LogUtil.log("Message", message.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            newMessage(message);
                        }
                    });
                }
            }.run();
        }
    };

    Emitter.Listener ackPrivate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = args[0].toString();
                    ACK ack = JsonUtils.fromJson(msg, ACK.class);
                    if (ack != null) {
                        chatDetailAdapter.fresh(ack);
                    }
                }
            });
        }
    };

    int getToId() {
        if (haveChatTo()) {
            if (chatFriend != null) {
                return chatFriend.getYourId();
            } else {
                return chatUserBrief.getId();
            }
        } else {
            return 0;
        }
    }

    boolean haveChatTo() {
        if (chatFriend != null || chatUserBrief != null) {
            return true;
        } else {
            return false;
        }
    }

    String getToName() {
        if (haveChatTo()) {
            if (chatFriend != null) {
                return chatFriend.getRemark();
            } else {
                return chatUserBrief.getNickname();
            }
        } else {
            return "Chat用户";
        }
    }

    String getHeap() {
        if (haveChatTo()) {
            if (chatFriend != null) {
                return chatFriend.getHeap();
            } else {
                return chatUserBrief.getHeap();
            }
        } else {
            return "errheap";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatService.mSocket.off(ChatApplication.getUserId() + "", message);
    }

    abstract void newMessage(final Message message);
}
