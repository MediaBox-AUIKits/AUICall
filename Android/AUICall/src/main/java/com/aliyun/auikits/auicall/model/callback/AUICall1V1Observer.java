package com.aliyun.auikits.auicall.model.callback;

public interface AUICall1V1Observer {
    void onAccept(String str);

    void onCall(String str);

    void onCameraOff(String str);

    void onCameraOn(String str);

    void onCancel();

    void onDebugInfo(String str);

    void onError(int i, String str);

    void onHangup();

    void onMicOff(String str);

    void onMicOn(String str);

    void onRefuse(String str);

    void onSilentRefuse();

    void onSwitchToAudioMode();
}
