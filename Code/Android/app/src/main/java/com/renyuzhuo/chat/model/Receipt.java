package com.renyuzhuo.chat.model;

/**
 * Created by RENYUZHUO on 2016/3/13.
 */
public class Receipt {
    private int id;
    private String token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean idOnly) {
        StringBuilder sb = new StringBuilder();
        sb.append("Receipt[ ");
        sb.append("Id=").append(getId()).append(" ");
        sb.append("Token=").append(getToken()).append(" ");
        sb.append("]");
        return sb.toString();
    }
}
