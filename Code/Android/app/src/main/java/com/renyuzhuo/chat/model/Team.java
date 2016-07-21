package com.renyuzhuo.chat.model;

/**
 * Created by RENYUZHUO on 2016/4/10.
 */
public class Team {
    int teamid;
    String teamname;
    int roomid;
    int user;
    String heap;
    int type;

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }

    public String getTeamname() {
        if(teamname == null || teamname.length() == 0){
            return "Chat群组:" + getTeamid();
        }else {
            return teamname;
        }
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getHeap() {
        if(heap == null || heap.length() == 0){
            return "errheap";
        }else {
            return heap;
        }
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

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean idOnly) {
        if (idOnly) {
            return String.valueOf(getTeamid());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Team[ ");
            sb.append("Teamname=").append(getTeamname()).append(" ");
            sb.append("Roomid=").append(getRoomid()).append(" ");
            sb.append("User=").append(getUser()).append(" ");
            sb.append("Heap=").append(getHeap()).append(" ");
            sb.append("Type=").append(getType()).append(" ");
            sb.append("]");
            return sb.toString();
        }
    }
}