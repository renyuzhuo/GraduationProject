package com.renyuzhuo.chat.model;

public class ACK {
    int messageId;
    String serverTime;
    int newMessageId;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public int getNewMessageId() {
        return newMessageId;
    }

    public void setNewMessageId(int newMessageId) {
        this.newMessageId = newMessageId;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean idOnly) {
        StringBuilder sb = new StringBuilder();
        sb.append("ACK[ ");
        sb.append("MessageId=").append(getMessageId()).append(" ");
        sb.append("NewMessageId=").append(getNewMessageId()).append(" ");
        sb.append("ServerTime=").append(getServerTime()).append(" ");
        sb.append("]");
        return sb.toString();
    }
}
