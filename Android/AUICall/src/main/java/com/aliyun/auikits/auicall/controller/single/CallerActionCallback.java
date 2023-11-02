package com.aliyun.auikits.auicall.controller.single;

public interface CallerActionCallback {
    void onCallerHangup();

    void onCallerMuteMic(boolean z);

    void onCallerOpenLoudspeaker(boolean z);
}
