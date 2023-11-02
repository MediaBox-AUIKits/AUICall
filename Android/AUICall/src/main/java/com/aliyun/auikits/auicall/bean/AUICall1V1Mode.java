package com.aliyun.auikits.auicall.bean;

public enum AUICall1V1Mode {
    Video("video"),
    Audio("audio");
    
    private final String mName;

    AUICall1V1Mode(String name_) {
        this.mName = name_;
    }

    public final String typename() {
        return this.mName;
    }
}
