package com.renyuzhuo.chat;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.sql.SQLOperation;
import com.renyuzhuo.chat.sql.TeamSQL;
import com.renyuzhuo.chat.util.CrashUtil;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.MyBase64;
import com.renyuzhuo.chat.util.PicCache;
import com.renyuzhuo.chat.util.QiNiuUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 全局Application
 * Created by RENYUZHUO on 2016/2/2.
 */
public class ChatApplication extends Application {

    public static boolean ISLOGIN = false;
    private static String username;
    private static String password;
    private static String token;
    private static String nickname;
    private static String heap;
    private static int userId;
    private static int type;

    private static final String SettingFileName = Global.SETTING_FILE_NAME;

    public static int screenWidth;
    public static int screenHeight;

    /**
     * 数据库操作句柄
     */
    private static SQLOperation sqlOperation;
    public static SQLiteDatabase sqLiteDatabase;

    private static SharedPreferences settings;

    private static Intent serviceIntent;
    private static ChatApplication context;
    public static List<Team> teams;

    public static boolean ERR = false;
    public static Map<Integer, Integer> messageToReads = null;
    public static int page = 0;

    public ChatApplication() {
        new LogUtil(ChatMainActivity.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        Thread.setDefaultUncaughtExceptionHandler(new CrashUtil(context));

        createPath(Global.BASIC_PATH);
        createPath(Global.TEMP_PATH);
        createPath(Global.LOG_PATH);
        createPath(Global.VIDEO_PATH);

        getUserinfo();
        ISLOGIN = isLogin();

        LogUtil.log("ISLOGIN: " + ISLOGIN);

        if (ISLOGIN) {
            LogUtil.log("ChatApplication.in if(ISLOGIN)");
            sqlOperation = new SQLOperation(this, Global.DATABASE_NAME, null, Global.DB_VERSION);
            sqLiteDatabase = sqlOperation.getWritableDatabase();
            sqlOperation.initSql();
            initTeams();
        }

        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);

        new NetOption(this);

        LogUtil.log("七牛token:" + QiNiuUtil.token);

        PicCache.loadImageCache(context);
    }

    private void initTeams() {
        teams = TeamSQL.getAllTeam();
    }

    public static void setISLOGIN(boolean ISLOGIN) {
        ChatApplication.ISLOGIN = ISLOGIN;
    }

    /**
     * 判断用户是够已经登陆
     *
     * @return 已经登陆:true,未登录:false
     */
    private static boolean isLogin() {
        if (token == null || token.equals("") || token.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取登陆用户的用户信息
     */
    public void getUserinfo() {
        settings = getSharedPreferences(SettingFileName, Context.MODE_PRIVATE);
        username = MyBase64.decode(settings.getString("username", ""));
        password = MyBase64.decode(settings.getString("password", ""));
        token = settings.getString("token", "");
        userId = settings.getInt("userId", -1);
        nickname = settings.getString("nickname", "");
        type = settings.getInt("type", -1);
        heap = settings.getString("heap", "null");
        ERR = settings.getBoolean("ERR", false);

        LogUtil.log("username:" + MyBase64.encode(username) + ", " +
                "token:" + token + ", userId:" + userId + ", " +
                "nickname:" + nickname + ", heap:" + heap + ", type:" + type);
    }

    public static void saveUserInfo() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", MyBase64.encode(username));
        editor.putString("password", MyBase64.encode(password));
        editor.putString("token", token);
        editor.putInt("userId", userId);
        editor.putString("nickname", nickname);
        editor.putString("heap", heap);
        editor.putInt("type", type);
        editor.putBoolean("ERR", ERR);
        editor.commit();
    }

    public static void saveUserInfo(String username, String password, String token, int userId, String nickname, String heap, int type) {
        setUsername(username);
        setPassword(password);
        setToken(token);
        setUserId(userId);
        setNickname(nickname);
        setHeap(heap);
        setType(type);
        saveUserInfo();
    }

    public static void saveHeap(String heap) {
        setHeap(heap);
        saveUserInfo();
    }

    public static void saveERR(boolean ERR) {
        ChatApplication.ERR = ERR;
        saveUserInfo();
    }

    public static void logout() {

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", "Chat用户");
        editor.putString("password", "");
        editor.putString("token", "");
        editor.putInt("userId", -1);
        editor.putString("nickname", "");
        editor.putString("heap", "");
        editor.putInt("type", -1);
        editor.commit();

        username = null;
        password = null;
        token = null;
        userId = -1;
        nickname = null;
        heap = null;
        type = -1;

        ISLOGIN = false;

        sqLiteDatabase = null;

        new Handler().postDelayed(new Runnable() {
            Context context = ChatApplication.context;

            @Override
            public void run() {
                if (context instanceof Activity) {
                    context.stopService(serviceIntent);
                }
            }
        }, 2000);
    }

    public static SQLiteDatabase getSqLiteDatabase() {
        if (sqLiteDatabase != null) {
            return sqLiteDatabase;
        } else {
            sqlOperation = new SQLOperation(context, Global.DATABASE_NAME, null, Global.DB_VERSION);
            sqLiteDatabase = sqlOperation.getWritableDatabase();
            sqlOperation.initSql();
            return sqLiteDatabase;
        }
    }

    public static void createPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
            LogUtil.log("createPath", path);
        }
    }

    public static void setUsername(String username) {
        ChatApplication.username = username;
    }

    public static String getUsername() {
        if (username == null || username.length() == 0) {
            return String.valueOf(getUserId());
        }
        return username;
    }

    public static void setPassword(String password) {
        ChatApplication.password = password;
    }

    public static String getPassword() {
        return password;
    }

    public static void setToken(String token) {
        ChatApplication.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static String getNickname() {
        if (nickname == null || nickname.length() == 0) {
            return getUsername();
        }
        return nickname;
    }

    public static void setNickname(String nickname) {
        ChatApplication.nickname = nickname;
    }

    public static String getHeap() {
        if (heap == null || heap.length() == 0) {
            return "errheap";
        } else {
            return heap;
        }
    }

    public static void setHeap(String heap) {
        ChatApplication.heap = heap;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        ChatApplication.userId = userId;
    }

    public static int getType() {
        return type;
    }

    public static void setType(int type) {
        ChatApplication.type = type;
    }

    public static void setServiceIntent(Intent serviceIntent) {
        ChatApplication.serviceIntent = serviceIntent;
    }
}
