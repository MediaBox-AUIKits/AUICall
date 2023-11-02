package com.aliyun.auikits.auicall.controller.single;

import android.content.Context;
import android.view.View;

import com.aliyun.auikits.auicall.controller.meeting.VideoOnCallActionCallback;
import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.widget.CameraMuteWidget;
import com.aliyun.auikits.auicall.widget.LoudSpeakerWidget;
import com.aliyun.auikits.auicall.widget.PhoneCallWidget;
import com.aliyun.auikits.auicall.widget.VoiceMuteWidget;
import com.aliyun.auikits.auicall.R;

public final class OnCallVideoPanelController extends BaseOnCallPanelController {
    private boolean cameraOn;
    public PhoneCallWidget hangupBtn;
    public LoudSpeakerWidget loudSpeakerBtn;
    private boolean loudSpeakerOn;

    private VideoOnCallActionCallback mCallback;
    private View mSwitchToAudioBtn;
    public CameraMuteWidget muteCameraBtn;
    public VoiceMuteWidget voiceMuteBtn;
    private boolean voiceMuted;

    public OnCallVideoPanelController(AUICall1V1Model callModel, VideoOnCallActionCallback callback) {
        super(callModel);
        this.mCallback = callback;
        this.voiceMuted = true;
        this.cameraOn = true;
    }

    public final VoiceMuteWidget getVoiceMuteBtn() {
        return this.voiceMuteBtn;
    }

    public final void setVoiceMuteBtn( VoiceMuteWidget voiceMuteWidget) {
        this.voiceMuteBtn = voiceMuteWidget;
    }

    public final LoudSpeakerWidget getLoudSpeakerBtn() {
        return this.loudSpeakerBtn;
    }

    public final void setLoudSpeakerBtn( LoudSpeakerWidget loudSpeakerWidget) {
        this.loudSpeakerBtn = loudSpeakerWidget;
    }

    public final CameraMuteWidget getMuteCameraBtn() {
        return this.muteCameraBtn;
    }

    public final void setMuteCameraBtn( CameraMuteWidget cameraMuteWidget) {
        this.muteCameraBtn = cameraMuteWidget;
    }

    public final PhoneCallWidget getHangupBtn() {
        return this.hangupBtn;
    }

    public final void setHangupBtn( PhoneCallWidget phoneCallWidget) {
        this.hangupBtn = phoneCallWidget;
    }

    public final boolean getVoiceMuted() {
        return this.voiceMuted;
    }

    public final void setVoiceMuted(boolean z) {
        this.voiceMuted = z;
    }

    public final boolean getLoudSpeakerOn() {
        return this.loudSpeakerOn;
    }

    public final void setLoudSpeakerOn(boolean z) {
        this.loudSpeakerOn = z;
    }

    public final boolean getCameraOn() {
        return this.cameraOn;
    }

    public final void setCameraOn(boolean z) {
        this.cameraOn = z;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.on_call_video_panel;
    }

    @Override 
    public View inflate( Context ctx,  View container) {
        super.inflate(ctx, container);
        View findViewById = rootView.findViewById(R.id.mute_voice_btn);
        setVoiceMuteBtn((VoiceMuteWidget) findViewById);
        View findViewById2 = rootView.findViewById(R.id.switch_audio_out_btn);
        setLoudSpeakerBtn((LoudSpeakerWidget) findViewById2);
        View findViewById3 = rootView.findViewById(R.id.mute_camera_btn);
        setMuteCameraBtn((CameraMuteWidget) findViewById3);
        View findViewById4 = rootView.findViewById(R.id.hangup_btn);
        setHangupBtn((PhoneCallWidget) findViewById4);
        View findViewById5 = rootView.findViewById(R.id.switch_to_audio);
        this.mSwitchToAudioBtn = findViewById5;
        getVoiceMuteBtn().setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                voiceMuted = !voiceMuted;
                if (mCallModel != null) {
                    mCallModel.openMic(voiceMuted);
                }
                if (voiceMuted) {
                    getVoiceMuteBtn().setState(VoiceMuteWidget.State.MIC_ON);
                } else {
                    getVoiceMuteBtn().setState(VoiceMuteWidget.State.MIC_OFF);
                }
                mCallback.onVideoOnCallMuteVoice(!voiceMuted);
            }
        });
        getLoudSpeakerBtn().setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (mCallModel != null && mCallModel.openLoudspeaker(!loudSpeakerOn)) {
                    loudSpeakerOn = !loudSpeakerOn;
                    if (loudSpeakerOn) {
                        getLoudSpeakerBtn().setState(LoudSpeakerWidget.State.ON);
                    } else {
                        getLoudSpeakerBtn().setState(LoudSpeakerWidget.State.OFF);
                    }
                    mCallback.onVideoOnCallSpeakerMute(!loudSpeakerOn);
                }
            }
        });
        getMuteCameraBtn().setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                cameraOn = !cameraOn;
                if (mCallModel != null) {
                    mCallModel.openCamera(cameraOn);
                }
                if (cameraOn) {
                    getMuteCameraBtn().setState(CameraMuteWidget.State.CAMERA_ON);
                } else {
                    getMuteCameraBtn().setState(CameraMuteWidget.State.CAMERA_OFF);
                }
                mCallback.onVideoOnCallCameraMute(!cameraOn);
            }
        });
        getHangupBtn().setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (mCallModel != null) {
                    mCallModel.hangup(false);
                }
                mCallback.onVideoOnCallHangup();
            }
        });
        mSwitchToAudioBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                if (mCallModel != null) {
                    mCallModel.switchToAudioMode();
                }
                mCallback.onVideoOnCallSwitchToAudio();
            }
        });
        return rootView;
    }

    @Override 
    public void toggleOperationsVisibility() {
        if (getHangupBtn().getVisibility() == View.GONE) {
            getVoiceMuteBtn().setVisibility(View.VISIBLE);
            getMuteCameraBtn().setVisibility(View.VISIBLE);
            getLoudSpeakerBtn().setVisibility(View.VISIBLE);
            mSwitchToAudioBtn.setVisibility(View.VISIBLE);
            getHangupBtn().setVisibility(View.VISIBLE);
            return;
        }
        getVoiceMuteBtn().setVisibility(View.GONE);
        getMuteCameraBtn().setVisibility(View.GONE);
        getLoudSpeakerBtn().setVisibility(View.GONE);
        mSwitchToAudioBtn.setVisibility(View.GONE);
        getHangupBtn().setVisibility(View.GONE);
    }

    @Override 
    public void resetState() {
        super.resetState();
        getVoiceMuteBtn().setState(VoiceMuteWidget.State.MIC_ON);
        this.voiceMuted = true;
        getLoudSpeakerBtn().setState(LoudSpeakerWidget.State.ON);
        this.loudSpeakerOn = true;
        getMuteCameraBtn().setState(CameraMuteWidget.State.CAMERA_ON);
        this.cameraOn = true;
    }
}
