package com.renyuzhuo.chat.model;

import com.google.gson.annotations.Expose;

/**
 * Created by RENYUZHUO on 2016/4/22.
 */
public class Me {
    @Expose
    private int userId;
    @Expose
    private String username;
    @Expose
    private String nickname;
    @Expose
    private String heap;

    public Me(int userId, String username, String nickname, String heap) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.heap = heap;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeap() {
        return heap;
    }

    public void setHeap(String heap) {
        this.heap = heap;
    }
}
