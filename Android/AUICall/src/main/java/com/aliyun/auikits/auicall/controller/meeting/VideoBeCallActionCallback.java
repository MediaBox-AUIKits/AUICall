package com.aliyun.auikits.auicall.controller.meeting;

public interface VideoBeCallActionCallback {
    void onSwitchToAudio();

    void onVideoHangon();

    void onVideoHangup();
}
