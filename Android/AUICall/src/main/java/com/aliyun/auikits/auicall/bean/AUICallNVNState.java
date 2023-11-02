package com.aliyun.auikits.auicall.bean;

public enum AUICallNVNState {
    Idle("Idle"),
    Joining("Joining"),
    WaitAnswer("WaitAnswer"),
    Online("Online");
    
    private String desc;

    public final String getDesc() {
        return this.desc;
    }

    public final void setDesc(String str) {
        this.desc = str;
    }

    AUICallNVNState(String desc) {
        this.desc = desc;
    }
}
