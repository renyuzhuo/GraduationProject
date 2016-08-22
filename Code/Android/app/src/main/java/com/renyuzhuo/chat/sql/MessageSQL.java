package com.renyuzhuo.chat.sql;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.model.ACK;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.Message;
import com.renyuzhuo.chat.model.Receipt;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.option.UserBriefOption;
import com.renyuzhuo.chat.util.DateUtils;
import com.renyuzhuo.chat.util.JsonUtils;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MessageSQL {

    public static String tablename;

    /**
     * 插入消息
     *
     * @param message 消息
     */
    public static void insertIntoMessage(Message message) {
        message.setTime(DateUtils.parseDateWithT(message.getTime()));
        try {
            LogUtil.log("insert into " + tablename + "", message.toString());
            SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();

            if (getMessageById(message.getId()) == null) {
                //本地没有此信息，插入用户信息
                sqliteDatabase.execSQL("insert into " + tablename + "(id, fromuser, touser, message, time, type, path, fromstate, tostate) values(?,?,?,?,?,?,?,?,?)",
                        new Object[]{message.getId(), message.getFromuser(), message.getTouser(), message.getMessage(),
                                message.getTime(), message.getType(), message.getPath(), message.getFromstate(), message.getTostate()});
                if (message.getTouser() == ChatApplication.getUserId() && message.getTostate() != null && message.getTostate().equals("unread")) {
                    //发送给我我未读过，未读消息列表加一
                    MessageToReadSQL.addOneUnreadToUser(message.getFromuser());
                }

                if (message.getTouser() == ChatApplication.getUserId()) {
                    //发给我的
                    if (UserBriefSQL.getUserBriefById(Integer.valueOf(message.getFromuser())) == null) {
                        //聊天列表没有此消息
                        Friend friend = FriendSQL.getFriendByYourId(message.getFromuser());
                        if (friend == null) {
                            //如果本地没有用户信息，获取用户信息
                            UserBriefOption.getUserBriefById(message.getFromuser(), message.getMessage(), message.getTime());
                        } else {
                            //本地有此用户信息，即此人与自己有关系(拉黑，申请等都算有关系)
                            UserBriefSQL.insertIntoUserBrief(
                                    new UserBrief(friend.getYourId(), friend.getRemark(), true, message.getMessage(), message.getTime(), friend.getHeap()),
                                    message.getMessage(), message.getTime());
                        }
                    } else {
                        //有聊天记录，修改时间
                        UserBriefSQL.updateDate(message.getFromuser(), message.getMessage(), message.getTime());
                    }
                } else {
                    //我发出的
                    if (message.getFromuser() == ChatApplication.getUserId()) {
                        //我发出的，对方未读用户设置为0
                        MessageToReadSQL.updateZeroToDb(message.getTouser());
                        if (UserBriefSQL.getUserBriefById(Integer.valueOf(message.getTouser())) == null) {
                            //没有对方聊天消息，即没有聊过天
                            Friend friend = FriendSQL.getFriendByYourId(message.getTouser());
                            if (friend == null) {
                                //如果没有此用户，获取用户信息
                                UserBriefOption.getUserBriefById(message.getTouser(), message.getMessage(), message.getTime());
                            } else {
                                //从联系人列表进入，本地有此人信息，通过本地个人消息初始化聊天对方消息
                                UserBriefSQL.insertIntoUserBrief(
                                        new UserBrief(friend.getYourId(), friend.getRemark(), true, message.getMessage(), message.getTime(), friend.getHeap()),
                                        message.getMessage(), message.getTime());
                            }

                        } else {
                            //以前聊过天，修改时间
                            UserBriefSQL.updateDate(message.getTouser(), message.getMessage(), message.getTime());
                        }
                    }
                }
            }

            //只要是发送给我我接受到的消息，都进行确认
            if (message.getTouser() == ChatApplication.getUserId()) {
                Receipt receipt = new Receipt();
                receipt.setId(message.getId());
                receipt.setToken(ChatApplication.getToken());
                ChatService.mSocket.emit("read", JsonUtils.transFormObjectToJsonObject(receipt));
            }

        } catch (SQLException e) {
            LogUtil.elog("ERR", "消息保存失败，可能因为是重复获取历史消息");
            return;
        }
    }

    /**
     * 通过消息ID获取消息
     *
     * @param messageId 消息ID
     * @return 消息
     */
    private static Message getMessageById(int messageId) {
        Message message = null;
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        String sql = "select * from " + tablename + " where id=?";
        Cursor result = sqliteDatabase.rawQuery(sql, new String[]{String.valueOf(messageId)});
        while (result.moveToNext()) {
            message = new Message();
            message.setId(result.getInt(result.getColumnIndex("id")));
            message.setFromuser(result.getInt(result.getColumnIndex("fromuser")));
            message.setTouser(result.getInt(result.getColumnIndex("touser")));
            message.setMessage(result.getString(result.getColumnIndex("message")));
            message.setTime(result.getString(result.getColumnIndex("time")));
            message.setType(result.getInt(result.getColumnIndex("type")));
            message.setPath(result.getString(result.getColumnIndex("path")));
            message.setFromstate(result.getString(result.getColumnIndex("fromstate")));
            message.setTostate(result.getString(result.getColumnIndex("tostate")));
        }
        return message;
    }

    /**
     * 获取某用户详细聊天记录
     *
     * @param userId 用户ID
     * @param page   消息页码
     * @return 消息列表
     */
    public static List<Message> getDetailMessages(int userId, int page) {
        List<Message> messages;
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor result = sqliteDatabase.rawQuery("select * from " + tablename + " where (fromuser=? or touser=?) order by time desc limit 0,?",
                new String[]{String.valueOf(userId), String.valueOf(userId), String.valueOf((page + 1) * 15)});
        messages = new ArrayList<>();
        Message message;
        while (result.moveToNext()) {
            message = new Message();
            message.setId(result.getInt(result.getColumnIndex("id")));
            message.setFromuser(result.getInt(result.getColumnIndex("fromuser")));
            message.setTouser(result.getInt(result.getColumnIndex("touser")));
            message.setFromstate(result.getString(result.getColumnIndex("fromstate")));
            message.setTostate(result.getString(result.getColumnIndex("tostate")));
            message.setPath(result.getString(result.getColumnIndex("path")));
            message.setType(result.getInt(result.getColumnIndex("type")));
            message.setMessage(result.getString(result.getColumnIndex("message")));
            message.setTime(result.getString(result.getColumnIndex("time")));
            LogUtil.log("time:" + message.getTime());
            messages.add(message);
        }
        List<Message> messagesTemp = new ArrayList<>();
        for (int i = messages.size() - 1; i >= 0; i--) {
            messagesTemp.add(messages.get(i));
        }
        LogUtil.log("消息列表显示" + messagesTemp.toString());
        return messagesTemp;
    }

    /**
     * 消息确认
     *
     * @param ack 确认
     */
    public static void ackMessage(ACK ack) {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        try {
            sqliteDatabase.execSQL("update " + tablename + " set id=?, fromstate=?, time=? where id=?",
                    new Object[]{ack.getNewMessageId(), "read", ack.getServerTime(), ack.getMessageId()});
        } catch (Exception e) {
            LogUtil.elog("ackMessage error");
            e.printStackTrace();
        }
    }

    /**
     * 获取最小消息ID
     *
     * @return 最小消息ID值
     */
    public static int getMinMassageId() {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        String sql = "select min(id)-1 id from " + tablename + "";
        Cursor result = sqliteDatabase.rawQuery(sql, null);
        while (result.moveToNext()) {
            int minMassageId = result.getInt(result.getColumnIndex("id"));
            LogUtil.log("get the min messageId:" + minMassageId);
            return minMassageId;
        }
        LogUtil.log("get the min messageId err and return -1");
        return -1;
    }

    /**
     * 插入消息
     *
     * @param messages 待插入消息
     */
    public static void insertIntoMessages(List<Message> messages) {
        if (messages != null && messages.size() != 0) {
            for (Message message : messages) {
                insertIntoMessage(message);
            }
        }
    }

    /**
     * 删除某人聊天消息
     *
     * @param userid 用户ID
     */
    public static void delMessageByOtherId(int userid) {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("delete from " + tablename + " where fromuser=? or touser=?", new String[]{String.valueOf(userid), String.valueOf(userid)});

    }

    /**
     * 修改所有发送中消息为发送失败
     */
    public static void updateSendingToUnread() {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        LogUtil.log("将所有正在发送消息标记为发送失败");
        sqliteDatabase.execSQL("update " + tablename + " set fromstate=? where fromstate=?", new String[]{"unread", "sending"});
    }
}
