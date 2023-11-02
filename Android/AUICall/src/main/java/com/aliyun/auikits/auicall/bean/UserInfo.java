package com.aliyun.auikits.auicall.bean;

public final class UserInfo {
    private boolean cameraOn;
    private boolean isCalling;
    private boolean isHost;
    private boolean micOn;

    private String userId;

    public UserInfo( String id, boolean micOn, boolean cameraOn, boolean isHost) {
        this.userId = id;
        this.micOn = micOn;
        this.cameraOn = cameraOn;
        this.isHost = isHost;
    }

    public final String getUserId() {
        return this.userId;
    }

    public final void setUserId( String str) {
        this.userId = str;
    }

    public final boolean getMicOn() {
        return this.micOn;
    }

    public final void setMicOn(boolean z) {
        this.micOn = z;
    }

    public final boolean getCameraOn() {
        return this.cameraOn;
    }

    public final void setCameraOn(boolean z) {
        this.cameraOn = z;
    }

    public final boolean isHost() {
        return this.isHost;
    }

    public final void setHost(boolean z) {
        this.isHost = z;
    }

    public final boolean isCalling() {
        return this.isCalling;
    }

    public final void setCalling(boolean z) {
        this.isCalling = z;
    }

    public final void reset() {
        this.micOn = false;
        this.cameraOn = false;
        this.isHost = false;
    }

    public boolean equals( Object other) {
        return other != null && (other instanceof UserInfo) && this.userId != null && this.userId.equals(((UserInfo) other).getUserId());
    }
}
