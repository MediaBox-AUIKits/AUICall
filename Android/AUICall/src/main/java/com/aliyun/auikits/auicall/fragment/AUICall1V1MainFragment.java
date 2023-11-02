package com.aliyun.auikits.auicall.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.aliyun.auikits.auicall.bean.AUICall1V1Mode;
import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.bean.AUICall1V1State;
import com.aliyun.auikits.auicall.bean.CameraType;
import com.aliyun.auikits.auicall.model.callback.AUICall1V1Observer;
import com.aliyun.auikits.auicall.controller.single.BaseBeCalledPanelController;
import com.aliyun.auikits.auicall.controller.single.BaseCallPanelController;
import com.aliyun.auikits.auicall.controller.single.BaseOnCallPanelController;
import com.aliyun.auikits.auicall.controller.single.BeCalledActionCallback;
import com.aliyun.auikits.auicall.controller.single.BeCalledAudioPanelController;
import com.aliyun.auikits.auicall.controller.single.BeCalledVideoPanelController;
import com.aliyun.auikits.auicall.controller.single.CallerActionCallback;
import com.aliyun.auikits.auicall.controller.single.CallerAudioPanelController;
import com.aliyun.auikits.auicall.controller.single.CallerVideoPanelController;
import com.aliyun.auikits.auicall.controller.single.OnCallAudioPanelController;
import com.aliyun.auikits.auicall.controller.single.OnCallVideoPanelController;
import com.aliyun.auikits.auicall.controller.meeting.VideoBeCallActionCallback;
import com.aliyun.auikits.auicall.controller.meeting.VideoCallerActionCallback;
import com.aliyun.auikits.auicall.controller.meeting.VideoOnCallActionCallback;
import com.aliyun.auikits.auicall.widget.LoudSpeakerWidget;
import com.aliyun.auikits.auicall.widget.VoiceMuteWidget;
import com.aliyun.auikits.auicall.R;

public final class AUICall1V1MainFragment extends Fragment implements AUICall1V1Observer {
    private static final int BIG_MODE = 1;
    private static final int SMALL_MODE = 0;

    private AUICall1V1Model bizCallModel;

    private Callback callBack;

    private AUICall1V1Observer debugInfoOutput;

    private BeCalledAudioPanelController mBeCalledAudioPanel;

    private BeCalledVideoPanelController mBeCalledVideoPanel;

    private CallerAudioPanelController mCallerAudioPanel;
    private TextView mCallerName;

    private CallerVideoPanelController mCallerVideoPanel;

    private CameraType mCameraType = CameraType.FRONT;

    private View mFragmentContentView;
    private View mMainContainer;

    private OnCallAudioPanelController mOnCallAudioPanel;

    private OnCallVideoPanelController mOnCallVideoPanel;
    private ViewGroup mPanelContainer;
    private int mPreviewMode;
    private View mRemotePreviewContainer;
    private View mRemotePreviewMask;
    private View mSmallContainer;
    private View mSwitchCamera;
    private ImageView mTargetUserIcon;
    private TextView mTips;

    public interface Callback {
        void onCallEnd();
    }

    public final AUICall1V1Model getBizCallModel() {
        return this.bizCallModel;
    }

    public final void setBizCallModel( AUICall1V1Model callModel) {
        this.bizCallModel = callModel;
    }

    public final AUICall1V1Observer getDebugInfoOutput() {
        return this.debugInfoOutput;
    }

    public final void setDebugInfoOutput( AUICall1V1Observer callObserver) {
        this.debugInfoOutput = callObserver;
    }

    public final Callback getCallBack() {
        return this.callBack;
    }

    public final void setCallBack( Callback callback) {
        this.callBack = callback;
    }

    @Override 
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        if (this.mFragmentContentView == null) {
            this.mFragmentContentView = getLayoutInflater().inflate(R.layout.fragment_calling, container, false);
            this.mMainContainer = mFragmentContentView.findViewById(R.id.main_container);
            this.mSmallContainer = mFragmentContentView.findViewById(R.id.small_container);
            this.mPanelContainer = mFragmentContentView.findViewById(R.id.bottom_panel);
            this.mTargetUserIcon = mFragmentContentView.findViewById(R.id.opposite_user_icon);
            this.mCallerName = mFragmentContentView.findViewById(R.id.opposite_user_name);
            this.mTips = mFragmentContentView.findViewById(R.id.call_tips);
            this.mRemotePreviewContainer = mFragmentContentView.findViewById(R.id.remote_preview_container);
            this.mRemotePreviewMask = mFragmentContentView.findViewById(R.id.remote_preview_mask);
            this.mSwitchCamera = mFragmentContentView.findViewById(R.id.switch_camera);
            KeyEvent.Callback callback = null;
            mMainContainer.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public final void onClick(View view10) {
                    toggleOperationsVisibility();
                }
            });
            mFragmentContentView.findViewById(R.id.bg_mask).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            mSwitchCamera.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public final void onClick(View view12) {
                    switchCamera();
                }
            });
            mSmallContainer.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public final void onClick(View view13) {
                    swapPreview();
                }
            });
            bizCallModel.setViewContainer((ViewGroup) mMainContainer, (ViewGroup) mSmallContainer);
        }
        return this.mFragmentContentView;
    }

    public void toggleOperationsVisibility() {
        BaseOnCallPanelController onCallPanel = getOnCallPanel();
        if (onCallPanel == null) {
            return;
        }
        onCallPanel.toggleOperationsVisibility();
    }

    public void switchCamera() {
        if (mCameraType == CameraType.FRONT) {
            mCameraType = CameraType.BACK;
        } else {
            mCameraType = CameraType.FRONT;
        }
        if (bizCallModel == null) {
            return;
        }
        bizCallModel.setCameraType(mCameraType);
    }

    public void swapPreview() {
        if (mPreviewMode == 0) {
            mPreviewMode = 1;
        } else {
            mPreviewMode = 0;
        }
        if (bizCallModel == null) {
            return;
        }
        bizCallModel.swapPreviewView();
    }

    public final void makeCall( String targetUser, AUICall1V1Mode mode) {
        if(TextUtils.isEmpty(targetUser) || bizCallModel == null) return;
        resetAllPanelsState();
        bizCallModel.call(targetUser, mode);
        ViewGroup viewGroup = null;
        if (bizCallModel.getMode() == AUICall1V1Mode.Video) {
            mSwitchCamera.setVisibility(View.VISIBLE);
            mMainContainer.setVisibility(View.VISIBLE);
        } else {
            mSwitchCamera.setVisibility(View.GONE);
            mMainContainer.setVisibility(View.INVISIBLE);
        }
        mCallerName.setVisibility(View.VISIBLE);
        mTargetUserIcon.setVisibility(View.VISIBLE);
        mCallerName.setText(targetUser);
        mTips.setText("正在等待对方接受邀请");
        mRemotePreviewContainer.setVisibility(View.INVISIBLE);
        if (getCallerPanel() == null) {
            return;
        }
        getCallerPanel().addToContainer(mPanelContainer);
    }

    private final void resetAllPanelsState() {
        if (mCallerAudioPanel != null) {
            mCallerAudioPanel.resetState();
        }
        if (mCallerVideoPanel != null) {
            mCallerVideoPanel.resetState();
        }
        if (mBeCalledAudioPanel != null) {
            mBeCalledAudioPanel.resetState();
        }
        if (mBeCalledVideoPanel != null) {
            mBeCalledVideoPanel.resetState();
        }
        if (mOnCallAudioPanel != null) {
            mOnCallAudioPanel.resetState();
        }
        if (mOnCallVideoPanel != null) {
            mOnCallVideoPanel.resetState();
        }
    }

    public final BaseCallPanelController getCallerPanel() {
        if(bizCallModel == null) return null;
        ViewGroup viewGroup = null;
        if (this.mCallerAudioPanel == null) {
            if (bizCallModel.getMode() == AUICall1V1Mode.Audio) {
                this.mCallerAudioPanel = new CallerAudioPanelController(bizCallModel, new CallerActionCallback() {
                    @Override 
                    public void onCallerHangup() {
                        AUICall1V1MainFragment.this.addDebugInfo("caller hangup");
                        AUICall1V1MainFragment.this.onCallEnd();
                    }

                    @Override 
                    public void onCallerMuteMic(boolean mute) {
                        AUICall1V1MainFragment.this.addDebugInfo("caller mute mic " + mute);
                    }

                    @Override 
                    public void onCallerOpenLoudspeaker(boolean open) {
                        AUICall1V1MainFragment.this.addDebugInfo("caller switch loudspeaker " + open);
                    }
                });
                Context applicationContext = requireActivity().getApplicationContext();
                mCallerAudioPanel.inflate(applicationContext, mPanelContainer);
            }
        }
        if (this.mCallerVideoPanel == null) {
            if (bizCallModel.getMode() == AUICall1V1Mode.Video) {
                this.mCallerVideoPanel = new CallerVideoPanelController(bizCallModel, new VideoCallerActionCallback() {
                    @Override 
                    public void onSwitchToAudio() {
                        mSwitchCamera.setVisibility(View.GONE);
                        if (getCallerPanel() != null) {
                            getCallerPanel().addToContainer(mPanelContainer);
                        }
                    }

                    @Override 
                    public void onCancelVideoCall() {
                        AUICall1V1MainFragment.this.onCallEnd();
                    }
                });
                Context applicationContext2 = requireActivity().getApplicationContext();
                mCallerVideoPanel.inflate(applicationContext2, mPanelContainer);
            }
        }
        return bizCallModel.getMode() == AUICall1V1Mode.Audio ? mCallerAudioPanel : mCallerVideoPanel;
    }

    public final BaseBeCalledPanelController getBeCallPanel() {
        if(bizCallModel == null) return null;
        if (this.mBeCalledAudioPanel == null && bizCallModel.getMode() == AUICall1V1Mode.Audio) {
            this.mBeCalledAudioPanel = new BeCalledAudioPanelController(bizCallModel, new BeCalledActionCallback() {
                @Override 
                public void onHangup() {
                    Toast.makeText(AUICall1V1MainFragment.this.requireActivity().getApplicationContext(), "已拒绝", Toast.LENGTH_SHORT).show();
                    AUICall1V1MainFragment.this.onCallEnd();
                }

                @Override 
                public void onAccept() {
                    AUICall1V1MainFragment.this.onCallStart();
                    AUICall1V1MainFragment.this.syncPanelState(1);
                }
            });
            Context applicationContext = requireActivity().getApplicationContext();
            mBeCalledAudioPanel.inflate(applicationContext, mPanelContainer);
        }
        if (this.mBeCalledVideoPanel == null && bizCallModel.getMode() == AUICall1V1Mode.Video) {
            mBeCalledVideoPanel = new BeCalledVideoPanelController(bizCallModel, new VideoBeCallActionCallback() {
                @Override 
                public void onVideoHangup() {
                    AUICall1V1MainFragment.this.onCallEnd();
                }

                @Override 
                public void onVideoHangon() {
                    AUICall1V1MainFragment.this.onCallStart();
                    AUICall1V1MainFragment.this.syncPanelState(1);
                }

                @Override 
                public void onSwitchToAudio() {
                    mSwitchCamera.setVisibility(View.GONE);
                    getBeCallPanel().setCallerId(bizCallModel == null ? null : bizCallModel.getOppositeUser());
                    getBeCallPanel().addToContainer(mPanelContainer);
                }
            });
            Context applicationContext2 = requireActivity().getApplicationContext();
            mBeCalledVideoPanel.inflate(applicationContext2, mPanelContainer);
        }
        return bizCallModel.getMode() == AUICall1V1Mode.Audio ? mBeCalledAudioPanel : mBeCalledVideoPanel;
    }

    private final void onHangupUI() {
        mPanelContainer.removeAllViews();
        this.mTargetUserIcon.setImageResource(R.mipmap.ic_launcher);
        this.mCallerName.setText("");
        this.mTips.setText("");
    }

    private final BaseOnCallPanelController getOnCallPanel() {
        if(bizCallModel == null) return null;
        if (this.mOnCallAudioPanel == null && bizCallModel.getMode() == AUICall1V1Mode.Audio) {
            this.mOnCallAudioPanel = new OnCallAudioPanelController(bizCallModel);
            Context applicationContext = requireActivity().getApplicationContext();
            mOnCallAudioPanel.inflate(applicationContext, mPanelContainer);
        }
        if (this.mOnCallVideoPanel == null && bizCallModel.getMode() == AUICall1V1Mode.Video) {
            mOnCallVideoPanel = new OnCallVideoPanelController(bizCallModel, new VideoOnCallActionCallback() {
                @Override 
                public void onVideoOnCallMuteVoice(boolean muted) {
                }

                @Override 
                public void onVideoOnCallSpeakerMute(boolean muted) {
                }

                @Override 
                public void onVideoOnCallCameraMute(boolean muted) {
                }

                @Override 
                public void onVideoOnCallHangup() {
                    AUICall1V1MainFragment.this.onCallEnd();
                }

                @Override 
                public void onVideoOnCallSwitchToAudio() {
                    mSwitchCamera.setVisibility(View.GONE);
                    AUICall1V1MainFragment.this.onCallStart();
                    AUICall1V1MainFragment.this.syncPanelState(1);
                }
            });
            Context applicationContext2 = requireActivity().getApplicationContext();
            mOnCallVideoPanel.inflate(applicationContext2, mPanelContainer);
        }
        return bizCallModel.getMode() == AUICall1V1Mode.Audio ? mOnCallAudioPanel : mOnCallVideoPanel;
    }

    public final void syncPanelState(int type) {
        Long startTime = mOnCallVideoPanel == null ? null : mOnCallVideoPanel.getRecordStartTime();
        if (mOnCallAudioPanel != null) {
            mOnCallAudioPanel.updateRecordStartTime(startTime);
        }
        if (type == 2 && this.mCallerAudioPanel != null && mOnCallAudioPanel != null) {
            VoiceMuteWidget voiceMuteBtn = mOnCallAudioPanel.getVoiceMuteBtn();
            voiceMuteBtn.setState(mCallerAudioPanel.getVoiceMuteBtn().getState());
            mOnCallAudioPanel.setVoiceMuted(mCallerAudioPanel.getVoiceMuted());
            LoudSpeakerWidget loudSpeakerBtn = mOnCallAudioPanel.getLoudSpeakerBtn();
            loudSpeakerBtn.setState(mCallerAudioPanel.getLoudspeakerBtn().getState());
            mOnCallAudioPanel.setLoudspeakerOn(mCallerAudioPanel.getLoudspeakerOn());
        } else if (type == 1 && this.mOnCallVideoPanel != null && this.mOnCallAudioPanel != null && getOnCallPanel() == mOnCallAudioPanel) {
            VoiceMuteWidget voiceMuteBtn2 = mOnCallAudioPanel.getVoiceMuteBtn();
            voiceMuteBtn2.setState(mOnCallVideoPanel.getVoiceMuteBtn().getState());
            mOnCallAudioPanel.setVoiceMuted(mOnCallVideoPanel.getVoiceMuted());
            LoudSpeakerWidget loudSpeakerBtn2 = mOnCallAudioPanel.getLoudSpeakerBtn();
            loudSpeakerBtn2.setState(mOnCallVideoPanel.getLoudSpeakerBtn().getState());
            mOnCallAudioPanel.getLoudspeakerOn();
            mOnCallVideoPanel.getLoudSpeakerOn();
        }
    }

    public final void onCallStart() {
        if(bizCallModel == null) return;
        showOnCallPanel();
        mTips.setText("");
        if (bizCallModel.getMode() == AUICall1V1Mode.Audio) {
            this.mTargetUserIcon.setVisibility(View.VISIBLE);
            this.mCallerName.setVisibility(View.VISIBLE);
            this.mCallerName.setText(bizCallModel.getOppositeUser());
            this.mRemotePreviewContainer.setVisibility(View.INVISIBLE);
        }else{
            this.mTargetUserIcon.setVisibility(View.INVISIBLE);
            this.mCallerName.setVisibility(View.INVISIBLE);
            this.mRemotePreviewContainer.setVisibility(View.VISIBLE);
        }
    }

    public final void onCallEnd() {
        hideOnCallPanel();
        onHangupUI();
        if (this.callBack == null) {
            return;
        }
        callBack.onCallEnd();
    }

    @Override 
    public void onCall( String caller) {
        if(bizCallModel == null) return;
        resetAllPanelsState();
        BaseBeCalledPanelController beCallPanel = getBeCallPanel();
        if (beCallPanel != null) {
            beCallPanel.setCallerId(caller);
            beCallPanel.addToContainer(mPanelContainer);
        }
        this.mCallerName.setVisibility(View.VISIBLE);
        this.mCallerName.setText(caller);
        this.mTargetUserIcon.setVisibility(View.VISIBLE);
        this.mRemotePreviewContainer.setVisibility(View.INVISIBLE);
        if (bizCallModel.getMode() == AUICall1V1Mode.Audio) {
            this.mSwitchCamera.setVisibility(View.GONE);
            this.mTips.setText("邀请你语音通话");
            this.mMainContainer.setVisibility(View.INVISIBLE);
        }else{
            this.mSwitchCamera.setVisibility(View.VISIBLE);
            this.mTips.setText("邀请你视频通话");
            this.mMainContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override 
    public void onAccept( String targetUser) {
        onCallStart();
        syncPanelState(2);
    }

    @Override 
    public void onRefuse( String targetUser) {
        Toast.makeText(requireActivity().getApplicationContext(), "对方已拒绝", Toast.LENGTH_SHORT).show();
        onCallEnd();
    }

    @Override 
    public void onHangup() {
        Toast.makeText(requireActivity().getApplicationContext(), "通话结束", Toast.LENGTH_SHORT).show();
        onCallEnd();
    }

    @Override 
    public void onCancel() {
        Toast.makeText(requireActivity().getApplicationContext(), "通话取消", Toast.LENGTH_SHORT).show();
        onCallEnd();
    }

    @Override 
    public void onSilentRefuse() {
        onCallEnd();
    }

    @Override 
    public void onCameraOn( String uid) {
        if(bizCallModel == null || TextUtils.isEmpty(uid)) return;
        if (uid.equals(bizCallModel.getOppositeUser())) {
            if (this.mPreviewMode == SMALL_MODE) {
                this.mTargetUserIcon.setVisibility(View.INVISIBLE);
                this.mMainContainer.setVisibility(View.VISIBLE);
            }else{
                this.mRemotePreviewMask.setVisibility(View.INVISIBLE);
            }
        } else if (uid.equals(bizCallModel.getCurrentUser())) {
            this.mSwitchCamera.setVisibility(View.VISIBLE);
            if (this.mPreviewMode == SMALL_MODE) {
                this.mRemotePreviewMask.setVisibility(View.INVISIBLE);
            }else{
                this.mTargetUserIcon.setVisibility(View.INVISIBLE);
                this.mMainContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override 
    public void onCameraOff( String uid) {
        if(bizCallModel == null || TextUtils.isEmpty(uid)) return;
        if (uid.equals(bizCallModel.getOppositeUser())) {
            if (this.mPreviewMode == SMALL_MODE) {
                this.mTargetUserIcon.setVisibility(View.VISIBLE);
                this.mMainContainer.setVisibility(View.INVISIBLE);
            }else{
                this.mRemotePreviewMask.setVisibility(View.VISIBLE);
            }
        }else if (uid.equals(bizCallModel.getCurrentUser())) {
            this.mSwitchCamera.setVisibility(View.GONE);
            if (this.mPreviewMode == SMALL_MODE) {
                this.mRemotePreviewMask.setVisibility(View.VISIBLE);
                return;
            }else{
                this.mTargetUserIcon.setVisibility(View.VISIBLE);
                this.mMainContainer.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override 
    public void onMicOn( String uid) {
    }

    @Override 
    public void onMicOff( String uid) {
    }

    private final void showOnCallPanel() {
        BaseOnCallPanelController onCallPanel = getOnCallPanel();
        if (onCallPanel != null) {
            onCallPanel.addToContainer(mPanelContainer);
            onCallPanel.startTimeRecord();
        }
    }

    private final void hideOnCallPanel() {
        BaseOnCallPanelController onCallPanel = getOnCallPanel();
        if (onCallPanel != null) {
            onCallPanel.removeFromContainer(mPanelContainer);
            onCallPanel.stopTimeRecord();
        }
    }

    @Override 
    public void onSwitchToAudioMode() {
        addDebugInfo("onSwitchToAudioMode");
        if (this.bizCallModel == null) {
            return;
        }
        this.mSwitchCamera.setVisibility(View.GONE);
        if (bizCallModel.getState() == AUICall1V1State.WaitAnswer) {
            BaseBeCalledPanelController beCallPanel = getBeCallPanel();
            if (beCallPanel != null) {
                beCallPanel.setCallerId(bizCallModel == null ? null : bizCallModel.getOppositeUser());
                beCallPanel.addToContainer(mPanelContainer);
            }
        } else if (bizCallModel.getState() == AUICall1V1State.Accepted) {
            onCallStart();
            syncPanelState(1);
        } else if (bizCallModel.getState() == AUICall1V1State.Online) {
            onCallStart();
            syncPanelState(1);
        } else {
            BaseCallPanelController callerPanel = getCallerPanel();
            if (callerPanel != null) {
                callerPanel.addToContainer(mPanelContainer);
            }
        }
    }

    @Override 
    public void onDebugInfo( String info) {
    }

    @Override 
    public void onError(int errCode,  String errMsg) {
    }

    public final void addDebugInfo(String info) {
        AUICall1V1Observer callObserver = this.debugInfoOutput;
        if (callObserver == null) {
            return;
        }
        callObserver.onDebugInfo(info);
    }
}
