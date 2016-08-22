package com.renyuzhuo.chat.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.model.Feedback;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RENYUZHUO on 2016/4/29.
 */
public class FeedBackSQL {

    public static String tablename;

    /**
     * 获取本地最大反馈ID
     *
     * @return 最大反馈ID
     */
    public static int getMaxId() {
        SQLiteDatabase sqLiteDatabase = ChatApplication.getSqLiteDatabase();
        String sql = "select max(id) id from " + tablename + "";
        Cursor result = sqLiteDatabase.rawQuery(sql, null);
        while (result.moveToNext()) {
            int maxFeedBackId = result.getInt(result.getColumnIndex("id"));
            LogUtil.log("get the max feedbackId: " + maxFeedBackId);
            return maxFeedBackId;
        }
        LogUtil.log("get the max feedbackId err and return -1");
        return -1;
    }

    /**
     * 获取所有反馈
     *
     * @return 所有反馈
     */
    public static List<Feedback> getAllFeedBacks() {
        List<Feedback> feedbacks;
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor result = sqliteDatabase.rawQuery("select * from " + tablename + " order by time desc", null);
        feedbacks = new ArrayList<>();
        Feedback feedback;
        while (result.moveToNext()) {
            feedback = new Feedback();
            feedback.setId(result.getInt(result.getColumnIndex("id")));
            feedback.setUserinfo_id(result.getInt(result.getColumnIndex("userinfo_id")));
            feedback.setMessage(result.getString(result.getColumnIndex("message")));
            feedback.setTime(result.getString(result.getColumnIndex("time")));
            feedback.setNickname(result.getString(result.getColumnIndex("nickname")));
            feedback.setUsername(result.getString(result.getColumnIndex("username")));
            feedback.setHeap(result.getString(result.getColumnIndex("heap")));
            feedback.setType(result.getInt(result.getColumnIndex("type")));
            feedbacks.add(feedback);
        }
        return feedbacks;
    }

    /**
     * 插入多条反馈信息
     *
     * @param feedbacks 多条反馈信息
     */
    public static void insertFeedbacks(List<Feedback> feedbacks) {
        if (feedbacks != null && feedbacks.size() != 0) {
            for (Feedback feedback : feedbacks) {
                if (getFeedbackById(feedback.getId()) == null) {
                    //反馈为空，插入
                    insertFeedback(feedback);
                } else {
                    //已经存在此反馈，修改
                    updateFeedback(feedback);
                }
            }
        }
    }

    /**
     * 插入反馈信息
     *
     * @param feedback 待插入反馈信息
     */
    private static void insertFeedback(Feedback feedback) {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        try {
            sqliteDatabase.execSQL("insert into " + tablename + "(id, userinfo_id, message, time, nickname, username, heap, type) values(?,?,?,?,?,?,?,?)",
                    new String[]{String.valueOf(feedback.getId()), String.valueOf(feedback.getUserinfo_id()),
                            feedback.getMessage(), feedback.getTime(), feedback.getNickname(), feedback.getUsername(),
                            feedback.getHeap(), String.valueOf(feedback.getType())});
        } catch (Exception e) {
            LogUtil.log("插入反馈失败");
            e.printStackTrace();
        }
    }

    /**
     * 更新反馈信息
     *
     * @param feedback 待修改反馈信息
     */
    private static void updateFeedback(Feedback feedback) {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        sqliteDatabase.execSQL("update " + tablename + " set userinfo_id=?, message=?, time=?, nickname=?, username=?, heap=?, type=? where id=?",
                new String[]{String.valueOf(feedback.getUserinfo_id()), feedback.getMessage(),
                        feedback.getTime(), feedback.getNickname(), feedback.getUsername(),
                        feedback.getHeap(), String.valueOf(feedback.getType()), String.valueOf(feedback.getId())});
    }

    /**
     * 通过反馈ID获取反馈信息
     *
     * @param feedbackId 反馈ID
     * @return 反馈信息
     */
    private static Feedback getFeedbackById(int feedbackId) {
        SQLiteDatabase sqliteDatabase = ChatApplication.getSqLiteDatabase();
        Cursor result = sqliteDatabase.rawQuery("select * from " + tablename + " where id=?", new String[]{String.valueOf(feedbackId)});
        Feedback feedback = null;
        while (result.moveToNext()) {
            feedback = new Feedback();
            feedback.setId(result.getInt(result.getColumnIndex("id")));
            feedback.setUserinfo_id(result.getInt(result.getColumnIndex("userinfo_id")));
            feedback.setMessage(result.getString(result.getColumnIndex("message")));
            feedback.setTime(result.getString(result.getColumnIndex("time")));
            feedback.setNickname(result.getString(result.getColumnIndex("nickname")));
            feedback.setUsername(result.getString(result.getColumnIndex("username")));
            feedback.setHeap(result.getString(result.getColumnIndex("heap")));
            feedback.setType(result.getInt(result.getColumnIndex("type")));
        }
        return feedback;
    }

}
