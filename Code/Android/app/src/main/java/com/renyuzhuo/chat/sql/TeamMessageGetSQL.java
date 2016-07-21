package com.renyuzhuo.chat.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.util.LogUtil;

/**
 * 群组列表获取
 * Created by RENYUZHUO on 2016/4/29.
 */
public class TeamMessageGetSQL {
    public static String tablename;

    /**
     * 获取某一群组最大已读消息id
     * @param team 群组
     * @return 最大已读Id，无此群组返回-1
     */
    public static int getMaxHaveReadIdByTeam(Team team) {
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + tablename + " where teamid=?",
                new String[]{String.valueOf(team.getTeamid())});

        if (cursor.moveToNext()) {
            return cursor.getInt(cursor.getColumnIndex("getid"));
        }else{
            insertNewUnread(team);
            return -1;
        }
    }

    /**
     * 插入新的群组未读消息记录
     * @param team 群组
     */
    private static void insertNewUnread(Team team) {
        LogUtil.log("insert into " + tablename + "", team.toString());
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("insert into " + tablename + "(teamid, getid) values(?,?)", new Object[]{team.getTeamid(), -1});
    }

    public static void updateNewUnread(int teamid, int getid){
        LogUtil.log("updateNewUnread");
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("update " + tablename + " set getid=? where teamid=?", new String[]{String.valueOf(getid), String.valueOf(teamid)});
    }

}
