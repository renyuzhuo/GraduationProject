package com.renyuzhuo.chat.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class UserBriefSQL {

    public static String tablename;

    /**
     * 通过userBrief获取UserBrief
     *
     * @param userBriefId userBriefId
     * @return UserBrief
     */
    public static UserBrief getUserBriefById(int userBriefId) {
        LogUtil.log("getUserBriefById");
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor result = sqliteDatabase.rawQuery("select id, nickname, portrait, is_friend, heap from " + tablename + " where id=?", new String[]{String.valueOf(userBriefId)});
        UserBrief userBrief = null;
        while (result.moveToNext()) {
            userBrief = new UserBrief();
            userBrief.setId(result.getInt(result.getColumnIndex("id")));
            userBrief.setNickname(result.getString(result.getColumnIndex("nickname")));
            userBrief.setIs_friend(Boolean.valueOf(result.getString(result.getColumnIndex("is_friend"))));
            userBrief.setHeap(result.getString(result.getColumnIndex("heap")));
        }
        if (userBrief != null) {
            Log.i("userBrief", userBrief.toString());
        }
        return userBrief;
    }

    /**
     * 插入用户聊天信息
     *
     * @param userBrief 用户聊天信息
     * @param message   信息详情
     * @param time      时间
     */
    public static void insertIntoUserBrief(UserBrief userBrief, String message, String time) {
        if (userBrief == null) {
            LogUtil.log("userBrief == null and not insert");
            return;
        }
        LogUtil.log("insert into " + tablename + "", userBrief.toString());
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        if (getUserBriefById(userBrief.getId()) == null) {
            sqliteDatabase.execSQL("insert into " + tablename + "(id, nickname, is_friend, message, time, heap) " +
                            "values(?,?,?,?,?,?)",
                    new Object[]{userBrief.getId(), userBrief.getNickname(), userBrief.is_friend(), message, time, userBrief.getHeap()});
        } else {
            updateUserBrief(userBrief, message);
        }
        return;
    }

    /**
     * 修改用户聊天信息
     *
     * @param userBrief 用户信息
     * @param message   消息详情
     */
    private static void updateUserBrief(UserBrief userBrief, String message) {
        if (userBrief == null) {
            LogUtil.log("userBrief == null and not update");
            return;
        } else {
            LogUtil.log("update userBrief: " + userBrief.toString());
            SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
            sqliteDatabase.execSQL("update " + tablename + " set nickname=?, is_friend=?, message=?, heap=? where id=?",
                    new Object[]{userBrief.getNickname(), userBrief.is_friend(), message, userBrief.getId(), userBrief.getHeap()});
            return;
        }
    }

    /**
     * 获取所有聊天UserBrief
     *
     * @return Userbrief 用户聊天列表
     */
    public static List<UserBrief> getAllUserBrief() {
        List<UserBrief> userBriefs = new ArrayList<>();
        UserBrief userBrief;
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor result = sqliteDatabase.rawQuery("select * from " + tablename + " where id<>? order by time desc",
                new String[]{String.valueOf(ChatApplication.getUserId())});
        while (result.moveToNext()) {
            userBrief = new UserBrief(
                    result.getInt(result.getColumnIndex("id")),
                    result.getString(result.getColumnIndex("nickname")),
                    Boolean.valueOf(result.getString(result.getColumnIndex("is_friend"))),
                    result.getString(result.getColumnIndex("message")),
                    result.getString(result.getColumnIndex("time")),
                    result.getString(result.getColumnIndex("heap"))
            );
            userBriefs.add(userBrief);
        }
        LogUtil.log("userBrief: " + userBriefs.toString());
        return userBriefs;
    }

    /**
     * 修改聊天最后一条消息日期
     *
     * @param id      用户ID
     * @param message 消息内容
     * @param time    时间
     */
    public static void updateDate(int id, String message, String time) {
        LogUtil.log("update updateDate time");
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("update " + tablename + " set message=?, time=? where id=?", new Object[]{message, time, id});
        return;
    }

    /**
     * 通过Friend更新Userbrief
     *
     * @param friend 朋友
     */
    public static void updateUserInfoByFriend(Friend friend) {
        LogUtil.log("update updateUserInfo");
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("update " + tablename + " set nickname=?, heap=? where id=?", new Object[]{friend.getRemark(), friend.getHeap(), friend.getYourId()});
        return;
    }

    /**
     * 通过用户id删除某用户所有聊天消息
     *
     * @param userBriefId 用户ID
     */
    public static void delUserBriefMessageByUserBriefId(int userBriefId) {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("delete from " + tablename + " where id=?", new String[]{String.valueOf(userBriefId)});
    }
}

