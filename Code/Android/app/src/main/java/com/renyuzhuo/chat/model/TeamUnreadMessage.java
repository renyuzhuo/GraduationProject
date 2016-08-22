package com.renyuzhuo.chat.model;

/**
 * Created by RENYUZHUO on 2016/4/30.
 */
public class TeamUnreadMessage {
    String token;
    int teamid;
    int messageid;
    int myid;

    public TeamUnreadMessage(){

    }

    public TeamUnreadMessage(String token, int teamid, int messageid, int myid){
        this.token = token;
        this.teamid = teamid;
        this.messageid = messageid;
        this.myid = myid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }

    public int getMessageid() {
        return messageid;
    }

    public void setMessageid(int messageid) {
        this.messageid = messageid;
    }

    public int getMyid() {
        return myid;
    }

    public void setMyid(int myid) {
        this.myid = myid;
    }
}
