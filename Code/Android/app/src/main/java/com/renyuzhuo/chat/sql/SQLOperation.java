package com.renyuzhuo.chat.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.util.LogUtil;

/**
 * 聊天相关数据库表操作
 * Created by RENYUZHUO on 2016/2/15.
 */
public class SQLOperation extends SQLiteOpenHelper {

    Context context;
    String name;
    SQLiteDatabase.CursorFactory factory;
    int version;

    private static String sqlFeedBack;

    public SQLOperation(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        LogUtil.log("initSql");
        this.context = context;
        this.name = name;
        this.factory = factory;
        this.version = version;
        sqlFeedBack = "CREATE TABLE feedback" +
                " (" +
                "`id` integer primary key ," +
                "`userinfo_id` int(11) DEFAULT '-1'," +
                "`message` varchar(255) DEFAULT NULL," +
                "`time` datetime DEFAULT NULL," +
                "`nickname` varchar(255) DEFAULT NULL," +
                "`username` varchar(255) DEFAULT NULL," +
                "`heap` varchar(255) DEFAULT NULL," +
                "`type` varchar(255) DEFAULT NULL" +
                ")";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlFeedBack);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1: {
                LogUtil.log("Database update from version: " + oldVersion + " to version: " + newVersion);
                db.execSQL(sqlFeedBack);
                break;
            }
        }
    }

    /**
     * 初始化创建数据库语句
     */
    public static void initSql() {
        String id;
        if (ChatApplication.ISLOGIN) {
            id = String.valueOf(ChatApplication.getUserId());
        } else {
            id = "err";
        }

        String sqlMaster = "SELECT COUNT(*) FROM sqlite_master where type='table' and name=?";

        String sqlMessage = "CREATE TABLE message" + id +
                " (" +
                "`id` integer primary key autoincrement," +
                "`fromuser` int(11) DEFAULT '-1'," +
                "`touser` int(11) DEFAULT '-1'," +
                "`message` varchar(255) DEFAULT NULL," +
                "`time` datetime DEFAULT NULL," +
                "`type` int(11) DEFAULT '1'," +
                "`path` varchar(100) DEFAULT NULL," +
                "`fromstate` varchar(30) DEFAULT 'unread'," +
                "`tostate` varchar(30) DEFAULT 'unread'" +
                ")";
        String sqlFriends = "CREATE TABLE chat_friend" + id +
                "(" +
                "`myFriendListId` int(11) NOT NULL primary key," +
                "`yourId` int(11) DEFAULT NULL," +
                "`time` datetime DEFAULT NULL," +
                "`state` varchar(30) DEFAULT NULL," +
                "`remark` varchar(30) DEFAULT NULL," +
                "`yourUsername` varchar(11) DEFAULT NULL," +
                "`yourNickname` varchar(30) DEFAULT NULL," +
                "`heap` varchar(255) DEFAULT NULL," +
                "`yourType` int(11) DEFAULT NULL" +
                ")";
        String sqlMessageNumberToRead = "create table messagenumtoread" + id +
                "(" +
                "`id` integer primary key," +
                "`userid` integer DEFAULT -1," +
                "`num` int(11) DEFAULT '0'" +
                ")";
        String sqlUserBrief = "CREATE TABLE userbrief" + id +
                "(" +
                "" +
                "`id` integer primary key," +
                "`nickname` varchar(30) DEFAULT NULL," +
                "`portrait` varchar(255) DEFAULT NULL," +
                "`is_friend` varchar(10)," +
                "`message` varchar(255) DEFAULT NULL," +
                "`heap` varchar(255) DEFAULT NULL," +
                "`time` datetime DEFAULT NULL" +
                ")";

        String sqlTeam = "CREATE TABLE team" + id +
                "(" +
                "" +
                "`teamid` integer primary key," +
                "`teamname` varchar(30) DEFAULT NULL," +
                "`roomid` int(11) DEFAULT '-1'," +
                "`heap` varchar(255) DEFAULT NULL," +
                "`type` int(11) DEFAULT '1'," +
                "`user` int(11) DEFAULT '-1'" +
                ")";
        String sqlTeamMessage = "CREATE TABLE teammessage" + id +
                " (" +
                "`id` integer primary key autoincrement," +
                "`fromuser` int(11) DEFAULT '-1'," +
                "`toteam` int(11) DEFAULT '-1'," +
                "`message` varchar(255) DEFAULT NULL," +
                "`time` datetime DEFAULT NULL," +
                "`type` int(11) DEFAULT '1'," +
                "`path` varchar(100) DEFAULT NULL" +
                ")";
        String sqlTeamMessageGet = "CREATE TABLE teammessageget" + id +
                " (" +
                "`id` integer primary key autoincrement," +
                "`teamid` int(11) DEFAULT '-1'," +
                "`getid` int(11) DEFAULT '-1'" +
                ")";

        try {
            /**
             * 数据库表名初始化
             */
            FeedBackSQL.tablename = "feedback";
            FriendSQL.tablename = "chat_friend" + id;
            MessageSQL.tablename = "message" + id;
            MessageToReadSQL.tablename = "messagenumtoread" + id;
            UserBriefSQL.tablename = "userbrief" + id;
            TeamSQL.tablename = "team" + id;
            TeamMessageSQL.tablename = "teammessage" + id;
            TeamMessageGetSQL.tablename = "teammessageget" + id;

            /**
             * 数据库创建
             */
            SQLiteDatabase db = ChatApplication.getSqLiteDatabase();
            Cursor cursor = db.rawQuery(sqlMaster, new String[]{FriendSQL.tablename});
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    LogUtil.log("数据库已经存在，不重复创建");
                    MessageSQL.updateSendingToUnread();
                } else {
                    LogUtil.log("数据库不存在，创建数据库");
                    db.execSQL(sqlMessage);
                    db.execSQL(sqlFriends);
                    db.execSQL(sqlMessageNumberToRead);
                    db.execSQL(sqlUserBrief);
                    db.execSQL(sqlTeam);
                    db.execSQL(sqlTeamMessage);
                    db.execSQL(sqlTeamMessageGet);
                }
            }
            cursor.close();
        } catch (Exception e) {
            LogUtil.elog("数据库创建错误");
        }
    }

}
