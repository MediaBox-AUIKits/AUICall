package com.aliyun.auikits.auicall.model.callback;

import com.aliyun.auikits.auicall.bean.UserInfo;

public interface AUICallNVNObserver {
    void onAccept(String str);

    void onCameraOff(String str);

    void onCameraOn(String str);

    void onCancel();

    void onDebugInfo(String str);

    void onError(int i, String str);

    void onHostMute(boolean z);

    void onInvite(String str, String str2);

    void onJoin(String str);

    void onKickOut();

    void onLeave();

    void onMicOff(String str);

    void onMicOn(String str);

    void onRefuse(String str);

    void onRequestCamera(String str, boolean z);

    void onRequestMic(String str, boolean z);

    void onSilentCancel();

    void onSilentRefuse();

    void onUpdateHost(UserInfo userInfo);

    void onUserJoin(String str);

    void onUserLeave(String str);
}
