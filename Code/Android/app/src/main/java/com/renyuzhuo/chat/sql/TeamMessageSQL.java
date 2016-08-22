package com.renyuzhuo.chat.sql;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.model.TeamMessage;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TeamMessageSQL {
    public static String tablename;

    public static void insertIntoMessage(TeamMessage teamMessage) {
        try {
            LogUtil.log("insert into " + tablename + "", teamMessage.toString());
            SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();

            if (getTeamMessageById(teamMessage.getId()) == null) {
                String ack = teamMessage.getAckId();
                String[] ids = null;
                if (ack != null) {
                    ids = ack.split("_");
                }
                if (ack == null || ack.length() == 0) {
                    sqliteDatabase.execSQL("insert into " + tablename +
                                    "(id, fromuser, toteam, message, time, type, path) " +
                                    "values(?,?,?,?,?,?,?)",
                            new Object[]{teamMessage.getId(), teamMessage.getFromuser(),
                                    teamMessage.getToteam(), teamMessage.getMessage(),
                                    teamMessage.getTime(), teamMessage.getType(),
                                    teamMessage.getPath()});
                } else if (ids != null && ids.length >= 2) {
                    int userid = Integer.valueOf(ids[0]);
                    int messageid = Integer.valueOf(ids[1]);
                    if (userid == ChatApplication.getUserId()) {
                        ackMessage(teamMessage, messageid);
                    } else {
                        sqliteDatabase.execSQL("insert into " + tablename +
                                        "(id, fromuser, toteam, message, time, type, path) " +
                                        "values(?,?,?,?,?,?,?)",
                                new Object[]{teamMessage.getId(), teamMessage.getFromuser(),
                                        teamMessage.getToteam(), teamMessage.getMessage(),
                                        teamMessage.getTime(), teamMessage.getType(),
                                        teamMessage.getPath()});
                    }
                }
            }

        } catch (SQLException e) {
            LogUtil.elog("消息保存失败，可能因为是重复获取历史消息");
            return;
        }
    }

    private static Object getTeamMessageById(int id) {
        TeamMessage teamMessage = null;
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        String sql = "select * from " + tablename + " where id=?";
        Cursor result = sqliteDatabase.rawQuery(sql, new String[]{String.valueOf(id)});
        while (result.moveToNext()) {
            teamMessage = new TeamMessage();
            teamMessage.setId(result.getInt(result.getColumnIndex("id")));
            teamMessage.setFromuser(result.getInt(result.getColumnIndex("fromuser")));
            teamMessage.setToteam(result.getInt(result.getColumnIndex("toteam")));
            teamMessage.setMessage(result.getString(result.getColumnIndex("message")));
            teamMessage.setTime(result.getString(result.getColumnIndex("time")));
            teamMessage.setType(result.getInt(result.getColumnIndex("type")));
            teamMessage.setPath(result.getString(result.getColumnIndex("path")));
        }
        return teamMessage;
    }

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

    public static List<TeamMessage> getDetailMessages(int teamid, int page) {
        List<TeamMessage> teamMessages = new ArrayList<>();
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        int id;

        Cursor result = sqliteDatabase.rawQuery("select * from " + tablename + " where toteam=? order by time desc limit 0,?",
                new String[]{String.valueOf(teamid), String.valueOf((page + 1) * 15)});
        TeamMessage teamMessage;
        while (result.moveToNext()) {
            teamMessage = new TeamMessage();
            teamMessage.setId(result.getInt(result.getColumnIndex("id")));
            teamMessage.setFromuser(result.getInt(result.getColumnIndex("fromuser")));
            teamMessage.setToteam(result.getInt(result.getColumnIndex("toteam")));
            teamMessage.setPath(result.getString(result.getColumnIndex("path")));
            teamMessage.setMessage(result.getString(result.getColumnIndex("message")));
            teamMessage.setTime(result.getString(result.getColumnIndex("time")));
            teamMessage.setType(result.getInt(result.getColumnIndex("type")));
            Log.i("message", teamMessage.toString());
            teamMessages.add(teamMessage);
        }
        List<TeamMessage> messagesTemp = new ArrayList<>();
        for (int i = teamMessages.size() - 1; i >= 0; i--) {
            messagesTemp.add(teamMessages.get(i));
        }
        return messagesTemp;
    }

    public static void ackMessage(TeamMessage teamMessage, int oldid) {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        try {
            sqliteDatabase.execSQL("update " + tablename + " set id=?, time=? where id=?",
                    new Object[]{teamMessage.getId(), teamMessage.getTime(), oldid});
        } catch (Exception e) {
            LogUtil.elog("ackMessage error");
        }
    }

    public static void insertIntoMessages(List<TeamMessage> teamMessages) {
        for(int i = 0; i < teamMessages.size(); i ++){
            insertIntoMessage(teamMessages.get(i));
        }
    }
}
