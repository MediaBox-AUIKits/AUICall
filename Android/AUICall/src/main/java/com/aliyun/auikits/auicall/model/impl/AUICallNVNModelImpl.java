package com.aliyun.auikits.auicall.model.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alivc.auimessage.model.token.IMNewToken;
import com.alivc.rtc.AliRtcEngine;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.bean.AUICallNVNState;
import com.aliyun.auikits.auicall.model.callback.AUICallNVNObserver;
import com.aliyun.auikits.auicall.model.callback.CreateRoomCallback;
import com.aliyun.auikits.auicall.model.callback.TokenAccessor;
import com.aliyun.auikits.auicall.bean.InitialInfo;
import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.callback.InitCallback;
import com.aliyun.auikits.auicall.model.callback.RTCInfoCallback;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public final class AUICallNVNModelImpl implements AUICallNVNModel, AUIRoomEngineObserver {

    private static final String DEBUG = "debug";

    private static final String ERROR = "error";

    private static final String HOST_ID = "hostId";

    private static final String INNER_TYPE = "innerType";

    private static final String MUTE_ALL = "muteAll";

    private static final String ROOM_ID = "roomId";

    private static final String ROOM_INFO = "roomInfo";

    private static final String TAG = "MeetingCall";
    private static final int TYPE_DISMISS_MEETING = 14000;
    private static final int TYPE_KICK_OUT_USER = 11000;
    private static final int TYPE_MUTE_ALL = 12000;
    private static final int TYPE_ROOM_INFO_UPDATE = 15000;
    private static final int TYPE_UN_MUTE_ALL = 13000;

    private static final String WARNING = "warning";
    private boolean mBeautyEnable;

    private UserInfo mCurrentUser;

    private String mHostId;
    private boolean mHostRequestMute;

    private IMNewToken mImToken;
    private boolean mInited;

    private String mInviter;

    private ViewGroup mMainContainer;
    private boolean mMirror;

    private AUICallNVNObserver mObserver;

    private AUIRoomEngine mRoomEngine;

    private String mRoomId;

    private TokenAccessor mTokenAccessor;
    private boolean mWithAudio;
    private boolean mWithVideo;

    private AUICallNVNState mState = AUICallNVNState.Idle;

    private Map<String, UserInfo> mMembers = new LinkedHashMap();

    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    private Map<String, UserInfo> mCallingMembers = new LinkedHashMap();

    private Map<String, CountDownTask> mCallingTimeStats = new LinkedHashMap();

    private AUIRoomEngineCameraType mCameraType = AUIRoomEngineCameraType.FRONT;

    private AliRtcEngine.AliRtcBeautyConfig mBeautyConfig = new AliRtcEngine.AliRtcBeautyConfig();

    private List<String> mCreatedRooms = new ArrayList();

    public enum Type {
        INVITER,
        INVITEE
    }

    public static final class CountDownTask implements Runnable {

        private final WeakReference<AUICallNVNModel> mModelRef;

        private final Type mType;

        private final String mUserId;

        public CountDownTask( String userId, Type type, AUICallNVNModel model) {
            this.mType = type;
            this.mModelRef = new WeakReference<>(model);
            this.mUserId = userId;
        }

        @Override 
        public void run() {
            AUICallNVNModel model = this.mModelRef.get();
            if (model == null) {
                return;
            }
            if (this.mType == Type.INVITER) {
                model.cancelInvite(this.mUserId, true);
            } else if (this.mType == Type.INVITEE) {
                model.refuse(this.mUserId, true);
            }
        }
    }

    @Override 
    public void setObserver( AUICallNVNObserver observer) {
        this.mObserver = observer;
    }

    @Override 
    public void setViewContainer(String uid,  ViewGroup container, boolean isTop) {
        if(mRoomEngine == null) return;
        if (mRoomEngine.getUserId() != null && mRoomEngine.getUserId().equals(uid)) {
            this.mMainContainer = container;
            mRoomEngine.setRenderViewLayout(uid, container, isTop, this.mMirror);
        }else{
            mRoomEngine.setRenderViewLayout(uid, container, isTop, false);
        }
    }

    @Override
    public void init(Context context, InitialInfo initialInfo, final InitCallback callback) {
        if (this.mInited) {
            onDebugInfo(TAG + DEBUG + ": init uid[" + initialInfo.getUserId() + "] deviceId[" + initialInfo.getDeviceId() + "] already inited!!!");
            return;
        }
        AppConfig.setAppInfo(initialInfo.getAppId());
        onDebugInfo(TAG + DEBUG + ": init uid[" + initialInfo.getUserId() + "] deviceId[" + initialInfo.getDeviceId() + ']');
        this.mCurrentUser = new UserInfo(initialInfo.getUserId(), false, false, false);
        this.mImToken = mTokenAccessor.getIMToken(initialInfo.getUserId());
        this.mState = AUICallNVNState.Idle;
        AUIRoomEngine createRoomEngine = AUIRoomEngineFactory.createRoomEngine();
        AlivcBase.setIntegrationWay(AUICallConfig.AUI_CALL);
        this.mRoomEngine = createRoomEngine;
        AUIRoomUserInfo userInfo = new AUIRoomUserInfo(initialInfo.getUserId(), initialInfo.getDeviceId(), this.mImToken);
        createRoomEngine.login(context, userInfo, new AUIActionCallback() {
            @Override 
            public final void onResult(final int errCode, final String errMsg) {
                onDebugInfo(TAG + DEBUG + ": login result " + errCode + " : " + errMsg);
                AliyunLog.d(TAG, "login " + errCode + ' ' + errMsg);
                mUIHandler.post(new Runnable() { 
                    @Override 
                    public final void run() {
                        callback.onResult(errCode, errMsg);
                    }
                });
            }
        });
        mRoomEngine.addObserver(this);
        this.mInited = true;
    }

    @Override 
    public void release() {
        onDebugInfo(TAG + DEBUG + ": release");
        if (mRoomEngine != null) {
            mRoomEngine.logout();
        }
        resetState();
        this.mCurrentUser = null;
        this.mObserver = null;
        this.mRoomEngine = null;
        this.mState = AUICallNVNState.Idle;
        this.mInited = false;
    }

    private final void makeSureRTCInfo(final String roomId, final RTCInfoCallback callback) {
        new Thread(new Runnable() { 
            @Override 
            public final void run() {
                TokenAccessor tokenAccessor = mTokenAccessor;
                UserInfo userInfo = mCurrentUser;
                final String rtcToken = tokenAccessor.getRtcToken(userInfo.getUserId(), roomId);
                final Long rtcTimestamp = tokenAccessor.getRtcTimestamp();
                mUIHandler.post(new Runnable() { 
                    @Override 
                    public final void run() {
                        if(TextUtils.isEmpty(rtcToken) || rtcTimestamp == null){
                            onDebugInfo("get rtc token failed!!!");
                            return;
                        }
                        callback.onRTCInfo(rtcToken, rtcTimestamp);
                    }
                });
            }
        }).start();
    }

    @Override 
    public void create(String roomId, final CreateRoomCallback callback) {
        if (mRoomEngine == null) {
            return;
        }
        mRoomEngine.createRoom(roomId, new AUICreateRoomCallback() {
            @Override 
            public void onSuccess( String roomId) {
                List list;
                if (roomId != null) {
                    list = AUICallNVNModelImpl.this.mCreatedRooms;
                    list.add(roomId);
                }
                if (callback == null) {
                    return;
                }
                callback.onSuccess(roomId);
            }

            @Override 
            public void onError(int code,  String msg) {
                if (callback == null) {
                    return;
                }
                callback.onError(code, msg);
            }
        });
    }

    @Override 
    public void join(final String roomId, final boolean withVideo, final boolean withAudio) {
        if (this.mState != AUICallNVNState.Idle) {
            onDebugInfo(TAG + WARNING + ": join in unexpected state [" + this.mState.getDesc() + ']');
            return;
        }
        this.mState = AUICallNVNState.Joining;
        makeSureRTCInfo(roomId, new RTCInfoCallback() {
            @Override
            public void onRTCInfo(String token, long timestamp) {
                mWithVideo = withVideo;
                mWithAudio = withAudio;
                mCurrentUser.setCameraOn(withVideo);
                mCurrentUser.setMicOn(withAudio);
                mRoomId = roomId;
                if (mRoomEngine == null) {
                    return;
                }
                AUIRoomConfig roomConfig = new AUIRoomConfig(mRoomId, AUICallConfig.GSLB, token, timestamp);
                mRoomEngine.join(roomConfig, new AUIActionCallback() {
                    @Override 
                    public final void onResult(int code, String msg) {
                        if (code != 0) {
                            mState = AUICallNVNState.Idle;
                            if (mObserver == null) {
                                return;
                            }
                            mObserver.onError(code, msg);
                            return;
                        }
                        mObserver.onDebugInfo(TAG + DEBUG + " join result " + code + " : " + ((Object) msg));
                    }
                });
            }
        });
    }

    private final void startCountDownTask(String userId, Type type) {
        onDebugInfo("startCountDownTask " + userId + " type[" + type + ']');
        CountDownTask cancelCallTask = new CountDownTask(userId, type, this);
        this.mCallingTimeStats.put(userId, cancelCallTask);
        this.mUIHandler.postDelayed(cancelCallTask, AUICallConfig.CALLING_TIME_OUT_MILLISECONDS);
    }

    public final void stopCountDownTask( String userId) {
        onDebugInfo("stopCountDownTask " + userId);
        CountDownTask task = this.mCallingTimeStats.remove(userId);
        if (task != null) {
            this.mUIHandler.removeCallbacks(task);
        }
    }

    @Override 
    public void invite( final String userId) {
        if (this.mState == AUICallNVNState.Online) {
            UserInfo userInfo = this.mCurrentUser;
            if (!TextUtils.equals(userId, userInfo == null ? null : userInfo.getUserId())) {
                this.mCallingMembers.put(userId, new UserInfo(userId, true, true, false));
                UserInfo callingUser = this.mCallingMembers.get(userId);
                callingUser.setCalling(true);
                startCountDownTask(userId, Type.INVITER);
                JSONObject extra = new JSONObject();
                try {
                    extra.put("type", "group");
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                AUIRoomEngine roomEngine = this.mRoomEngine;
                if (roomEngine == null) {
                    return;
                }
                roomEngine.requestJoin(userId, extra, new AUIActionCallback() { 
                    @Override 
                    public final void onResult(int code, String msg) {
                        if (code != 0) {
                            AUICallNVNObserver meetingCallObserver = mObserver;
                            if (meetingCallObserver == null) {
                                return;
                            }
                            meetingCallObserver.onError(code, msg);
                            return;
                        }
                        onDebugInfo(TAG + DEBUG + " invite user[" + userId + "] result " + code + " : " + ((Object) msg));
                    }
                });
                return;
            }
        }
        onDebugInfo(TAG + WARNING + ": invite user[" + userId + "] in unexpected state [" + this.mState.getDesc() + ']');
    }

    @Override 
    public void cancelInvite( String userId, boolean silent) {
        AUICallNVNObserver meetingCallObserver;
        onDebugInfo(TAG + WARNING + ": cancel invite user[" + userId + "] in state [" + this.mState.getDesc() + ']');
        this.mCallingMembers.remove(userId);
        stopCountDownTask(userId);
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine != null) {
            roomEngine.cancelRequestJoin(userId, null, null);
        }
        if (!silent || (meetingCallObserver = this.mObserver) == null) {
            return;
        }
        meetingCallObserver.onSilentCancel();
    }

    @Override 
    public void accept(final String userId, final boolean withVideo) {
        if (this.mState != AUICallNVNState.WaitAnswer) {
            onDebugInfo(TAG + WARNING + ": accept user[" + userId + "] invite in unexpected state [" + this.mState.getDesc() + ']');
            return;
        }
        stopCountDownTask(userId);
        String str = this.mRoomId;
        makeSureRTCInfo(str, new RTCInfoCallback() {
            @Override
            public void onRTCInfo(String token, long timestamp) {
                mWithVideo = withVideo;
                mCurrentUser.setCameraOn(mWithVideo);
                mCurrentUser.setMicOn(mWithAudio);
                if (mRoomEngine != null) {
                    mRoomEngine.responseJoin(userId, mRoomId, true, null);
                }
                mState = AUICallNVNState.Joining;
                if (mRoomEngine != null) {
                    AUIRoomConfig roomConfig = new AUIRoomConfig(mRoomId, AUICallConfig.GSLB, token, timestamp);
                    mRoomEngine.join(roomConfig, new AUIActionCallback() {
                        @Override 
                        public final void onResult(int code, String msg) {
                            AUICallNVNObserver meetingCallObserver;
                            if (code != 0) {
                                mState = AUICallNVNState.Idle;
                                if (mObserver == null) {
                                    return;
                                }
                                mObserver.onError(code, msg);
                                return;
                            }
                            onDebugInfo(TAG + DEBUG + " accept result " + code + " : " + ((Object) msg));
                        }
                    });
                }
            }
        });
    }

    @Override 
    public void refuse( String userId, boolean silent) {
        if (this.mState != AUICallNVNState.WaitAnswer) {
            onDebugInfo(TAG + WARNING + ": accept user[" + userId + "] invite in unexpected state [" + this.mState.getDesc() + ']');
            return;
        }
        stopCountDownTask(userId);
        this.mState = AUICallNVNState.Idle;
        this.mInviter = null;
        if (!silent) {
            AUIRoomEngine roomEngine = this.mRoomEngine;
            if (roomEngine != null) {
                roomEngine.responseJoin(userId, this.mRoomId, false, null);
                return;
            }
            return;
        }
        AUICallNVNObserver meetingCallObserver = this.mObserver;
        if (meetingCallObserver == null) {
            return;
        }
        meetingCallObserver.onSilentRefuse();
    }

    @Override 
    public void openMic( final String userId, final boolean open) {
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine == null) {
            return;
        }
        roomEngine.switchMicrophone(userId, open, null, new AUIActionCallback() { 
            @Override 
            public final void onResult(int errCode, String errMsg) {
                AliyunLog.d(TAG, "openMic switchMicrophone onResult " + errCode + " : " + ((Object) errMsg));
                onDebugInfo(TAG + DEBUG + " openMic user[" + userId + "] open[" + open + "] result " + errCode + " : " + ((Object) errMsg));
            }
        });
    }

    @Override 
    public void openCamera( final String userId, final boolean open) {
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine == null) {
            return;
        }
        roomEngine.switchCamera(userId, open, null, new AUIActionCallback() { 
            @Override 
            public final void onResult(int errCode, String errMsg) {
                AliyunLog.d(TAG, "openCamera switchCamera onResult " + errCode + " : " + ((Object) errMsg));
                onDebugInfo(TAG + DEBUG + " openCamera user[" + userId + "] open[" + open + "] result " + errCode + " : " + ((Object) errMsg));
            }
        });
    }

    @Override 
    public boolean openLoudspeaker(boolean open) {
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (open) {
            if (roomEngine != null) {
                roomEngine.switchAudioOutput(AUIAudioOutputType.SPEAKER);
            }
        } else {
            if (roomEngine != null) {
                roomEngine.switchAudioOutput(AUIAudioOutputType.HEADSET);
            }
        }
        StringBuilder append = new StringBuilder().append("openLoudspeaker ").append(open).append(", result ");
        AUIAudioOutputType audioOutputType = mRoomEngine == null ? null : mRoomEngine.getAudioOutputType();
        onDebugInfo(append.append(audioOutputType.getTypename()).toString());
        return true;
    }

    @Override 
    public boolean isLoudspeakerOn() {
        AUIRoomEngine roomEngine = this.mRoomEngine;
        AUIAudioOutputType audioOutputType = roomEngine == null ? null : roomEngine.getAudioOutputType();
        if (audioOutputType != null && audioOutputType == AUIAudioOutputType.SPEAKER) {
            return true;
        }
        return false;
    }

    @Override 
    public Map<String, UserInfo> getMeetingMembers() {
        return this.mMembers;
    }

    @Override 
    public Map<String, UserInfo> getCallingMembers() {
        return this.mCallingMembers;
    }

    @Override 
    public UserInfo getHost() {
        if (this.mHostId == null) {
            return null;
        }
        UserInfo userInfo = this.mCurrentUser;
        if (userInfo != null) {
            if (TextUtils.equals(userInfo.getUserId(), this.mHostId)) {
                return this.mCurrentUser;
            }
        }
        return this.mMembers.get(this.mHostId);
    }

    @Override 
    public boolean isHost() {
        UserInfo userInfo = this.mCurrentUser;
        if (userInfo != null) {
            return userInfo.isHost();
        }
        return false;
    }

    @Override 
    public void leave() {
        onDebugInfo(TAG + DEBUG + ": leave in state [" + this.mState.getDesc() + ']');
        if (this.mState == AUICallNVNState.Idle) {
            return;
        }
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine != null) {
            roomEngine.stopPublish(null);
            roomEngine.leave(null);
        }
    }

    @Override 
    public void kickOut( String userId) {
        onDebugInfo(TAG + DEBUG + ": kickOut user[" + userId + "] in state [" + this.mState.getDesc() + ']');
        if (this.mState == AUICallNVNState.Online && this.mRoomEngine != null) {
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(INNER_TYPE, TYPE_KICK_OUT_USER);
                jsonObj.put(ROOM_ID, mRoomEngine.getRoomId());
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            mRoomEngine.sendCustomMessage(userId, jsonObj.toString(), null);
        }
    }

    @Override 
    public void muteAll() {
        onDebugInfo(TAG + DEBUG + ": muteAll in state [" + this.mState.getDesc() + ']');
        if (this.mState != AUICallNVNState.Online) {
            return;
        }
        this.mHostRequestMute = true;
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine == null) {
            return;
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(INNER_TYPE, TYPE_MUTE_ALL);
            jsonObj.put(ROOM_ID, roomEngine.getRoomId());
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        roomEngine.sendGroupCustomMessage(jsonObj.toString(), null);
    }

    @Override 
    public void unMuteAll() {
        onDebugInfo(TAG + DEBUG + ": unMuteAll in state [" + this.mState.getDesc() + ']');
        if (this.mState != AUICallNVNState.Online) {
            return;
        }
        this.mHostRequestMute = false;
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine == null) {
            return;
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(INNER_TYPE, TYPE_UN_MUTE_ALL);
            jsonObj.put(ROOM_ID, roomEngine.getRoomId());
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        roomEngine.sendGroupCustomMessage(jsonObj.toString(), null);
    }

    @Override 
    public boolean isMuteAll() {
        return this.mHostRequestMute;
    }

    @Override 
    public void dismiss() {
        String str = TAG + DEBUG + ": dismiss in state [" + this.mState.getDesc() + ']';
        if (this.mState != AUICallNVNState.Online) {
            return;
        }
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine != null) {
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(INNER_TYPE, TYPE_DISMISS_MEETING);
                jsonObj.put(ROOM_ID, roomEngine.getRoomId());
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            roomEngine.sendGroupCustomMessage(jsonObj.toString(), null);
        }
        leave();
    }

    @Override 
    public void onJoin( String roomId, String uid) {
        if (this.mState != AUICallNVNState.Joining) {
            onDebugInfo(TAG + DEBUG + ": onJoin in unexpected state [" + this.mState.getDesc() + ']');
        } else if (this.mCurrentUser == null) {
            onDebugInfo("current user is null");
        } else {
            if (this.mCreatedRooms.contains(roomId)) {
                UserInfo userInfo = this.mCurrentUser;
                this.mHostId = userInfo.getUserId();
                userInfo.setHost(true);
            }
            this.mInviter = null;
            this.mState = AUICallNVNState.Online;
            AUIRoomEngine roomEngine = this.mRoomEngine;
            if (roomEngine != null) {
                roomEngine.startPublish(this.mWithVideo, this.mWithAudio, new AUIActionCallback() { 
                    @Override 
                    public final void onResult(int code, String msg) {
                        AliyunLog.d(TAG, "onJoin startPublish result " + code + " : " + ((Object) msg));
                        if (code != 0) {
                            mState = AUICallNVNState.Joining;
                            if (mObserver == null) {
                                return;
                            }
                            mObserver.onError(code, msg);
                        }
                    }
                });
            }
            AUICallNVNObserver meetingCallObserver = this.mObserver;
            if (meetingCallObserver == null) {
                return;
            }
            meetingCallObserver.onJoin(roomId);
        }
    }

    private final void resetState() {
        this.mMembers.clear();
        this.mCallingMembers.clear();
        for (CountDownTask task : this.mCallingTimeStats.values()) {
            this.mUIHandler.removeCallbacks(task);
        }
        this.mCallingTimeStats.clear();
        this.mHostRequestMute = false;
        this.mHostId = null;
        UserInfo userInfo = this.mCurrentUser;
        if (userInfo != null) {
            userInfo.reset();
        }
        this.mWithVideo = false;
        this.mWithAudio = false;
        this.mState = AUICallNVNState.Idle;
        this.mInviter = null;
        this.mBeautyEnable = false;
        this.mBeautyConfig = new AliRtcEngine.AliRtcBeautyConfig();
        this.mCreatedRooms.clear();
    }

    @Override 
    public void onLeave() {
        resetState();
        AUICallNVNObserver meetingCallObserver = this.mObserver;
        if (meetingCallObserver == null) {
            return;
        }
        meetingCallObserver.onLeave();
    }

    @Override 
    public void onUserOnline( String roomId, String uid) {
        if (!TextUtils.isEmpty(roomId) && TextUtils.equals(roomId, this.mRoomId)) {
            this.mCallingMembers.remove(uid);
            stopCountDownTask(uid);
            this.mMembers.put(uid, new UserInfo(uid, false, false, false));
            UserInfo userInfo = this.mMembers.get(uid);
            userInfo.setCameraOn(true);
            userInfo.setMicOn(true);
            AUICallNVNObserver meetingCallObserver = this.mObserver;
            if (TextUtils.equals(this.mHostId, uid)) {
                userInfo.setHost(true);
                if (meetingCallObserver != null) {
                    meetingCallObserver.onUpdateHost(userInfo);
                }
            }
            if (isHost()) {
                JSONObject obj = new JSONObject();
                AUIRoomEngine roomEngine = this.mRoomEngine;
                try {
                    JSONObject info = new JSONObject();
                    obj.put(INNER_TYPE, TYPE_ROOM_INFO_UPDATE);
                    obj.put(ROOM_ID, roomEngine == null ? null : roomEngine.getRoomId());
                    UserInfo userInfo5 = this.mCurrentUser;
                    info.put(HOST_ID, userInfo5.getUserId());
                    info.put(MUTE_ALL, this.mHostRequestMute);
                    obj.put(ROOM_INFO, info);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (roomEngine != null) {
                    roomEngine.sendCustomMessage(uid, obj.toString(), null);
                }
            }
            if (meetingCallObserver != null) {
                meetingCallObserver.onUserJoin(uid);
            }
            if (userInfo.getCameraOn()) {
                if (meetingCallObserver != null) {
                    meetingCallObserver.onCameraOn(uid);
                }
            } else {
                if (meetingCallObserver != null) {
                    meetingCallObserver.onCameraOff(uid);
                }
            }
            if (userInfo.getMicOn()) {
                if (meetingCallObserver != null) {
                    meetingCallObserver.onMicOn(uid);
                }
            }else{
                if (meetingCallObserver != null) {
                    meetingCallObserver.onMicOff(uid);
                }
            }
        }
    }

    @Override 
    public void onUserOffline( String roomId, String uid) {
        if (!TextUtils.isEmpty(roomId) && TextUtils.equals(roomId, this.mRoomId)) {
            if (TextUtils.equals(uid, this.mHostId)) {
                this.mHostId = null;
            }
            this.mMembers.remove(uid);
            AUICallNVNObserver meetingCallObserver = this.mObserver;
            if (meetingCallObserver == null) {
                return;
            }
            meetingCallObserver.onUserLeave(uid);
        }
    }

    @Override 
    public void onUserStartPublish( String uid) {
    }

    @Override 
    public void onUserVideoMuted( String uid, boolean muted) {
        if (TextUtils.isEmpty(uid)) {
            throw new RuntimeException("uid should not be null");
        }
        if (!this.mMembers.containsKey(uid)) {
            AUIRoomEngine roomEngine = this.mRoomEngine;
            if (!TextUtils.equals(uid, roomEngine.getUserId())) {
                return;
            }
        }
        if (this.mMembers.containsKey(uid)) {
            UserInfo userInfo = this.mMembers.get(uid);
            userInfo.setCameraOn(!muted);
        } else {
            UserInfo userInfo2 = this.mCurrentUser;
            userInfo2.setCameraOn(!muted);
        }
        AUICallNVNObserver meetingCallObserver = this.mObserver;
        if (muted) {
            if (meetingCallObserver != null) {
                meetingCallObserver.onCameraOff(uid);
            }
        }else{
            if (meetingCallObserver != null) {
                meetingCallObserver.onCameraOn(uid);
            }
        }
    }

    @Override 
    public void onUserAudioMuted( String uid, boolean muted) {
        if (TextUtils.isEmpty(uid)) {
            throw new RuntimeException("uid should not be null");
        }
        if (!this.mMembers.containsKey(uid)) {
            AUIRoomEngine roomEngine = this.mRoomEngine;
            if (!TextUtils.equals(uid, roomEngine.getUserId())) {
                return;
            }
        }
        if (this.mMembers.containsKey(uid)) {
            UserInfo userInfo = this.mMembers.get(uid);
            userInfo.setMicOn(!muted);
        } else {
            UserInfo userInfo2 = this.mCurrentUser;
            userInfo2.setMicOn(!muted);
        }
        AUICallNVNObserver meetingCallObserver = this.mObserver;
        if (muted) {
            if (meetingCallObserver != null) {
                meetingCallObserver.onMicOff(uid);
            }
        }else{
            if (meetingCallObserver != null) {
                meetingCallObserver.onMicOn(uid);
            }
        }
    }

    @Override 
    public void onUserStopPublish( String uid) {
    }

    @Override 
    public void onRequestJoin( String roomId, String inviter,  JSONObject extra) {
        AUIRoomEngine roomEngine;
        onDebugInfo(TAG + DEBUG + ": onRequestJoin roomId[" + roomId + "] inviter[" + inviter + "] state[" + this.mState.getDesc() + ']');
        if (extra == null) {
            return;
        }
        String type = extra.optString("type");
        if (!TextUtils.equals(type, "group")) {
            return;
        }
        if (this.mState == AUICallNVNState.Idle) {
            UserInfo userInfo = this.mCurrentUser;
            if (!TextUtils.equals(inviter, userInfo == null ? null : userInfo.getUserId())) {
                this.mInviter = inviter;
                startCountDownTask(inviter, Type.INVITEE);
                this.mState = AUICallNVNState.WaitAnswer;
                this.mRoomId = roomId;
                AUICallNVNObserver meetingCallObserver = this.mObserver;
                if (meetingCallObserver == null) {
                    return;
                }
                meetingCallObserver.onInvite(inviter, roomId);
                return;
            }
        }
        if (this.mInviter == null || (roomEngine = this.mRoomEngine) == null) {
            return;
        }
        roomEngine.responseJoin(inviter, roomId, false, null);
    }

    @Override 
    public void onCancelRequestJoin( String roomId, String sender,  JSONObject extra) {
        onDebugInfo(TAG + DEBUG + ": onCancelRequestJoin roomId[" + roomId + "] sender[" + sender + "] state[" + this.mState.getDesc() + "] inviter[" + ((Object) this.mInviter) + ']');
        if (!TextUtils.equals(this.mInviter, sender)) {
            return;
        }
        if (mState == AUICallNVNState.Joining || mState == AUICallNVNState.Online) {
            leave();
        }else if(mState == AUICallNVNState.WaitAnswer){
            resetState();
            AUICallNVNObserver meetingCallObserver = this.mObserver;
            if (meetingCallObserver == null) {
                return;
            }
            meetingCallObserver.onCancel();
        }
    }

    @Override 
    public void onResponseJoin( String roomId, String invitee, boolean agree,  JSONObject extra) {
        onDebugInfo(TAG + DEBUG + ": onResponseJoin roomId[" + roomId + "] invitee[" + invitee + "] state[" + this.mState.getDesc() + ']');
        if (this.mState != AUICallNVNState.Online) {
            return;
        }
        this.mCallingMembers.remove(invitee);
        stopCountDownTask(invitee);
        AUICallNVNObserver meetingCallObserver = this.mObserver;
        if (agree) {
            if (meetingCallObserver == null) {
                return;
            }
            meetingCallObserver.onAccept(invitee);
            return;
        }
        if (meetingCallObserver == null) {
            return;
        }
        meetingCallObserver.onRefuse(invitee);
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
        AUICallNVNObserver meetingCallObserver;
        onDebugInfo(TAG + DEBUG + ": onRequestSwitchMic roomId[" + roomId + "] requester[" + requester + "] state[" + this.mState.getDesc() + ']');
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (TextUtils.equals(roomId, roomEngine == null ? null : roomEngine.getRoomId()) && (meetingCallObserver = this.mObserver) != null) {
            meetingCallObserver.onRequestMic(requester, !off);
        }
    }

    @Override 
    public void onRequestSwitchCamera( String roomId, String requester, boolean off,  JSONObject extra) {
        AUICallNVNObserver meetingCallObserver;
        onDebugInfo(TAG + DEBUG + ": onRequestSwitchCamera roomId[" + roomId + "] requester[" + requester + "] state[" + this.mState.getDesc() + ']');
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (TextUtils.equals(roomId, roomEngine == null ? null : roomEngine.getRoomId()) && (meetingCallObserver = this.mObserver) != null) {
            meetingCallObserver.onRequestCamera(requester, !off);
        }
    }

    @Override 
    public void onCustomMessageReceived( String sender, String data) {
        onDebugInfo("onCustomMessageReceived sender[" + sender + "] data[" + data + ']');
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        int optInt = jsonObj.optInt(INNER_TYPE);
        AUICallNVNObserver meetingCallObserver = this.mObserver;
        AUIRoomEngine roomEngine = this.mRoomEngine;
        String userId = roomEngine.getUserId();
        if (optInt == TYPE_KICK_OUT_USER) {
            leave();
            if (meetingCallObserver == null) {
                return;
            }
            meetingCallObserver.onKickOut();
        } else if (optInt == TYPE_MUTE_ALL) {
            if (TextUtils.equals(sender, roomEngine.getUserId())) {
                return;
            }
            openMic(userId, false);
            this.mHostRequestMute = true;
            if (meetingCallObserver == null) {
                return;
            }
            meetingCallObserver.onHostMute(true);
        } else if (optInt == TYPE_UN_MUTE_ALL) {
            if (TextUtils.equals(sender, roomEngine.getUserId())) {
                return;
            }
            this.mHostRequestMute = false;
            if (meetingCallObserver == null) {
                return;
            }
            meetingCallObserver.onHostMute(false);
        } else if (optInt == TYPE_DISMISS_MEETING) {
            if (TextUtils.equals(sender, roomEngine.getUserId())) {
                return;
            }
            leave();
        } else if (optInt == TYPE_ROOM_INFO_UPDATE) {
            JSONObject roomInfo = jsonObj.optJSONObject(ROOM_INFO);
            String hostId = roomInfo.optString(HOST_ID);
            String roomId = jsonObj.optString(ROOM_ID);
            boolean muteAll = roomInfo.optBoolean(MUTE_ALL);
            if (TextUtils.isEmpty(hostId) || TextUtils.isEmpty(roomId)) {
                return;
            }
            if (TextUtils.equals(roomEngine == null ? null : roomEngine.getRoomId(), roomId)) {
                this.mHostId = hostId;
                if (this.mMembers.containsKey(hostId)) {
                    UserInfo userInfo = this.mMembers.get(this.mHostId);
                    userInfo.setHost(true);
                    if (meetingCallObserver != null) {
                        meetingCallObserver.onUpdateHost(userInfo);
                    }
                }
                this.mHostRequestMute = muteAll;
                if (muteAll) {
                    openMic(userId, false);
                    if (meetingCallObserver == null) {
                        return;
                    }
                    meetingCallObserver.onHostMute(true);
                }
            }
        }
    }

    @Override 
    public void onDebugInfo( String info) {
        AUICallNVNObserver meetingCallObserver = this.mObserver;
        if (meetingCallObserver == null) {
            return;
        }
        meetingCallObserver.onDebugInfo(info);
    }

    @Override 
    public void onError(int code,  String msg) {
        AUICallNVNObserver meetingCallObserver = this.mObserver;
        if (meetingCallObserver == null) {
            return;
        }
        meetingCallObserver.onError(code, msg);
    }

    @Override 
    public void setTokenAccessor( TokenAccessor accessor) {
        this.mTokenAccessor = accessor;
    }

    @Override 
    public UserInfo getCurrentUser() {
        UserInfo userInfo = this.mCurrentUser;
        if (userInfo == null) {
            return null;
        }
        String userId = userInfo.getUserId();
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (!TextUtils.equals(userId, roomEngine != null ? roomEngine.getUserId() : null)) {
            onDebugInfo("meeting model user not match room model");
            userInfo.setUserId(roomEngine.getUserId());
        }
        return this.mCurrentUser;
    }

    @Override 
    public void switchCamera() {
        if (this.mCameraType == AUIRoomEngineCameraType.FRONT) {
            this.mCameraType = AUIRoomEngineCameraType.BACK;
        } else {
            this.mCameraType = AUIRoomEngineCameraType.FRONT;
        }
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine == null) {
            return;
        }
        roomEngine.setCameraType(this.mCameraType, null);
    }

    @Override 
    public void toggleMirror() {
        this.mMirror = !this.mMirror;
        if (this.mMainContainer != null) {
            AUIRoomEngine roomEngine = this.mRoomEngine;
            if (roomEngine != null) {
                roomEngine.setRenderViewLayout(roomEngine.getUserId(), null, false, false);
                roomEngine.setRenderViewLayout(roomEngine.getUserId(), this.mMainContainer, false, this.mMirror);
            }
        }
    }

    @Override 
    public void enableBeauty() {
        AliRtcEngine rTCEngine;
        this.mBeautyEnable = true;
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine != null && (rTCEngine = roomEngine.getRTCEngine()) != null) {
            rTCEngine.setBeautyEffect(this.mBeautyEnable, this.mBeautyConfig);
        }
    }

    @Override 
    public void disableBeauty() {
        AliRtcEngine rTCEngine;
        this.mBeautyEnable = false;
        AUIRoomEngine roomEngine = this.mRoomEngine;
        if (roomEngine != null && (rTCEngine = roomEngine.getRTCEngine()) != null) {
            rTCEngine.setBeautyEffect(this.mBeautyEnable, this.mBeautyConfig);
        }
    }

    @Override 
    public void setBeautySkinBlur(float ratio) {
        AUIRoomEngine roomEngine;
        AliRtcEngine rTCEngine;
        this.mBeautyConfig.smoothnessLevel = ratio;
        if (!this.mBeautyEnable || (roomEngine = this.mRoomEngine) == null || (rTCEngine = roomEngine.getRTCEngine()) == null) {
            return;
        }
        rTCEngine.setBeautyEffect(this.mBeautyEnable, this.mBeautyConfig);
    }

    @Override 
    public void setBeautyWhite(float ratio) {
        AUIRoomEngine roomEngine;
        AliRtcEngine rTCEngine;
        this.mBeautyConfig.whiteningLevel = ratio;
        if (!this.mBeautyEnable || (roomEngine = this.mRoomEngine) == null || (rTCEngine = roomEngine.getRTCEngine()) == null) {
            return;
        }
        rTCEngine.setBeautyEffect(this.mBeautyEnable, this.mBeautyConfig);
    }

    @Override 
    public float getBeautySkinBlurLevel() {
        return this.mBeautyConfig.smoothnessLevel;
    }

    @Override 
    public float getBeautyWhiteLevel() {
        return this.mBeautyConfig.whiteningLevel;
    }

    @Override 
    public AUICallNVNState getState() {
        return this.mState;
    }
}
