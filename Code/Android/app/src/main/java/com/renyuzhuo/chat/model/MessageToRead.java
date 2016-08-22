package com.renyuzhuo.chat.model;

public class MessageToRead {
    int id;
    int userid;
    int num;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean idOnly) {
        StringBuilder sb = new StringBuilder();
        sb.append("MessageToRead[ ");
        sb.append("Id=").append(getId()).append(" ");
        sb.append("UserId=").append(getUserid()).append(" ");
        sb.append("Num=").append(getNum()).append(" ");
        sb.append("]");
        return sb.toString();
    }
}
