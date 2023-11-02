package com.aliyun.auikits.auicall.bean;

public final class InitialInfo {

    private String appGroup;

    private String appId;

    private String deviceId;

    private String userId;

    public InitialInfo( String uid, String devid, String appid, String appgroup) {
        this.userId = uid;
        this.deviceId = devid;
        this.appId = appid;
        this.appGroup = appgroup;
    }

    public final String getUserId() {
        return this.userId;
    }

    public final void setUserId( String str) {
        this.userId = str;
    }

    public final String getDeviceId() {
        return this.deviceId;
    }

    public final void setDeviceId( String str) {
        this.deviceId = str;
    }


    public final String getAppId() {
        return this.appId;
    }

    public final void setAppId( String str) {
        this.appId = str;
    }

    public final String getAppGroup() {
        return this.appGroup;
    }

    public final void setAppGroup( String str) {
        this.appGroup = str;
    }
}
