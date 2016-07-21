package com.renyuzhuo.chat.model;

import com.renyuzhuo.chat.util.Global;

public class UserBrief {
    private int id;
    private String nickname;
    private boolean is_friend = false;
    private String message;
    private String time;
    private String heap;

    public UserBrief() {

    }

    public UserBrief(int id, String nickname, boolean is_friend, String message, String time, String heap) {
        this.id = id;
        this.nickname = nickname;
        this.is_friend = is_friend;
        this.message = message;
        this.time = time;
        this.heap = heap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        if (nickname != null && nickname.length() != 0) {
            return nickname;
        } else {
            return String.valueOf(getId());
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean is_friend() {
        return is_friend;
    }

    public void setIs_friend(boolean is_friend) {
        this.is_friend = is_friend;
    }

    public String getMessage() {
        if (message != null && message.contains(Global.pictureMessage)) {
            message = "图片消息";
        } else if (message != null && message.contains(".amr")) {
            message = "语音消息";
        }
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

    public String getHeap() {
        return heap;
    }

    public void setHeap(String heap) {
        this.heap = heap;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean idOnly) {
        if (idOnly) {
            return String.valueOf(getId());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("UserBrief[ ");
            sb.append("Id=").append(getId()).append(" ");
            sb.append("Nickname=").append(getNickname()).append(" ");
            sb.append("is_friend=").append(is_friend()).append(" ");
            sb.append("Message=").append(getMessage()).append(" ");
            sb.append("Time=").append(getTime()).append(" ");
            sb.append("]");
            return sb.toString();
        }
    }
}
