package com.garow.logserver.generator.entity;

public class CommonLog {
    private String uid;

    private String deviceId;

    private Integer logType;

    private String msg;

    private Long time;

    private String app;

    private String chanId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    public Integer getLogType() {
        return logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg == null ? null : msg.trim();
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app == null ? null : app.trim();
    }

    public String getChanId() {
        return chanId;
    }

    public void setChanId(String chanId) {
        this.chanId = chanId == null ? null : chanId.trim();
    }
}