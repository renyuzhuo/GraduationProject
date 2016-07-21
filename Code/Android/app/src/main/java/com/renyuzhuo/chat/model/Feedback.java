package com.renyuzhuo.chat.model;

/**
 * Created by RENYUZHUO on 2016/4/24.
 */
public class Feedback {
    int id;
    int userinfo_id;
    String message;
    String time;
    String nickname;
    String username;
    String heap;
    int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserinfo_id() {
        return userinfo_id;
    }

    public void setUserinfo_id(int userinfo_id) {
        this.userinfo_id = userinfo_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNickname() {
        if (nickname == null || nickname.length() == 0) {
            return getUsername();
        } else {
            return nickname;
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        if (username == null || username.equals("")) {
            return String.valueOf(id);
        } else {
            return username;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeap() {
        return heap;
    }

    public void setHeap(String heap) {
        this.heap = heap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
