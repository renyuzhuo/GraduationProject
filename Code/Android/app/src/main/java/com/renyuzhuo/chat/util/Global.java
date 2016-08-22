package com.renyuzhuo.chat.util;

import android.os.Environment;

import java.io.File;

/**
 * 全局变量
 * Created by RENYUZHUO on 2016/2/13.
 */
public class Global {

    //    public static String BASIC_URL = "http://10.61.3.53:3002";
    public static String BASIC_URL = "http://server.renyuzhuo.cn:3002";

    public static final String VERSION = "Version 0.0.6";

    public static final String LOGIN_URL = BASIC_URL + "/login";
    public static final String CHAT_URL = BASIC_URL + "/chat";
    public static final String FRIENDS_URL = BASIC_URL + "/friends";
    public static final String FRIEND_URL = BASIC_URL + "/friend";
    public static final String CODE_URL = BASIC_URL + "/code";
    public static final String REGISTER_URL = BASIC_URL + "/register";
    public static final String IDENTIFYINGCODE = BASIC_URL + "/identifyingCode";
    public static final String TEAMS_URL = BASIC_URL + "/teams";
    public static final String DEFAULT_TEAMS_URL = BASIC_URL + "/defaultTeams";
    public static final String UPDATE_HEAP_URL = BASIC_URL + "/updateHeap";
    public static final String UPDATE_NICKNAME_URL = BASIC_URL + "/updateNickname";
    public static final String UPDATE_REMARK_URL = BASIC_URL + "/updateRemark";
    public static final String FEEDBACK_URL = BASIC_URL + "/feedback";
    public static final String GET_ALL_FEEDBACK_URL = BASIC_URL + "/getAllFeedback";
    public static final String USERINFO_URL = BASIC_URL + "/userinfo";
    public static final String ADD_FRIEND_URL = BASIC_URL + "/addFriend";
    public static final String VALIDATE_URL = BASIC_URL + "/validate";
    public static final String INVITE_URL = BASIC_URL + "/invite";
    public static final String SUBMIT_INVITE_FRIEND_URL = BASIC_URL + "/submitFriend";
    public static final String DELETE_FRIEND_URL = BASIC_URL + "/deleteFriend";
    public static final String UPDATE_TEAM_HEAP_URL = BASIC_URL + "/updateTeamHeap";
    public static final String UPDATE_TEAM_NAME_URL = BASIC_URL + "/updateTeamName";
    public static final String QUIT_TEAM_URL = BASIC_URL + "/quitTeam";
    public static final String ALL_TEAMS_URL = BASIC_URL + "/allTeams";
    public static final String ADD_TEAM_URL = BASIC_URL + "/addTeam";
    public static final String CREATE_TEAM_URL = BASIC_URL + "/createTeam";
    public static final String ADD_FRIEND_TO_TEAM = BASIC_URL + "/addFriendToTeam";

    /**
     * 配置文件名称
     */
    public static final String SETTING_FILE_NAME = "Chat.cxf";

    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "chat.db";

    public static final int DB_VERSION = 2;

    public static final String SMS_APP_KEY = "100308ebf3f80";

    public static final String SMS_APP_SECRET = "279a7820d37c82803be46437d7aff671";

    public static final File MYFILE_PATH = Environment.getExternalStorageDirectory();
    public static final String BASIC_PATH = MYFILE_PATH + "/chat";
    public static final String TEMP_PATH = MYFILE_PATH + "/chat/img";
    public static final String VIDEO_PATH = MYFILE_PATH + "/chat/video";
    public static final String LOG_PATH = MYFILE_PATH + "/chat/logs";
    public static final String ERROR_LOG_PATH = MYFILE_PATH + "/chat/logs/errorlog.txt";

    public static final String QINIU_PIC_BASIC_URL = "http://7xswvn.com1.z0.glb.clouddn.com/";
    public static final String QINIU_VIDEO_BASIC_URL = "http://o6gbyli25.bkt.clouddn.com/";
    public static final String QINIU_PIC_ERRLOG_URL = "http://7xtc7t.com1.z0.glb.clouddn.com/";
    public static final String BLOG_CHAT_URL = "http://blog.renyuzhuo.cn/2016/04/25/GraduationProject.html";

    public static String voicePassword = "password.renyuzhuo.cn";
    public static boolean ISDEBUG = true;
    public static String pictureMessage = "chat_picture";
}
