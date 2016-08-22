package com.renyuzhuo.chat.model;

/**
 * 返回信息实体
 * Created by RENYUZHUO on 2016/2/10.
 */
public class ResponseMessage {
    int id;

    String res;

    String username;

    String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean idOnly) {
        if (idOnly) {
            return String.valueOf(getId());
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("ResponseMessage[ ");
            sb.append("Id=").append(getId()).append(" ");
            sb.append("Res=").append(getRes()).append(" ");
            sb.append("Username=").append(getUsername()).append(" ");
            sb.append("Password=").append(getPassword()).append(" ");
            sb.append("]");
            return sb.toString();
        }
    }
}
