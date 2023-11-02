package com.aliyun.auikits.auicall.bean;

public enum AUICall1V1State {
    Idle("Idle"),
    Calling("Calling"),
    WaitAnswer("WaitAnswer"),
    Accepted("Accepted"),
    Online("Online");
    
    private final String mName = name();

    AUICall1V1State(String name) {
    }

    public final String typename() {
        return this.mName;
    }
}
