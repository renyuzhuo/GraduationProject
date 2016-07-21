package com.renyuzhuo.chat.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TeamSQL {

    public static String tablename;

    public static void refreshChatTeam(List<Team> teams) {
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        sqLiteDatabase.execSQL("delete from " + tablename + " where type<>0");

        for (int i = 0; i < teams.size(); i++) {
            TeamSQL.insertIntoTeam(teams.get(i));
        }
    }

    private static void insertIntoTeam(Team teamObj) {
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        if (getTeamById(teamObj.getTeamid()) == null) {
            LogUtil.log("新增群组");
            ContentValues values = new ContentValues();
            values.put("teamid", teamObj.getTeamid());
            values.put("teamname", teamObj.getTeamname());
            values.put("roomid", teamObj.getRoomid());
            values.put("user", teamObj.getUser());
            values.put("heap", teamObj.getHeap());
            values.put("type", teamObj.getType());

            sqLiteDatabase.insert(tablename, null, values);
        } else {
            LogUtil.log("更新群组");
            String sql = "update " + tablename + " set teamname=?, roomid=?, user=?, heap=?, type=? where teamid=?";
            sqLiteDatabase.execSQL(sql, new Object[]{teamObj.getTeamname(), teamObj.getRoomid(),
                    teamObj.getUser(), teamObj.getHeap(), teamObj.getType(), teamObj.getTeamid()});
        }
    }

    public static Team getTeamById(int id) {
        Team team = null;
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + tablename + " where teamid=?", new String[]{String.valueOf(id)});

        while (cursor.moveToNext()) {
            team = new Team();
            team.setTeamid(cursor.getInt(cursor.getColumnIndex("teamid")));
            team.setTeamname(cursor.getString(cursor.getColumnIndex("teamname")));
            team.setRoomid(cursor.getInt(cursor.getColumnIndex("roomid")));
            team.setUser(cursor.getInt(cursor.getColumnIndex("user")));
            team.setHeap(cursor.getString(cursor.getColumnIndex("heap")));
            team.setType(cursor.getInt(cursor.getColumnIndex("type")));
        }
        cursor.close();
        return team;
    }

    public static List<Team> getAllTeam() {
        List<Team> teams = new ArrayList<>();
        Team team;
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + tablename, null);

        while (cursor.moveToNext()) {
            team = new Team();
            team.setTeamid(cursor.getInt(cursor.getColumnIndex("teamid")));
            team.setTeamname(cursor.getString(cursor.getColumnIndex("teamname")));
            team.setRoomid(cursor.getInt(cursor.getColumnIndex("roomid")));
            team.setUser(cursor.getInt(cursor.getColumnIndex("user")));
            team.setHeap(cursor.getString(cursor.getColumnIndex("heap")));
            team.setType(cursor.getInt(cursor.getColumnIndex("type")));
            teams.add(team);
        }
        cursor.close();
        return teams;
    }

    public static void refreshDefaultTeams(List<Team> teams) {
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        sqLiteDatabase.execSQL("delete from " + tablename + " where type=0");
        for (int i = 0; i < teams.size(); i++) {
            TeamSQL.insertIntoTeam(teams.get(i));
        }
    }
}
