package com.aliyun.auikits.auicall.model.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;
import com.aliyun.auikits.auicall.bean.InitialInfo;
import com.aliyun.auikits.auicall.bean.AUICall1V1Mode;
import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.bean.AUICall1V1State;
import com.aliyun.auikits.auicall.bean.CameraType;
import com.aliyun.auikits.auicall.model.callback.AUICall1V1Observer;
import com.aliyun.auikits.auicall.model.callback.InitCallback;
import com.aliyun.auikits.auicall.model.callback.RTCInfoCallback;
import com.aliyun.auikits.auicall.model.callback.TokenAccessor;
import com.aliyun.auikits.auicall.util.AUICallConfig;
import com.aliyun.auikits.room.AUIActionCallback;
import com.aliyun.auikits.room.AUICreateRoomCallback;
import com.aliyun.auikits.room.AppConfig;
import com.aliyun.auikits.room.AUIRoomEngine;
import com.aliyun.auikits.room.bean.AUIAudioOutputType;
import com.aliyun.auikits.room.bean.AUIRoomConfig;
import com.aliyun.auikits.room.bean.AUIRoomEngineCameraType;
import com.aliyun.auikits.room.bean.AUIRoomUserInfo;
import com.aliyun.auikits.room.callback.AUIRoomEngineObserver;
import com.aliyun.auikits.room.factory.AUIRoomEngineFactory;
import com.aliyun.auikits.room.util.AliyunLog;
import com.aliyun.common.AlivcBase;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

public final class AUICall1V1ModelImpl implements AUICall1V1Model, AUIRoomEngineObserver {

    private static final int SWITCH_TO_AUDIO_MODE = 10000;

    private static final String TAG = "CallModelImpl";

    private AUICall1V1Observer mCallObserver;

    private CountDownTask mCallingTask;

    private String mImToken;

    private ViewGroup mMainContainer;
    private boolean mMirror;

    private String mOppositeUserId;

    private AUIRoomEngine mRoomEngine;

    private String mRoomId;

    private ViewGroup mSmallContainer;

    private TokenAccessor mTokenAccessor;

    private AUICall1V1State mCallState = AUICall1V1State.Idle;

    private AUICall1V1Mode mCallMode = AUICall1V1Mode.Video;
    private boolean mIsSwapPreview = true;

    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    public enum Type {
        CALLER,
        BECALLER
    }

    public static final class CountDownTask implements Runnable {

        private final WeakReference<AUICall1V1Model> mRef;

        private final Type mType;

        private final String mUserId;

        public CountDownTask( String userId,  Type type, AUICall1V1Model model) {
            this.mType = type;
            this.mRef = new WeakReference<>(model);
            this.mUserId = userId;
        }

        @Override 
        public void run() {
            AUICall1V1Model model = this.mRef.get();
            if (model == null) {
                return;
            }
            if (this.mType == Type.CALLER) {
                model.hangup(true);
            } else if (this.mType == Type.BECALLER) {
                model.refuse(mUserId, true);
            }
        }
    }

    @Override
    public void init(Context context, InitialInfo initialInfo, final InitCallback callback) {
        if (mCallObserver != null) {
            mCallObserver.onDebugInfo("init " + initialInfo.getUserId());
        }
        AppConfig.setAppInfo(initialInfo.getAppId(), initialInfo.getAppGroup());
        TokenAccessor tokenAccessor = this.mTokenAccessor;
        this.mImToken = tokenAccessor == null ? null : tokenAccessor.getIMToken(initialInfo.getUserId());
        this.mCallState = AUICall1V1State.Idle;
        this.mRoomEngine = AUIRoomEngineFactory.createRoomEngine();
        AlivcBase.setIntegrationWay(AUICallConfig.AUI_CALL);
        AUIRoomUserInfo userInfo = new AUIRoomUserInfo(initialInfo.getUserId(), initialInfo.getDeviceId(), this.mImToken);
        mRoomEngine.login(context, userInfo, new AUIActionCallback() {
            @Override 
            public final void onResult(final int errCode, final String errMsg) {
                if (mCallObserver != null) {
                    mCallObserver.onDebugInfo("login result " + errCode + " : " + errMsg);
                }
                AliyunLog.d(TAG, "login " + errCode + ' ' + ((Object) errMsg));
                mUIHandler.post(new Runnable() { 
                    @Override 
                    public final void run() {
                        callback.onResult(errCode, errMsg);
                    }
                });
            }
        });
        mRoomEngine.addObserver(this);
    }

    @Override 
    public void setViewContainer( ViewGroup mainContainer, ViewGroup smallContainer) {
        this.mMainContainer = mainContainer;
        this.mSmallContainer = smallContainer;
    }

    @Override 
    public void swapPreviewView() {
        if (mRoomEngine!= null && mRoomEngine.isInRoom() && !TextUtils.isEmpty(this.mOppositeUserId)) {
            this.mIsSwapPreview = !this.mIsSwapPreview;
            preview();
        }
    }

    @Override 
    public void release() {
        if (mCallObserver != null && mRoomEngine != null) {
            mCallObserver.onDebugInfo("release " + mRoomEngine.getUserId());
        }
        stopPreview();
        if (mRoomEngine != null) {
            mRoomEngine.logout();
        }
        this.mCallObserver = null;
        this.mRoomEngine = null;
        resetState();
    }

    private final void resetState() {
        stopCountDownTask();
        this.mCallMode = AUICall1V1Mode.Video;
        this.mCallState = AUICall1V1State.Idle;
        this.mOppositeUserId = null;
        this.mIsSwapPreview = true;
        this.mRoomId = null;
    }

    public final void makeSureRTCInfo(final String roomId, final RTCInfoCallback callback) {
        new Thread(new Runnable() { 
            @Override 
            public final void run() {
                final String rtcToken;
                if (mTokenAccessor == null) {
                    callback.onRTCInfo(null, 0);
                } else {
                    String userId = mRoomEngine.getUserId();
                    rtcToken = mTokenAccessor.getRtcToken(userId, roomId);
                    final Long rtcTimestamp = mTokenAccessor.getRtcTimestamp();
                    mUIHandler.post(new Runnable() { 
                        @Override 
                        public final void run() {
                            callback.onRTCInfo(rtcToken, rtcTimestamp);
                        }
                    });
                }
            }
        }).start();
    }

    private final void startCountDownTask(String userId, Type type) {
        onDebugInfo("startCountDownTask");
        if (mCallingTask != null) {
            mUIHandler.removeCallbacks(mCallingTask);
        }
        mCallingTask = new CountDownTask(userId, type, this);;
        mUIHandler.postDelayed(mCallingTask, 60000L);
    }

    private final void stopCountDownTask() {
        if (mCallingTask != null) {
            onDebugInfo("stopCountDownTask");
            mUIHandler.removeCallbacks(mCallingTask);
            mCallingTask = null;
        }
    }

    private final void preview() {
        if(mRoomEngine == null) return;
        stopPreview();
        if (getMode() == AUICall1V1Mode.Video) {
            if (this.mCallState == AUICall1V1State.Online) {
                if (this.mIsSwapPreview) {
                    mRoomEngine.setRenderViewLayout(mRoomEngine.getUserId(), this.mSmallContainer, true, this.mMirror);
                    mRoomEngine.setRenderViewLayout(this.mOppositeUserId, this.mMainContainer, false, false);
                }else{
                    mRoomEngine.setRenderViewLayout(mRoomEngine.getUserId(), this.mMainContainer, false, this.mMirror);
                    mRoomEngine.setRenderViewLayout(this.mOppositeUserId, this.mSmallContainer, true, false);
                }
            }else{
                mRoomEngine.setRenderViewLayout(mRoomEngine.getUserId(), this.mMainContainer, false, this.mMirror);
                mRoomEngine.setRenderViewLayout(this.mOppositeUserId, this.mSmallContainer, true, false);
            }
        }
    }

    private final void stopPreview() {
        if(mRoomEngine == null) return;
        mRoomEngine.setRenderViewLayout(mRoomEngine.getUserId(), null, false, false);
        mRoomEngine.setRenderViewLayout(mOppositeUserId, null, false, false);
    }

    @Override 
    public void call( String targetUser, AUICall1V1Mode mode) {
        if(mRoomEngine == null) return;
        if (TextUtils.isEmpty(targetUser)) {
            if (mCallObserver == null) {
                return;
            }
            mCallObserver.onError(-1, "input uid empty");
            return;
        }
        this.mCallMode = mode;
        if (mCallObserver != null) {
            mCallObserver.onDebugInfo("call " + targetUser + " mode " + mode);
        }
        this.mCallState = AUICall1V1State.Calling;
        this.mOppositeUserId = targetUser;
        mRoomEngine.createRoom(new AUICreateRoomCallback() { 
            @Override 
            public void onSuccess( String roomId) {
                if(roomId == null || roomId.equals(mRoomId)){
                    return;
                }
                mRoomId = roomId;
                makeSureRTCInfo(roomId, new RTCInfoCallback() {
                    @Override
                    public void onRTCInfo(String token, long timestamp) {
                        if (mRoomEngine != null) {
                            AUIRoomConfig roomConfig = new AUIRoomConfig(mRoomId, AUICallConfig.GSLB, token, timestamp);
                            mRoomEngine.join(roomConfig, new AUIActionCallback() {
                                @Override 
                                public final void onResult(int errCode, String errMsg) {
                                    if (mCallObserver!= null) {
                                        mCallObserver.onDebugInfo("call result " + errCode + " : " +  errMsg);
                                    }
                                    AliyunLog.d("CallModelImpl", "call onResult " + errCode + " : " + errMsg);
                                }
                            });
                        }
                    }
                });
            }

            @Override 
            public void onError(int code,  String msg) {
                if (mCallObserver == null) {
                    return;
                }
                mCallObserver.onError(code, msg);
            }
        });
    }

    @Override 
    public void hangup(boolean silent) {
        if(mRoomEngine == null) return;
        if (mCallObserver != null) {
            mCallObserver.onDebugInfo(mRoomEngine.getUserId() + " hangup on state " + this.mCallState.typename());
        }
        if (this.mCallState == AUICall1V1State.Idle) {
            if (mCallObserver == null) {
                return;
            }
            mCallObserver.onDebugInfo("already in Idle state, cancel hangup");
            return;
        }
        if (this.mCallState == AUICall1V1State.Calling) {
            mRoomEngine.cancelRequestJoin(this.mOppositeUserId, null, new AUIActionCallback() { 
                @Override 
                public final void onResult(int code, String msg) {
                    onDebugInfo("send cancel call message result: " + code + " : " + ((Object) msg));
                }
            });
        }
        stopCountDownTask();
        mRoomEngine.stopPublish(new AUIActionCallback() { 
            @Override 
            public final void onResult(int errCode, String errMsg) {
                if (mCallObserver != null) {
                    mCallObserver.onDebugInfo("hangup stop publish result " + errCode + " : " + errMsg);
                }
                AliyunLog.d(TAG, "hangup stopPublish onResult " + errCode + " : " +  errMsg);
            }
        });
        mRoomEngine.leave(new AUIActionCallback() { 
            @Override 
            public final void onResult(int errCode, String errMsg) {
                if (mCallObserver != null) {
                    mCallObserver.onDebugInfo("hangup leave result " + errCode + " : " + errMsg);
                }
                AliyunLog.d(TAG, "hangup leave onResult " + errCode + " : " + errMsg);
            }
        });
    }

    @Override 
    public void accept(final String targetUser) {
        if(mRoomEngine == null) return;
        if (mCallObserver != null) {
            mCallObserver.onDebugInfo(mRoomEngine.getUserId() + " accept on state " + this.mCallState.typename());
        }
        if (this.mCallState != AUICall1V1State.WaitAnswer) {
            if (mCallObserver == null) {
                return;
            }
            mCallObserver.onDebugInfo("accept call on incorrect state");
            return;
        }
        stopCountDownTask();
        this.mCallState = AUICall1V1State.Accepted;
        makeSureRTCInfo(mRoomId, new RTCInfoCallback() {
            @Override
            public void onRTCInfo(String token, long timestamp) {
                if(mRoomEngine == null) return;
                if (mCallState == AUICall1V1State.Accepted) {
                    mRoomEngine.responseJoin(targetUser, mRoomId, true, null);
                    AUIRoomConfig roomConfig = new AUIRoomConfig(mRoomId, AUICallConfig.GSLB, token, timestamp);
                    mRoomEngine.join(roomConfig, new AUIActionCallback() {
                        @Override 
                        public final void onResult(int errCode, String errMsg) {
                            if (mCallObserver != null) {
                                mCallObserver.onDebugInfo("accept result " + errCode + " : " + errMsg);
                            }
                            AliyunLog.d("CallModelImpl", "accept onResult " + errCode + " : " + errMsg);
                        }
                    });
                }else{
                    onDebugInfo("accept token back in incorrect state[" + mCallState.typename() + "]");
                }
            }
        });
    }

    @Override 
    public void refuse( String targetUser, boolean silent) {
        if(mRoomEngine == null) return;
        if (mCallObserver != null) {
            mCallObserver.onDebugInfo(mRoomEngine.getUserId() + " refuse on state " + this.mCallState.typename());
        }
        if (this.mCallState != AUICall1V1State.WaitAnswer) {
            if (mCallObserver == null) {
                return;
            }
            mCallObserver.onDebugInfo("refuse call on incorrect state");
            return;
        }
        stopCountDownTask();
        stopPreview();
        this.mOppositeUserId = null;
        this.mCallState = AUICall1V1State.Idle;
        if (!silent) {
            mRoomEngine.responseJoin(targetUser, this.mRoomId, false, null);
        }else{
            if(mCallObserver != null){
                mCallObserver.onSilentRefuse();
            }
        }
    }

    @Override 
    public void setCameraType( CameraType type) {
        if(mRoomEngine == null) return;
        if (type == CameraType.BACK) {
            mRoomEngine.setCameraType(AUIRoomEngineCameraType.BACK, null);
            return;
        }
        mRoomEngine.setCameraType(AUIRoomEngineCameraType.FRONT, null);
    }

    @Override 
    public void openMic(boolean open) {
        if (mRoomEngine == null) return;
        mRoomEngine.switchMicrophone(mRoomEngine.getUserId(), open, null, new AUIActionCallback() {
            @Override
            public void onResult(int errCode, String errMsg) {
                AliyunLog.d(TAG, "openMic switchMicrophone onResult " + errCode + " : " +  errMsg);
            }
        });
    }

    @Override 
    public void openCamera(boolean open) {
        if (mRoomEngine == null) {
            return;
        }
        mRoomEngine.switchCamera(mRoomEngine.getUserId(), open, null, new AUIActionCallback() { 
            @Override 
            public final void onResult(int errCode, String errMsg) {
                AliyunLog.d(TAG, "openCamera switchCamera onResult " + errCode + " : " + errMsg);
                if (mCallObserver == null) {
                    return;
                }
                mCallObserver.onDebugInfo("openCamera switchCamera onResult " + errCode + " : " + errMsg);
            }
        });
    }

    @Override 
    public boolean openLoudspeaker(boolean open) {
        if(mRoomEngine == null) return false;
        if (open) {
            mRoomEngine.switchAudioOutput(AUIAudioOutputType.SPEAKER);
        } else {
            mRoomEngine.switchAudioOutput(AUIAudioOutputType.HEADSET);
        }
        return true;
    }

    @Override 
    public boolean isLoudspeakerOn() {
        if(mRoomEngine == null) return false;
        if (mRoomEngine.getAudioOutputType() == AUIAudioOutputType.SPEAKER) {
            return true;
        }
        return false;
    }

    @Override 
    public void setCallObserver(AUICall1V1Observer observer) {
        this.mCallObserver = observer;
    }

    @Override 
    public AUICall1V1Mode getMode() {
        return this.mCallMode;
    }

    @Override 
    public AUICall1V1State getState() {
        return this.mCallState;
    }

    @Override 
    public String getOppositeUser() {
        return this.mOppositeUserId;
    }

    @Override 
    public String getCurrentUser() {
        if (mRoomEngine == null) {
            return null;
        }
        return mRoomEngine.getUserId();
    }

    @Override 
    public void switchToAudioMode() {
        if (this.mRoomEngine == null) {
            AliyunLog.e(TAG, "switchToAudioMode when room engine is null");
            return;
        }
        this.mCallMode = AUICall1V1Mode.Audio;
        mRoomEngine.switchCamera(mRoomEngine.getUserId(), false, null, new AUIActionCallback() {
            @Override
            public void onResult(int errCode, String errMsg) {
            }
        });
        stopPreview();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("innerType", SWITCH_TO_AUDIO_MODE);
            jsonObject.put("roomId", mRoomId);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        mRoomEngine.sendCustomMessage(mOppositeUserId, jsonObject.toString(), new AUIActionCallback() { 
            @Override 
            public final void onResult(int code, String msg) {
                onDebugInfo("send message switchToAudioMode result " + code + " : " + ((Object) msg));
            }
        });
    }

    @Override 
    public void onJoin( String roomId, String uid) {
        if(mRoomEngine == null) return;
        if (mRoomEngine.getUserId() == null || !mRoomEngine.getUserId().equals(uid)) {
            onDebugInfo("error: onJoin current user not match");
            return;
        }
        if (this.mCallState == AUICall1V1State.Calling) {
            startCountDownTask(null, Type.CALLER);
        }
        preview();
        if (mCallState == AUICall1V1State.Accepted) {
            boolean cameraOn = mCallMode == AUICall1V1Mode.Video;
            mRoomEngine.startPublish(true, true, new AUIActionCallback() {
                @Override
                public void onResult(int errCode, String errMsg) {
                    AliyunLog.d(TAG, "startPublish onResult " + errCode + " : " + errMsg);
                }
            });
            if (!cameraOn) {
                mRoomEngine.switchCamera(mRoomEngine.getUserId(), false, null, null);
            }else{
                mRoomEngine.switchCamera(mRoomEngine.getUserId(), true, null, null);
            }
        }else if(mCallState == AUICall1V1State.Calling){
            boolean cameraOn = mCallMode == AUICall1V1Mode.Video;
            mRoomEngine.startPublish(true, true, new AUIActionCallback() {
                @Override
                public void onResult(int errCode, String errMsg) {
                    AliyunLog.d(TAG, "startPublish onResult " + errCode + " : " + ((Object) errMsg));
                }
            });
            if (!cameraOn) {
                mRoomEngine.switchCamera(mRoomEngine.getUserId(), false, null, null);
            } else {
                mRoomEngine.switchCamera(mRoomEngine.getUserId(), true, null, null);
            }
            String mode = mCallMode.typename();
            JSONObject extra = new JSONObject();
            try {
                extra.put("mode", mode);
                extra.put("type", "single");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mRoomEngine.requestJoin(this.mOppositeUserId, extra, new AUIActionCallback() {
                @Override
                public void onResult(int errCode, String errMsg) {
                    AliyunLog.d(TAG, "requestJoin onResult " + errCode + " : " + ((Object) errMsg));
                }
            });
        }
    }

    @Override 
    public void onLeave() {
        onDebugInfo("onLeave");
        stopPreview();
        resetState();
        if (mCallObserver == null) {
            return;
        }
        mCallObserver.onHangup();
    }

    @Override 
    public void onUserOnline( String roomId, String uid) {
        if(mRoomEngine == null) return;
        onDebugInfo("onUserOnline roomId[" + roomId + "] uid[" + uid + ']');
        if (mRoomEngine.getRoomId() != null && mRoomEngine.getRoomId().equals(roomId)) {
            stopCountDownTask();
            this.mOppositeUserId = uid;
            this.mCallState = AUICall1V1State.Online;
            preview();
        }
    }

    @Override 
    public void onUserOffline( String roomId, String uid) {
        if(mRoomEngine == null) return;
        onDebugInfo("onUserOffline roomId[" + roomId + "] uid[" + uid + ']');
        if (mRoomEngine.getRoomId() != null && mRoomEngine.getRoomId().equals(roomId)) {
            mRoomEngine.stopPublish(new AUIActionCallback() {
                @Override
                public void onResult(int errCode, String errMsg) {
                    AliyunLog.d(TAG, "onUserOffline stopPublish onResult " + errCode + " : " + ((Object) errMsg));
                }
            });
            mRoomEngine.leave(new AUIActionCallback() {
                @Override
                public void onResult(int errCode, String errMsg) {
                    AliyunLog.d(TAG, "onUserOffline leave onResult " + errCode + " : " + ((Object) errMsg));
                }
            });
            this.mOppositeUserId = null;
            this.mCallState = AUICall1V1State.Idle;
            if ((mCallState == AUICall1V1State.Online || mCallState == AUICall1V1State.Accepted) && this.mCallObserver != null) {
                mCallObserver.onHangup();
            }
        }
    }

    @Override 
    public void onUserStartPublish( String uid) {
    }

    @Override 
    public void onUserVideoMuted( String uid, boolean muted) {
        if (mCallObserver == null) {
            return;
        }
        if (muted) {
            mCallObserver.onCameraOff(uid);
        }else{
            mCallObserver.onCameraOn(uid);
        }
    }

    @Override 
    public void onUserAudioMuted( String uid, boolean muted) {
        if (mCallObserver == null) {
            return;
        }
        if (muted) {
            mCallObserver.onMicOff(uid);
        }else{
            mCallObserver.onMicOn(uid);
        }
    }

    @Override 
    public void onUserStopPublish( String uid) {
    }

    @Override 
    public void onRequestJoin( String roomId, String inviter,  JSONObject extra) {
        if(mRoomEngine == null) return;
        if (mCallState == AUICall1V1State.Idle) {
            if (extra == null) {
                return;
            }
            String type = extra.optString("type");
            if (!"single".equals(type)) {
                return;
            }
            String mode = extra.optString("mode");
            if (AUICall1V1Mode.Video.typename().equals(mode)) {
                this.mCallMode = AUICall1V1Mode.Video;
            } else {
                this.mCallMode = AUICall1V1Mode.Audio;
            }
            this.mOppositeUserId = inviter;
            this.mCallState = AUICall1V1State.WaitAnswer;
            this.mRoomId = roomId;
            startCountDownTask(inviter, Type.BECALLER);
            if (mCallObserver != null) {
                mCallObserver.onCall(inviter);
            }
            preview();
            return;
        }
        mRoomEngine.responseJoin(inviter, roomId, false, null);
    }

    @Override 
    public void onCancelRequestJoin( String roomId, String sender,  JSONObject extra) {
        onDebugInfo("onCancelRequestJoin roomId[" + roomId + "] sender[" + sender + "] extra[" + extra + "] state[" + this.mCallState + "] opposite_user[" + this.mOppositeUserId + ']');
        if (mOppositeUserId != null && !mOppositeUserId.equals(sender)) {
            return;
        }
        if (this.mCallState == AUICall1V1State.WaitAnswer) {
            this.mCallState = AUICall1V1State.Idle;
            if (mCallObserver == null) {
                return;
            }
            mCallObserver.onCancel();
        } else if (this.mCallState == AUICall1V1State.Accepted || this.mCallState == AUICall1V1State.Online) {
            if (mRoomEngine != null) {
                mRoomEngine.leave(null);
            }
            this.mCallState = AUICall1V1State.Idle;
            if (mCallObserver == null) {
                return;
            }
            mCallObserver.onCancel();
        }
    }

    @Override 
    public void onResponseJoin( String roomId, String invitee, boolean agree,  JSONObject extra) {
        stopCountDownTask();
        if (!agree) {
            if (mRoomEngine != null) {
                mRoomEngine.stopPublish(new AUIActionCallback() {
                    @Override
                    public void onResult(int errCode, String errMsg) {
                        AliyunLog.d(TAG, "onResponseJoin stopPublish onResult " + errCode + " : " + ((Object) errMsg));
                    }
                });
                mRoomEngine.leave(new AUIActionCallback() {
                    @Override
                    public void onResult(int errCode, String errMsg) {
                        AliyunLog.d(TAG, "onResponseJoin leave onResult " + errCode + " : " + ((Object) errMsg));
                    }
                });
            }
            if (this.mCallState != AUICall1V1State.Calling || mCallObserver == null) {
                return;
            }
            mCallObserver.onRefuse(invitee);
        } else if ((this.mCallState == AUICall1V1State.Calling || this.mCallState == AUICall1V1State.Online) && mCallObserver != null) {
            mCallObserver.onAccept(invitee);
        }
    }

    @Override 
    public void onRequestPublish( String roomId, String inviter,  JSONObject extra) {
    }

    @Override 
    public void onCancelRequestPublish( String sender,  JSONObject extra) {
    }

    @Override 
    public void onResponsePublish( String roomId, String invitee, boolean agree,  JSONObject extra) {
    }

    @Override 
    public void onRequestSwitchMic( String roomId, String requester, boolean off,  JSONObject extra) {
        if (mRoomEngine == null) {
            return;
        }
        mRoomEngine.switchMicrophone(mRoomEngine.getUserId(), !off, null, new AUIActionCallback() {
            @Override
            public void onResult(int errCode, String errMsg) {
                AliyunLog.d(TAG, "onRequestSwitchMic onResult " + errCode + " : " + ((Object) errMsg));
            }
        });
    }

    @Override 
    public void onRequestSwitchCamera( String roomId, String requester, boolean off,  JSONObject extra) {
        if ((requester != null && !requester.equals(mOppositeUserId)) || mRoomEngine == null) {
            return;
        }
        mRoomEngine.switchCamera(mRoomEngine.getUserId(), !off, null, new AUIActionCallback() {
            @Override
            public void onResult(int errCode, String errMsg) {
                AliyunLog.e(TAG, "onRequestSwitchCamera switchCamera onResult " + errCode + " : " + ((Object) errMsg));
            }
        });
    }

    @Override 
    public void onCustomMessageReceived( String sender, String data) {
        AliyunLog.d(TAG, "onCustomMessageReceived sender[" + sender + "] data[" + data + ']');
        onDebugInfo("onCustomMessageReceived sender[" + sender + "] data[" + data + ']');
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (jsonObj.optInt("innerType") == SWITCH_TO_AUDIO_MODE) {
            this.mCallMode = AUICall1V1Mode.Audio;
            if (mRoomEngine != null) {
                mRoomEngine.switchCamera(mRoomEngine.getUserId(), false, null, new AUIActionCallback() { 
                    @Override 
                    public final void onResult(int code, String msg) {
                        onDebugInfo("switch audio mode result " + code + " : " + ((Object) msg));
                    }
                });
            }
            stopPreview();
            if (mCallObserver == null) {
                return;
            }
            mCallObserver.onSwitchToAudioMode();
        }
    }

    @Override 
    public void onDebugInfo( String info) {
        AliyunLog.d(TAG, "onDebugInfo: " + info);
        if (mCallObserver == null) {
            return;
        }
        mCallObserver.onDebugInfo(info);
    }

    @Override 
    public void onError(int code,  String msg) {
        AliyunLog.e(TAG, "onError " + code + ' ' + ((Object) msg));
        if (mCallObserver == null) {
            return;
        }
        mCallObserver.onError(code, msg);
    }

    @Override 
    public void setTokenAccessor( TokenAccessor accessor) {
        this.mTokenAccessor = accessor;
    }
}
