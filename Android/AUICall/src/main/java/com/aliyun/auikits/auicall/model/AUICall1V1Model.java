package com.aliyun.auikits.auicall.model;

import android.content.Context;
import android.view.ViewGroup;

import com.aliyun.auikits.auicall.bean.AUICall1V1Mode;
import com.aliyun.auikits.auicall.bean.AUICall1V1State;
import com.aliyun.auikits.auicall.bean.CameraType;
import com.aliyun.auikits.auicall.model.callback.AUICall1V1Observer;
import com.aliyun.auikits.auicall.model.callback.TokenAccessor;
import com.aliyun.auikits.auicall.bean.InitialInfo;
import com.aliyun.auikits.auicall.model.callback.InitCallback;

public interface AUICall1V1Model {
    void accept(String str);

    void call( String str, AUICall1V1Mode callMode);

    String getCurrentUser();

    AUICall1V1Mode getMode();

    String getOppositeUser();

    AUICall1V1State getState();

    void hangup(boolean z);

    void init(Context context, InitialInfo initialInfo, InitCallback callback);

    boolean isLoudspeakerOn();

    void openCamera(boolean z);

    boolean openLoudspeaker(boolean z);

    void openMic(boolean z);

    void refuse( String str, boolean z);

    void release();

    void setCallObserver( AUICall1V1Observer callObserver);

    void setCameraType( CameraType cameraType);

    void setTokenAccessor( TokenAccessor tokenAccessor);

    void setViewContainer( ViewGroup viewGroup, ViewGroup viewGroup2);

    void swapPreviewView();

    void switchToAudioMode();
}
