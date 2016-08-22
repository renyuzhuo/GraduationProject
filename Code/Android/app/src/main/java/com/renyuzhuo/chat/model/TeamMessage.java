package com.renyuzhuo.chat.model;

public class TeamMessage {
    private int id;
    private int fromuser;
    private int toteam;
    private String message;
    private String time;
    private int type;
    private String path;
    private String ackId;

    private String token;
    private String fromstate;

    boolean showOrNotTime;

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean idOnly) {
        if (idOnly) {
            return String.valueOf(getId());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Message[ ");
            sb.append("Id=").append(getId()).append(" ");
            sb.append("Fromuser=").append(getFromuser()).append(" ");
            sb.append("Touser=").append(getToteam()).append(" ");
            sb.append("Message=").append(getMessage()).append(" ");
            sb.append("Time=").append(getTime()).append(" ");
            sb.append("Type=").append(getType()).append(" ");
            sb.append("Path=").append(getPath()).append(" ");
            sb.append("AckId=").append(getAckId()).append(" ");
            sb.append("Token=").append(getToken()).append(" ");
            sb.append("]");
            return sb.toString();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromuser() {
        return fromuser;
    }

    public void setFromuser(int fromuser) {
        this.fromuser = fromuser;
    }

    public int getToteam() {
        return toteam;
    }

    public void setToteam(int toteam) {
        this.toteam = toteam;
    }

    public String getMessage() {
        while (message != null && message.length() != 0 && (message.contains("[chat1]") || message.contains("[chat2]"))) {
            message = message.replace("[chat1]", "\\");
            message = message.replace("[chat2]", "'");
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        if (path == null || path.length() == 0) {
            return "errpath";
        } else {
            return path;
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAckId() {
        return ackId;
    }

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFromstate() {
        return fromstate;
    }

    public void setFromstate(String fromstate) {
        this.fromstate = fromstate;
    }

    public boolean isShowOrNotTime() {
        return showOrNotTime;
    }

    public void setShowOrNotTime(boolean showOrNotTime) {
        this.showOrNotTime = showOrNotTime;
    }
}
