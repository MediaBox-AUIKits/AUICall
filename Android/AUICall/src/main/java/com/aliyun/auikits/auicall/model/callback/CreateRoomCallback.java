package com.aliyun.auikits.auicall.model.callback;

public interface CreateRoomCallback {
    void onError(int i, String str);

    void onSuccess(String str);
}
