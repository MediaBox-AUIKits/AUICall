package com.aliyun.auikits.auicall.controller.meeting;

public interface VideoOnCallActionCallback {
    void onVideoOnCallCameraMute(boolean z);

    void onVideoOnCallHangup();

    void onVideoOnCallMuteVoice(boolean z);

    void onVideoOnCallSpeakerMute(boolean z);

    void onVideoOnCallSwitchToAudio();
}
