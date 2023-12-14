package com.aliyun.auikits.auicall;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.aliyun.auikits.auicall.bean.InitialInfo;
import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.fragment.AUICallNVNCreateRoomFragment;
import com.aliyun.auikits.auicall.fragment.AUICallNVNJoinRoomFragment;
import com.aliyun.auikits.auicall.fragment.AUICallNVNOptionFragment;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.model.callback.InitCallback;
import com.aliyun.auikits.auicall.model.callback.AUICallNVNObserver;
import com.aliyun.auikits.auicall.model.callback.CreateRoomCallback;
import com.aliyun.auikits.auicall.model.callback.TokenAccessor;
import com.aliyun.auikits.auicall.model.impl.AUICallNVNModelImpl;
import com.aliyun.auikits.auicall.util.AUICallConfig;
import com.aliyun.auikits.auicall.widget.ConfirmNotitleDialog;
import com.aliyun.auikits.auicall.fragment.BaseFragment;
import com.aliyun.auikits.auicall.fragment.AUICallNVNInviteFragment;
import com.aliyun.auikits.auicall.fragment.AUICallNVNBeCallFragment;
import com.aliyun.auikits.auicall.fragment.AUICallNVNMainFragment;
import com.aliyun.auikits.auicall.fragment.AUICallNVNLoginFragment;
import com.aliyun.auikits.auicall.fragment.AUICallNVNMemberListFragment;
import com.aliyun.auikits.auicall.fragment.UserLoginFragment;

import java.util.List;

public final class AUICallNVNActivity extends BaseActivity implements AUICallNVNObserver, TokenAccessor, AUICallNVNMainFragment.Callback, AUICallNVNMemberListFragment.Callback, UserLoginFragment.Callback, AUICallNVNOptionFragment.Callback, AUICallNVNJoinRoomFragment.Callback, AUICallNVNBeCallFragment.Callback, AUICallNVNInviteFragment.Callback, AUICallNVNCreateRoomFragment.Callback, ConfirmNotitleDialog.Callback {

    private ConfirmNotitleDialog mDialog;

    private AUICallNVNInviteFragment mInviteMemberFragment;

    private AUICallNVNBeCallFragment mMeetingBeCallFragment;

    private AUICallNVNMainFragment mMeetingCallFragment;

    private AUICallNVNModel mMeetingCallModel;

    private AUICallNVNCreateRoomFragment mMeetingCreateRoomFragment;

    private AUICallNVNJoinRoomFragment mMeetingJoinRoomFragment;

    private AUICallNVNMemberListFragment mMeetingMemberListFragment;

    private AUICallNVNOptionFragment mMeetingOptionFragment;
    private boolean mReqCamera;
    private boolean mReqMic;

    private UserLoginFragment mUserLoginFragment;

    @Override 
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(128);
        showMeetingUserInfoFragment();
    }

    private final void showMeetingUserInfoFragment() {
        if (this.mUserLoginFragment == null) {
            AUICallNVNLoginFragment meetingLoginFragment = new AUICallNVNLoginFragment();
            this.mUserLoginFragment = meetingLoginFragment;
            meetingLoginFragment.setCallback(this);
        }
        showFragment(this.mUserLoginFragment);
    }

    public final void showMeetingOptionFragment() {
        if (this.mMeetingOptionFragment == null) {
            AUICallNVNOptionFragment meetingOptionFragment = new AUICallNVNOptionFragment();
            this.mMeetingOptionFragment = meetingOptionFragment;
            meetingOptionFragment.setBizModel(this.mMeetingCallModel);
            AUICallNVNOptionFragment meetingOptionFragment2 = this.mMeetingOptionFragment;
            meetingOptionFragment2.setCallback(this);
        }
        showFragment(this.mMeetingOptionFragment);
    }

    
    @Override 
    public void onDestroy() {
        super.onDestroy();
        release();
        appLogout();
    }

    private final void init(final String userId, final InitCallback callback) {
        onDebugInfo("init start >>>>>>>");
        new Thread(new Runnable() { 
            @Override 
            public final void run() {
                appLogin(userId);
                release();
                AUICallNVNModelImpl meetingCallModelImpl = new AUICallNVNModelImpl();
                mMeetingCallModel = meetingCallModelImpl;
                meetingCallModelImpl.setObserver(AUICallNVNActivity.this);
                AUICallNVNModel meetingCallModel = mMeetingCallModel;
                meetingCallModel.setTokenAccessor(AUICallNVNActivity.this);
                String deviceId = getDeviceId();
                InitialInfo info = new InitialInfo(userId, deviceId, AUICallConfig.APP_ID);
                AUICallNVNModel meetingCallModel2 = mMeetingCallModel;
                Context applicationContext = getApplicationContext();
                meetingCallModel2.init(applicationContext, info, callback);
                onDebugInfo("init end <<<<<<<<");
            }
        }).start();
    }

    private final AUICallNVNMainFragment showAndGetMeetingCallFragment() {
        if (this.mMeetingCallFragment == null) {
            AUICallNVNMainFragment meetingCallFragment = new AUICallNVNMainFragment();
            this.mMeetingCallFragment = meetingCallFragment;
            meetingCallFragment.setCallback(this);
            AUICallNVNMainFragment meetingCallFragment2 = this.mMeetingCallFragment;
            meetingCallFragment2.setBizModel(this.mMeetingCallModel);
        }
        showFragment(this.mMeetingCallFragment);
        return this.mMeetingCallFragment;
    }

    private final AUICallNVNBeCallFragment showAndGetMeetingBeCallFragment() {
        if (this.mMeetingBeCallFragment == null) {
            AUICallNVNBeCallFragment meetingBeCallFragment = new AUICallNVNBeCallFragment();
            this.mMeetingBeCallFragment = meetingBeCallFragment;
            meetingBeCallFragment.setCallback(this);
            AUICallNVNBeCallFragment meetingBeCallFragment2 = this.mMeetingBeCallFragment;
            meetingBeCallFragment2.setBizModel(this.mMeetingCallModel);
        }
        pushBackFragment(this.mMeetingBeCallFragment);
        return this.mMeetingBeCallFragment;
    }

    private final AUICallNVNMemberListFragment showAndGetMeetingMemberFragment() {
        if (this.mMeetingMemberListFragment == null) {
            AUICallNVNMemberListFragment memberListFragment = new AUICallNVNMemberListFragment();
            this.mMeetingMemberListFragment = memberListFragment;
            memberListFragment.setBizModel(this.mMeetingCallModel);
            AUICallNVNMemberListFragment memberListFragment2 = this.mMeetingMemberListFragment;
            memberListFragment2.setCallback(this);
        }
        pushBackFragment(this.mMeetingMemberListFragment);
        return this.mMeetingMemberListFragment;
    }

    private final void release() {
        AUICallNVNModel meetingCallModel = this.mMeetingCallModel;
        if (meetingCallModel != null) {
            if (meetingCallModel != null) {
                meetingCallModel.release();
            }
            this.mMeetingCallModel = null;
        }
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.activity_call;
    }

    @Override 
    public void onAccept( String targetUser) {
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onShow();
    }

    @Override 
    public void onRefuse( String targetUser) {
        Toast.makeText(getApplicationContext(), "已拒绝", Toast.LENGTH_SHORT).show();
        onDebugInfo("onRefuse targetUser[" + targetUser + ']');
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onShow();
    }

    private final void makeSureDialog() {
        if (this.mDialog == null) {
            ConfirmNotitleDialog confirmNotitleDialog = new ConfirmNotitleDialog(this);
            this.mDialog = confirmNotitleDialog;
            confirmNotitleDialog.setCallback(this);
            ConfirmNotitleDialog confirmNotitleDialog2 = this.mDialog;
            confirmNotitleDialog2.setConfirmTxt("同意");
            ConfirmNotitleDialog confirmNotitleDialog3 = this.mDialog;
            confirmNotitleDialog3.setCancelTxt("拒绝");
            ConfirmNotitleDialog confirmNotitleDialog4 = this.mDialog;
            confirmNotitleDialog4.setOnDismissListener(new DialogInterface.OnDismissListener() { 
                @Override 
                public final void onDismiss(DialogInterface dialogInterface) {
                    mReqMic = false;
                    mReqCamera = false;
                }
            });
        }
    }

    @Override 
    public void onRequestMic( String requester, boolean open) {
        if (open) {
            makeSureDialog();
            ConfirmNotitleDialog confirmNotitleDialog = this.mDialog;
            if (confirmNotitleDialog.isShowing()) {
                return;
            }
            this.mReqMic = true;
            ConfirmNotitleDialog confirmNotitleDialog2 = this.mDialog;
            if (confirmNotitleDialog2 != null) {
                confirmNotitleDialog2.setContent("主持人发起语音交流");
            }
            ConfirmNotitleDialog confirmNotitleDialog3 = this.mDialog;
            if (confirmNotitleDialog3 == null) {
                return;
            }
            confirmNotitleDialog3.show();
            return;
        }
        AUICallNVNModel meetingCallModel = this.mMeetingCallModel;
        if (meetingCallModel != null) {
            if (meetingCallModel.getCurrentUser() == null) {
                return;
            }
            AUICallNVNModel meetingCallModel2 = this.mMeetingCallModel;
            AUICallNVNModel meetingCallModel3 = this.mMeetingCallModel;
            UserInfo currentUser = meetingCallModel3.getCurrentUser();
            meetingCallModel2.openMic(currentUser.getUserId(), false);
            Toast.makeText(getApplicationContext(), R.string.host_mute_mic_tips, Toast.LENGTH_SHORT).show();
        }
    }

    @Override 
    public void onRequestCamera( String requester, boolean open) {

        if (open) {
            makeSureDialog();
            ConfirmNotitleDialog confirmNotitleDialog = this.mDialog;

            if (confirmNotitleDialog.isShowing()) {
                return;
            }
            this.mReqCamera = true;
            ConfirmNotitleDialog confirmNotitleDialog2 = this.mDialog;
            if (confirmNotitleDialog2 != null) {
                confirmNotitleDialog2.setContent("主持人发起打开摄像头交流");
            }
            ConfirmNotitleDialog confirmNotitleDialog3 = this.mDialog;
            if (confirmNotitleDialog3 == null) {
                return;
            }
            confirmNotitleDialog3.show();
            return;
        }
        AUICallNVNModel meetingCallModel = this.mMeetingCallModel;
        if (meetingCallModel != null) {

            if (meetingCallModel.getCurrentUser() == null) {
                return;
            }
            AUICallNVNModel meetingCallModel2 = this.mMeetingCallModel;

            AUICallNVNModel meetingCallModel3 = this.mMeetingCallModel;

            UserInfo currentUser = meetingCallModel3.getCurrentUser();

            meetingCallModel2.openCamera(currentUser.getUserId(), false);
            Toast.makeText(getApplicationContext(), R.string.host_mute_camera_tips, Toast.LENGTH_SHORT).show();
        }
    }

    @Override 
    public void onCancel() {
        onDebugInfo("onCancel");
        showMeetingOptionFragment();
        Toast.makeText(getApplicationContext(), "已取消", Toast.LENGTH_SHORT).show();
    }

    @Override 
    public void onSilentCancel() {
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onShow();
    }

    @Override 
    public void onSilentRefuse() {
        showMeetingOptionFragment();
    }

    @Override 
    public void onJoin( String roomId) {

        onDebugInfo("onJoin");
        AUICallNVNMainFragment showAndGetMeetingCallFragment = showAndGetMeetingCallFragment();
        if (showAndGetMeetingCallFragment == null) {
            return;
        }
        showAndGetMeetingCallFragment.onJoin(roomId);
    }

    @Override 
    public void onLeave() {
        onDebugInfo("onLeave");
        AUICallNVNMainFragment meetingCallFragment = this.mMeetingCallFragment;
        if (meetingCallFragment != null) {
            meetingCallFragment.onLeave();
        }
        popBackFragment(this.mMeetingCallFragment);
        pushBackFragment(this.mMeetingOptionFragment);
    }

    @Override 
    public void onUserJoin( String userId) {

        onDebugInfo("onUserJoin userId[" + userId + ']');
        AUICallNVNMainFragment meetingCallFragment = this.mMeetingCallFragment;
        if (meetingCallFragment != null) {
            meetingCallFragment.onUserJoin(userId);
        }
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onMemberChanged(userId);
    }

    @Override 
    public void onUserLeave( String userId) {

        onDebugInfo("onUserLeave userId[" + userId + ']');
        AUICallNVNMainFragment meetingCallFragment = this.mMeetingCallFragment;
        if (meetingCallFragment != null) {
            meetingCallFragment.onUserLeave(userId);
        }
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onMemberChanged(userId);
    }

    @Override 
    public void onCameraOn( String uid) {

        AUICallNVNMainFragment meetingCallFragment = this.mMeetingCallFragment;
        if (meetingCallFragment != null) {
            meetingCallFragment.onCameraOn(uid);
        }
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onMemberChanged(uid);
    }

    @Override 
    public void onCameraOff( String uid) {

        AUICallNVNMainFragment meetingCallFragment = this.mMeetingCallFragment;
        if (meetingCallFragment != null) {
            meetingCallFragment.onCameraOff(uid);
        }
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onMemberChanged(uid);
    }

    @Override 
    public void onMicOn( String uid) {

        AUICallNVNMainFragment meetingCallFragment = this.mMeetingCallFragment;
        if (meetingCallFragment != null) {
            meetingCallFragment.onMicOn(uid);
        }
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onMemberChanged(uid);
    }

    @Override 
    public void onMicOff( String uid) {

        AUICallNVNMainFragment meetingCallFragment = this.mMeetingCallFragment;
        if (meetingCallFragment != null) {
            meetingCallFragment.onMicOff(uid);
        }
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onMemberChanged(uid);
    }

    @Override 
    public void onInvite( String inviter,  String roomId) {


        onDebugInfo("onInvite inviter[" + inviter + "] roomId[" + roomId + ']');
        AUICallNVNBeCallFragment showAndGetMeetingBeCallFragment = showAndGetMeetingBeCallFragment();
        if (showAndGetMeetingBeCallFragment == null) {
            return;
        }
        showAndGetMeetingBeCallFragment.onInvite(inviter, roomId);
    }

    @Override 
    public void onDebugInfo( String info) {

        addDebugInfo(info);
    }

    @Override 
    public void onError(int errCode,  String errMsg) {
        AUICallNVNModel meetingCallModel;
        addDebugInfo("onError code[" + errCode + "] msg[" + ((Object) errMsg) + ']');
        if (errCode == AUICallConfig.ERROR_HEART_BEAT_TIMEOUT && (meetingCallModel = this.mMeetingCallModel) != null) {
            meetingCallModel.leave();
        }
        Toast.makeText(getApplicationContext(), errMsg == null ? "" : errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override 
    public void onKickOut() {
        onDebugInfo("onKickOut");
        Toast.makeText(getApplicationContext(), R.string.being_kickout_tips, Toast.LENGTH_SHORT).show();
    }

    @Override 
    public void onHostMute(boolean mute) {
        if (mute) {
            Toast.makeText(getApplicationContext(), R.string.host_mute_tips, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.host_unmute_tips, Toast.LENGTH_SHORT).show();
        }
    }

    @Override 
    public void onUpdateHost( UserInfo host) {

        AUICallNVNMainFragment meetingCallFragment = this.mMeetingCallFragment;
        if (meetingCallFragment != null) {
            meetingCallFragment.onUpdateHost(host);
        }
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onShow();
    }

    @Override 
    public void onShowMemberList() {
        AUICallNVNMemberListFragment showAndGetMeetingMemberFragment = showAndGetMeetingMemberFragment();
        if (showAndGetMeetingMemberFragment == null) {
            return;
        }
        showAndGetMeetingMemberFragment.onShow();
    }

    public final void showInviteMemberFragment() {
        if (this.mInviteMemberFragment == null) {
            AUICallNVNInviteFragment inviteMemberFragment = new AUICallNVNInviteFragment();
            this.mInviteMemberFragment = inviteMemberFragment;

            inviteMemberFragment.setCallback(this);
            AUICallNVNInviteFragment inviteMemberFragment2 = this.mInviteMemberFragment;

            inviteMemberFragment2.setBizModel(this.mMeetingCallModel);
        }
        pushBackFragment(this.mInviteMemberFragment);
    }

    @Override 
    public void onInviteBtnClick() {
        showInviteMemberFragment();
    }

    @Override 
    public void onActionCancelInvite( String userId) {

        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onShow();
    }

    @Override 
    public void onActionLogin( String userId) {
        init(userId, new InitCallback() {
            @Override
            public void onResult(int code, String msg) {
                UserLoginFragment userLoginFragment;

                onDebugInfo("init result code[" + code + "] msg[" + msg + ']');
                if (code == 0) {
                    showMeetingOptionFragment();
                    return;
                }
                userLoginFragment = mUserLoginFragment;
                if (userLoginFragment == null) {
                    return;
                }
                userLoginFragment.onLoginFail();
            }
        });
    }

    @Override 
    public void onUserLoginBackPress() {
        onBackPressed();
    }

    
    public final void makeSureCreateRoomFragment() {
        if (this.mMeetingCreateRoomFragment == null) {
            AUICallNVNCreateRoomFragment meetingCreateRoomFragment = new AUICallNVNCreateRoomFragment();
            this.mMeetingCreateRoomFragment = meetingCreateRoomFragment;

            meetingCreateRoomFragment.setBizModel(this.mMeetingCallModel);
            AUICallNVNCreateRoomFragment meetingCreateRoomFragment2 = this.mMeetingCreateRoomFragment;
            if (meetingCreateRoomFragment2 == null) {
                return;
            }
            meetingCreateRoomFragment2.setCallback(this);
        }
    }

    @Override 
    public void onActionCreateRoom() {
        AUICallNVNModel meetingCallModel = this.mMeetingCallModel;
        if (meetingCallModel == null) {
            return;
        }
        makeSureCreateRoomFragment();
        mMeetingCreateRoomFragment.resetState();
        pushBackFragment(mMeetingCreateRoomFragment);
    }

    @Override 
    public void onActionJoinRoom() {
        if (this.mMeetingJoinRoomFragment == null) {
            AUICallNVNJoinRoomFragment meetingJoinRoomFragment = new AUICallNVNJoinRoomFragment();
            this.mMeetingJoinRoomFragment = meetingJoinRoomFragment;

            meetingJoinRoomFragment.setCallback(this);
        }
        AUICallNVNJoinRoomFragment meetingJoinRoomFragment2 = this.mMeetingJoinRoomFragment;
        if (meetingJoinRoomFragment2 != null) {
            meetingJoinRoomFragment2.resetState();
        }
        AUICallNVNJoinRoomFragment meetingJoinRoomFragment3 = this.mMeetingJoinRoomFragment;
        if (meetingJoinRoomFragment3 == null) {
            throw new NullPointerException("null cannot be cast to non-null type androidx.fragment.app.Fragment");
        }
        pushBackFragment(meetingJoinRoomFragment3);
    }

    @Override 
    public void onMeetingOptionBackPress() {
        onBackPressed();
    }

    @Override 
    public void onActionJoinRoom( String roomId, boolean cameraOn, boolean micOn) {

        AUICallNVNModel meetingCallModel = this.mMeetingCallModel;
        if (meetingCallModel == null) {
            return;
        }
        meetingCallModel.join(roomId, cameraOn, micOn);
    }

    @Override 
    public void onActionJoinRoomLoudspeaker(boolean on) {
    }

    @Override
    public void onCreateRoomFailed(int code, String msg) {
        Toast.makeText(this.getApplicationContext(), "创建房间失败", Toast.LENGTH_SHORT).show();
        this.onDebugInfo("create room failed!!! code[" + code + "] msg[" + ((Object) msg) + ']');
    }

    @Override 
    public void onActionRefuse() {
        Toast.makeText(getApplicationContext(), "已拒绝", Toast.LENGTH_SHORT).show();
        popBackFragment();
    }

    @Override 
    public void onActionInvite( String userId) {

        popBackFragment();
        AUICallNVNMemberListFragment memberListFragment = this.mMeetingMemberListFragment;
        if (memberListFragment == null) {
            return;
        }
        memberListFragment.onShow();
    }

    @Override 
    public void onBackPressed() {
        UserLoginFragment userLoginFragment = this.mUserLoginFragment;
        if (userLoginFragment != null) {

            if (userLoginFragment.isAdded()) {
                UserLoginFragment userLoginFragment2 = this.mUserLoginFragment;

                if (userLoginFragment2.isLogining()) {
                    return;
                }
            }
        }
        if (getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();

            Fragment fragment = fragments.get(fragments.size()-1);
            if (fragment != null && fragment == this.mMeetingOptionFragment && fragment.isAdded()) {
                super.onBackPressed();
                return;
            } else if (fragment != null && (fragment instanceof BaseFragment)) {
                ((BaseFragment) fragment).onBackPressed();
                return;
            } else {
                return;
            }
        }
        super.onBackPressed();
    }

    @Override 
    public void onActionConfirm( Dialog dialog) {

        AUICallNVNModel meetingCallModel = this.mMeetingCallModel;
        if (meetingCallModel != null) {

            if (meetingCallModel.getCurrentUser() != null) {
                if (this.mReqMic) {
                    AUICallNVNModel meetingCallModel2 = this.mMeetingCallModel;

                    AUICallNVNModel meetingCallModel3 = this.mMeetingCallModel;

                    UserInfo currentUser = meetingCallModel3.getCurrentUser();

                    meetingCallModel2.openMic(currentUser.getUserId(), true);
                }
                if (this.mReqCamera) {
                    AUICallNVNModel meetingCallModel4 = this.mMeetingCallModel;

                    AUICallNVNModel meetingCallModel5 = this.mMeetingCallModel;

                    UserInfo currentUser2 = meetingCallModel5.getCurrentUser();

                    meetingCallModel4.openCamera(currentUser2.getUserId(), true);
                }
            }
        }
        ConfirmNotitleDialog confirmNotitleDialog = this.mDialog;
        if (confirmNotitleDialog == null) {
            return;
        }
        confirmNotitleDialog.dismiss();
    }

    @Override 
    public void onActionCancel( Dialog dialog) {

        ConfirmNotitleDialog confirmNotitleDialog = this.mDialog;
        if (confirmNotitleDialog == null) {
            return;
        }
        confirmNotitleDialog.dismiss();
    }
}
