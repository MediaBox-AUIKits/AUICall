package com.aliyun.auikits.auicall.model;

import android.content.Context;
import android.view.ViewGroup;

import com.aliyun.auikits.auicall.bean.AUICallNVNState;
import com.aliyun.auikits.auicall.model.callback.AUICallNVNObserver;
import com.aliyun.auikits.auicall.model.callback.CreateRoomCallback;
import com.aliyun.auikits.auicall.model.callback.TokenAccessor;
import com.aliyun.auikits.auicall.bean.InitialInfo;
import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.callback.InitCallback;

import java.util.Map;

public interface AUICallNVNModel {
    void accept( String str, boolean z);

    void cancelInvite( String str, boolean z);

    void create(String roomId, CreateRoomCallback meetingCreateRoomCallback);

    void disableBeauty();

    void dismiss();

    void enableBeauty();

    float getBeautySkinBlurLevel();

    float getBeautyWhiteLevel();


    Map<String, UserInfo> getCallingMembers();


    UserInfo getCurrentUser();


    UserInfo getHost();


    Map<String, UserInfo> getMeetingMembers();


    AUICallNVNState getState();

    void init(Context context, InitialInfo initialInfo, InitCallback callback);

    void invite( String str);

    boolean isHost();

    boolean isLoudspeakerOn();

    boolean isMuteAll();

    void join( String str, boolean z, boolean z2);

    void kickOut( String str);

    void leave();

    void muteAll();

    void openCamera( String str, boolean z);

    boolean openLoudspeaker(boolean z);

    void openMic( String str, boolean z);

    void refuse( String str, boolean z);

    void release();

    void setBeautySkinBlur(float f);

    void setBeautyWhite(float f);

    void setObserver( AUICallNVNObserver meetingCallObserver);

    void setTokenAccessor( TokenAccessor tokenAccessor);

    void setViewContainer( String str,  ViewGroup viewGroup, boolean z);

    void switchCamera();

    void toggleMirror();

    void unMuteAll();
}
