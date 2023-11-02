package com.aliyun.auikits.auicall;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.aliyun.auikits.auicall.bean.InitialInfo;
import com.aliyun.auikits.auicall.bean.AUICall1V1Mode;
import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.model.callback.AUICall1V1Observer;
import com.aliyun.auikits.auicall.model.callback.InitCallback;
import com.aliyun.auikits.auicall.model.impl.AUICall1V1ModelImpl;
import com.aliyun.auikits.auicall.util.AUICallConfig;
import com.aliyun.auikits.auicall.fragment.AUICall1V1MainFragment;
import com.aliyun.auikits.auicall.fragment.AUICall1V1MakeCallFragment;
import com.aliyun.auikits.auicall.fragment.AUICall1V1LoginFragment;
import com.aliyun.auikits.auicall.fragment.UserLoginFragment;
public final class AUICall1V1Activity extends BaseActivity implements UserLoginFragment.Callback, AUICall1V1MakeCallFragment.Callback, AUICall1V1MainFragment.Callback, AUICall1V1Observer {

    private AUICall1V1Model mCallModel;

    private AUICall1V1MainFragment mCallingFragment;

    private AUICall1V1MakeCallFragment mMakeCallFragment;

    private UserLoginFragment mUserLoginFragment;

    @Override 
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(128);
        showUserInfoFragment();
    }

    private final void showUserInfoFragment() {
        if (this.mUserLoginFragment == null) {
            AUICall1V1LoginFragment singleLoginFragment = new AUICall1V1LoginFragment();
            this.mUserLoginFragment = singleLoginFragment;

            singleLoginFragment.setCallback(this);
        }
        showFragment(this.mUserLoginFragment);
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
                AUICall1V1ModelImpl callModelImpl = new AUICall1V1ModelImpl();
                mCallModel = callModelImpl;

                callModelImpl.setCallObserver(AUICall1V1Activity.this);
                AUICall1V1Model callModel = mCallModel;

                callModel.setTokenAccessor(AUICall1V1Activity.this);
                String deviceId = getDeviceId();

                InitialInfo info = new InitialInfo(userId, deviceId, AUICallConfig.APP_ID, AUICallConfig.APP_GROUP);
                AUICall1V1Model callModel2 = mCallModel;

                Context applicationContext = getApplicationContext();

                callModel2.init(applicationContext, info, callback);
                onDebugInfo("init end <<<<<<<<");
            }
        }).start();
    }

    private final AUICall1V1MainFragment showAndGetCallingFragment() {
        if (this.mCallingFragment == null) {
            AUICall1V1MainFragment callingFragment = new AUICall1V1MainFragment();
            this.mCallingFragment = callingFragment;

            callingFragment.setDebugInfoOutput(this);
            AUICall1V1MainFragment callingFragment2 = this.mCallingFragment;

            callingFragment2.setBizCallModel(this.mCallModel);
            AUICall1V1MainFragment callingFragment3 = this.mCallingFragment;

            callingFragment3.setCallBack(this);
        }
        showFragment(this.mCallingFragment);
        return this.mCallingFragment;
    }

    public final void hideCallingFragment() {
        popBackFragment(this.mCallingFragment);
    }

    private final void release() {
        AUICall1V1Model callModel = this.mCallModel;
        if (callModel != null) {
            if (callModel != null) {
                callModel.release();
            }
            AUICall1V1MainFragment callingFragment = this.mCallingFragment;
            if (callingFragment != null) {
                callingFragment.setDebugInfoOutput(null);
            }
            this.mCallModel = null;
        }
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.activity_call;
    }

    @Override 
    public void onCall( String caller) {

        onDebugInfo("czwxxx: onCall caller[" + caller + ']');
        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.onCall(caller);
    }

    @Override 
    public void onAccept( String targetUser) {

        onDebugInfo("czwxxx: onAccept targetUser[" + targetUser + ']');
        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.onAccept(targetUser);
    }

    @Override 
    public void onRefuse( String targetUser) {

        Toast.makeText(getApplicationContext(), "已拒绝", Toast.LENGTH_SHORT).show();
        onDebugInfo("czwxxx: onRefuse targetUser[" + targetUser + ']');
        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.onRefuse(targetUser);
    }

    @Override 
    public void onSilentRefuse() {
        onDebugInfo("czwxxx: onSilentRefuse");
        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.onSilentRefuse();
    }

    @Override 
    public void onHangup() {
        onDebugInfo("czwxxx: onHangup");
        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment != null) {
            showAndGetCallingFragment.onHangup();
        }
        hideCallingFragment();
        pushBackFragment(this.mMakeCallFragment);
    }

    @Override 
    public void onCancel() {
        onDebugInfo("czwxxx: onCancel");
        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment != null) {
            showAndGetCallingFragment.onCancel();
        }
        hideCallingFragment();
        pushBackFragment(this.mMakeCallFragment);
    }

    @Override 
    public void onCameraOn( String uid) {

        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.onCameraOn(uid);
    }

    @Override 
    public void onCameraOff( String uid) {

        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.onCameraOff(uid);
    }

    @Override 
    public void onMicOn( String uid) {

        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.onMicOn(uid);
    }

    @Override 
    public void onMicOff( String uid) {

        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.onMicOff(uid);
    }

    @Override 
    public void onSwitchToAudioMode() {
        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.onSwitchToAudioMode();
    }

    @Override 
    public void onDebugInfo( String info) {

        addDebugInfo(info);
    }

    @Override 
    public void onError(int errCode,  String errMsg) {
        AUICall1V1Model callModel;
        addDebugInfo("onError code[" + errCode + "] msg[" + ((Object) errMsg) + ']');
        if (errCode != AUICallConfig.ERROR_HEART_BEAT_TIMEOUT || (callModel = this.mCallModel) == null) {
            return;
        }
        callModel.hangup(true);
    }

    @Override 
    public void onActionLogin( String userId) {

        init(userId, new InitCallback() {
            @Override
            public void onResult(int code, String msg) {
                UserLoginFragment userLoginFragment;
                AUICall1V1MakeCallFragment makeCallFragment;
                AUICall1V1MakeCallFragment makeCallFragment2;
                AUICall1V1MakeCallFragment makeCallFragment3;
                onDebugInfo("init result code[" + code + "] msg[" + msg + ']');
                if (code == 0) {
                    makeCallFragment = mMakeCallFragment;
                    if (makeCallFragment == null) {
                        mMakeCallFragment = new AUICall1V1MakeCallFragment();
                        makeCallFragment3 = mMakeCallFragment;
                        makeCallFragment3.setCallback(AUICall1V1Activity.this);
                    }
                    AUICall1V1Activity singleCallActivity = AUICall1V1Activity.this;
                    makeCallFragment2 = singleCallActivity.mMakeCallFragment;
                    singleCallActivity.showFragment(makeCallFragment2);
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

    @Override 
    public void onActionMakeCall( String userId,  AUICall1V1Mode mode) {
        AUICall1V1MainFragment showAndGetCallingFragment = showAndGetCallingFragment();
        if (showAndGetCallingFragment == null) {
            return;
        }
        showAndGetCallingFragment.makeCall(userId, mode);
    }

    @Override 
    public void onMakeCallBackPress() {
        onBackPressed();
    }

    @Override 
    public void onCallEnd() {
        showFragment(this.mMakeCallFragment);
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
        super.onBackPressed();
    }
}
