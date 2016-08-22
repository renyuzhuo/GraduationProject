package com.renyuzhuo.chat.model;

public class Friend {

    private int myFriendListId;
    private int yourId;
    private String time;
    private String state;
    private String remark;
    private String yourUsername;
    private String yourNickname;
    private String heap;
    private int yourType;

    public int getMyFriendListId() {
        return myFriendListId;
    }

    public void setMyFriendListId(int myFriendListId) {
        this.myFriendListId = myFriendListId;
    }

    public int getYourId() {
        return yourId;
    }

    public void setYourId(int yourId) {
        this.yourId = yourId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        if (remark != null && remark.length() != 0) {
            return remark;
        } else {
            return getYourNickname();
        }
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getYourUsername() {
        if (yourUsername != null && yourUsername.length() != 0) {
            return yourUsername;
        } else {
            return String.valueOf(getYourId());
        }
    }

    public void setYourUsername(String yourUsername) {
        this.yourUsername = yourUsername;
    }

    public String getYourNickname() {
        if (yourNickname != null && yourNickname.length() != 0) {
            return yourNickname;
        } else {
            return getYourUsername();
        }
    }

    public void setYourNickname(String yourNickname) {
        this.yourNickname = yourNickname;
    }

    public int getYourType() {
        return yourType;
    }

    public void setYourType(int yourType) {
        this.yourType = yourType;
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
            return String.valueOf(getMyFriendListId());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Friend[ ");
            sb.append("MyFriendListId=").append(getMyFriendListId()).append(" ");
            sb.append("YourId=").append(getYourId()).append(" ");
            sb.append("Time=").append(getTime()).append(" ");
            sb.append("State=").append(getState()).append(" ");
            sb.append("Remark=").append(getRemark()).append(" ");
            sb.append("YourUsername=").append(getYourUsername()).append(" ");
            sb.append("YourNickname=").append(getYourNickname()).append(" ");
            sb.append("Heap=").append(getHeap()).append(" ");
            sb.append("YourType=").append(getYourType()).append(" ");
            sb.append("]");
            return sb.toString();
        }
    }
}
