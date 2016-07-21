package com.renyuzhuo.chat.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.model.MessageToRead;

import java.util.HashMap;
import java.util.Map;

public class MessageToReadSQL {

    public static String tablename;

    private static SQLiteDatabase sqliteDatabase;

    /**
     * 新增加某条未读消息
     *
     * @param userid 用户ID
     */
    public static void addOneUnreadToUser(int userid) {
        sqliteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor result = sqliteDatabase.rawQuery("select * from " + tablename + " where userid=?", new String[]{String.valueOf(userid)});
        if (result.moveToNext()) {
            Log.i("updateOneToDb", "updateOneToDb");
            updateOneToDb(userid);
        } else {
            Log.i("addOneToDb", "addOneToDb");
            addOneToDb(userid);
        }
        return;
    }

    /**
     * 新增一条某人未读消息记录
     *
     * @param userid 用户ID
     */
    private static void addOneToDb(int userid) {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("insert into " + tablename + "(userid, num) values(?, 1)", new String[]{String.valueOf(userid)});
        getMessageToRead();
        return;
    }

    /**
     * 某人未读消息加一
     *
     * @param userid 用户ID
     */
    private static void updateOneToDb(int userid) {
        sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("update " + tablename + " set num=num+1 where userid=?", new String[]{String.valueOf(userid)});
        getMessageToRead();
        return;
    }

    /**
     * 某人未读消息设置为0
     *
     * @param userid 用户ID
     */
    public static void updateZeroToDb(int userid) {
        sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("update " + tablename + " set num=0 where userid=?", new String[]{String.valueOf(userid)});
        getMessageToRead();
        return;
    }

    /**
     * 对未读消息列表记性刷新
     *
     * @return 未读消息列表
     */
    public static void getMessageToRead() {
        Map<Integer, Integer> messageToReads = new HashMap<>();
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor result = sqliteDatabase.rawQuery("select id, userid, num from " + tablename + " where num<>0", null);
        while (result.moveToNext()) {
            messageToReads.put(result.getInt(result.getColumnIndex("userid")), result.getInt(result.getColumnIndex("num")));
        }
        ChatApplication.messageToReads = messageToReads;
    }

}
