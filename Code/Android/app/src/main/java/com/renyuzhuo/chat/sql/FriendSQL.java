package com.renyuzhuo.chat.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Friend数据库数据操作
 * Created by RENYUZHUO on 2016/2/15.
 */
public class FriendSQL {

    public static String tablename;

    public static List<Friend> getFriends(String state) {
        List<Friend> friends = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select " +
                "myFriendListId, yourId, time, state, remark, " +
                "yourUsername, yourNickname, yourType, heap " +
                "from " + tablename + " where state=?", new String[]{state});

        while (cursor.moveToNext()) {
            Friend friend = new Friend();
            friend.setMyFriendListId(cursor.getInt(cursor.getColumnIndex("myFriendListId")));
            friend.setYourId(cursor.getInt(cursor.getColumnIndex("yourId")));
            friend.setTime(cursor.getString(cursor.getColumnIndex("time")));
            friend.setState(cursor.getString(cursor.getColumnIndex("state")));
            friend.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
            friend.setYourUsername(cursor.getString(cursor.getColumnIndex("yourUsername")));
            friend.setYourNickname(cursor.getString(cursor.getColumnIndex("yourNickname")));
            friend.setYourType(cursor.getInt(cursor.getColumnIndex("yourType")));
            friend.setHeap(cursor.getString(cursor.getColumnIndex("heap")));
            friends.add(friend);
        }
        return friends;
    }

    public static void refreshChatFriend(JSONArray friends) {

        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        sqLiteDatabase.execSQL("delete from " + tablename + "");

        try {
            for (int i = 0; i < friends.length(); i++) {
                JSONObject friend = friends.getJSONObject(i);

                Friend friendObj = new Friend();

                friendObj.setMyFriendListId(friend.getInt("myFriendListId"));
                friendObj.setYourId(friend.getInt("yourId"));
                friendObj.setTime(friend.getString("time"));
                friendObj.setState(friend.getString("state"));
                friendObj.setRemark(friend.getString("remark"));
                friendObj.setYourUsername(friend.getString("yourUsername"));
                friendObj.setYourNickname(friend.getString("yourNickname"));
                friendObj.setYourType(friend.getInt("yourType"));
                friendObj.setHeap(friend.getString("heap"));
                FriendSQL.insertIntoFriend(friendObj);
            }
        } catch (Exception e) {
            Log.i("ERR::", "FriendSQL.refreshChatFriend");
            e.printStackTrace();
        }

    }

    public static void insertIntoFriend(Friend friend) {

        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        if (getFriendByFriendListId(friend.getMyFriendListId()) != null) {
            updateFriend(friend);
        } else {
            ContentValues values = new ContentValues();
            values.put("myFriendListId", friend.getMyFriendListId());
            values.put("yourId", friend.getYourId());
            values.put("time", friend.getTime());
            values.put("state", friend.getState());
            values.put("remark", friend.getRemark());
            values.put("yourUsername", friend.getYourUsername());
            values.put("yourNickname", friend.getYourNickname());
            values.put("yourType", friend.getYourType());
            values.put("heap", friend.getHeap());
            sqLiteDatabase.insert(tablename, null, values);
        }

        if (UserBriefSQL.getUserBriefById(friend.getYourId()) != null) {
            UserBriefSQL.updateUserInfoByFriend(friend);
        }
    }

    private static void updateFriend(Friend friend) {
        LogUtil.log("update updateFriend");
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("update " + tablename + " set time=?,state=?,remark=?,yourUsername=?," +
                        "yourNickname=?,heap=?,yourType=? where myFriendListId=?",
                new Object[]{friend.getTime(), friend.getState(), friend.getRemark(), friend.getYourUsername(),
                        friend.getYourNickname(), friend.getHeap(), friend.getYourType(), friend.getMyFriendListId()});
        return;
    }

    private static Friend getFriendByFriendListId(int myFriendListId) {
        Friend friend = null;
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select " +
                "myFriendListId, yourId, time, state, remark, " +
                "yourUsername, yourNickname, yourType, heap " +
                "from " + tablename + " where myFriendListId=?", new String[]{String.valueOf(myFriendListId)});

        while (cursor.moveToNext()) {
            friend = new Friend();
            friend.setMyFriendListId(cursor.getInt(cursor.getColumnIndex("myFriendListId")));
            friend.setYourId(cursor.getInt(cursor.getColumnIndex("yourId")));
            friend.setTime(cursor.getString(cursor.getColumnIndex("time")));
            friend.setState(cursor.getString(cursor.getColumnIndex("state")));
            friend.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
            friend.setYourUsername(cursor.getString(cursor.getColumnIndex("yourUsername")));
            friend.setYourNickname(cursor.getString(cursor.getColumnIndex("yourNickname")));
            friend.setYourType(cursor.getInt(cursor.getColumnIndex("yourType")));
            friend.setHeap(cursor.getString(cursor.getColumnIndex("heap")));
        }
        return friend;
    }

    public static Friend getFriendByYourId(int userId) {
        Friend friend = null;
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select " +
                "myFriendListId, yourId, time, state, remark, " +
                "yourUsername, yourNickname, yourType, heap " +
                "from " + tablename + " where yourId=? and state=\"friend\"", new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            friend = new Friend();
            friend.setMyFriendListId(cursor.getInt(cursor.getColumnIndex("myFriendListId")));
            friend.setYourId(cursor.getInt(cursor.getColumnIndex("yourId")));
            friend.setTime(cursor.getString(cursor.getColumnIndex("time")));
            friend.setState(cursor.getString(cursor.getColumnIndex("state")));
            friend.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
            friend.setYourUsername(cursor.getString(cursor.getColumnIndex("yourUsername")));
            friend.setYourNickname(cursor.getString(cursor.getColumnIndex("yourNickname")));
            friend.setYourType(cursor.getInt(cursor.getColumnIndex("yourType")));
            friend.setHeap(cursor.getString(cursor.getColumnIndex("heap")));
        }
        return friend;
    }

    public static List<Friend> getFriendsOfInviteMe() {
        List<Friend> friends = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select " +
                "myFriendListId, yourId, time, state, remark, " +
                "yourUsername, yourNickname, yourType, heap " +
                "from " + tablename + " where yourId=? and state=?", new String[]{String.valueOf(ChatApplication.getUserId()), "invite"});

        while (cursor.moveToNext()) {
            Friend friend = new Friend();
            friend.setMyFriendListId(cursor.getInt(cursor.getColumnIndex("myFriendListId")));
            friend.setYourId(cursor.getInt(cursor.getColumnIndex("yourId")));
            friend.setTime(cursor.getString(cursor.getColumnIndex("time")));
            friend.setState(cursor.getString(cursor.getColumnIndex("state")));
            friend.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
            friend.setYourUsername(cursor.getString(cursor.getColumnIndex("yourUsername")));
            friend.setYourNickname(cursor.getString(cursor.getColumnIndex("yourNickname")));
            friend.setYourType(cursor.getInt(cursor.getColumnIndex("yourType")));
            friend.setHeap(cursor.getString(cursor.getColumnIndex("heap")));
            friends.add(friend);
        }
        LogUtil.log(friends.toString());
        return friends;
    }

    public static void insertInviteFriendIntoChatFriend(JSONArray friends) {
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        String sql = "delete from " + tablename + " where yourId=" + ChatApplication.getUserId() + " and state='invite'";
        LogUtil.log(sql);
        sqLiteDatabase.execSQL(sql);
        try {
            for (int i = 0; i < friends.length(); i++) {
                JSONObject friend = friends.getJSONObject(i);

                Friend friendObj = new Friend();

                friendObj.setMyFriendListId(friend.getInt("myFriendListId"));
                friendObj.setYourId(friend.getInt("yourId"));
                friendObj.setTime(friend.getString("time"));
                friendObj.setState(friend.getString("state"));
                friendObj.setRemark(friend.getString("remark"));
                friendObj.setYourUsername(friend.getString("yourUsername"));
                friendObj.setYourNickname(friend.getString("yourNickname"));
                friendObj.setYourType(friend.getInt("yourType"));
                friendObj.setHeap(friend.getString("heap"));
                FriendSQL.insertIntoFriend(friendObj);
            }
        } catch (Exception e) {
            Log.i("ERR::", "FriendSQL.refreshChatFriend");
            e.printStackTrace();
        }
    }

    public static void addFriend(int flistid) {
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        String sql = "delete from " + tablename + " where myFriendListId=" + flistid;
        sqLiteDatabase.execSQL(sql);
    }
}
