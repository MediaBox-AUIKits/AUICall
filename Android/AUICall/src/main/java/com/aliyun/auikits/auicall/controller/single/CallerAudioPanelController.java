package com.aliyun.auikits.auicall.controller.single;

import android.content.Context;
import android.view.View;

import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.widget.LoudSpeakerWidget;
import com.aliyun.auikits.auicall.widget.PhoneCallWidget;
import com.aliyun.auikits.auicall.widget.VoiceMuteWidget;
import com.aliyun.auikits.auicall.R;

public final class CallerAudioPanelController extends BaseCallPanelController {
    public PhoneCallWidget hangupBtn;
    public LoudSpeakerWidget loudspeakerBtn;
    private boolean loudspeakerOn;

    private CallerActionCallback mCallback;
    public VoiceMuteWidget voiceMuteBtn;
    private boolean voiceMuted;

    public CallerAudioPanelController(AUICall1V1Model callModel, CallerActionCallback callback) {
        super(callModel);
        this.loudspeakerOn = true;
        this.mCallback = callback;
    }

    public final PhoneCallWidget getHangupBtn() {
        return hangupBtn;
    }

    public final void setHangupBtn( PhoneCallWidget phoneCallWidget) {
        this.hangupBtn = phoneCallWidget;
    }


    public final VoiceMuteWidget getVoiceMuteBtn() {
        return voiceMuteBtn;
    }

    public final void setVoiceMuteBtn( VoiceMuteWidget voiceMuteWidget) {
        this.voiceMuteBtn = voiceMuteWidget;
    }

    public final LoudSpeakerWidget getLoudspeakerBtn() {
        return loudspeakerBtn;
    }

    public final void setLoudspeakerBtn( LoudSpeakerWidget loudSpeakerWidget) {
        this.loudspeakerBtn = loudSpeakerWidget;
    }

    public final boolean getVoiceMuted() {
        return this.voiceMuted;
    }

    public final void setVoiceMuted(boolean z) {
        this.voiceMuted = z;
    }

    public final boolean getLoudspeakerOn() {
        return this.loudspeakerOn;
    }

    public final void setLoudspeakerOn(boolean z) {
        this.loudspeakerOn = z;
    }

    @Override 
    public View inflate( Context ctx,  View container) {
        super.inflate(ctx, container);
        VoiceMuteWidget voiceMuteWidget = rootView == null ? null : (VoiceMuteWidget) rootView.findViewById(R.id.mute_voice_btn);
        setVoiceMuteBtn(voiceMuteWidget);
        PhoneCallWidget phoneCallWidget = rootView == null ? null : (PhoneCallWidget) rootView.findViewById(R.id.hangup_btn);
        setHangupBtn(phoneCallWidget);
        getHangupBtn().setState(PhoneCallWidget.State.AUDIO_HANGUP);
        LoudSpeakerWidget loudSpeakerWidget = rootView != null ? (LoudSpeakerWidget) rootView.findViewById(R.id.switch_audio_out_btn) : null;
        setLoudspeakerBtn(loudSpeakerWidget);
        getVoiceMuteBtn().setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                voiceMuted = !voiceMuted;
                mute(voiceMuted);
                if (voiceMuted) {
                    getVoiceMuteBtn().setState(VoiceMuteWidget.State.MIC_OFF);
                } else {
                    getVoiceMuteBtn().setState(VoiceMuteWidget.State.MIC_ON);
                }
            }
        });
        getHangupBtn().setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                hangup();
            }
        });
        getLoudspeakerBtn().setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (mCallModel != null && mCallModel.openLoudspeaker(!loudspeakerOn)) {
                    loudspeakerOn = !loudspeakerOn;
                    if (loudspeakerOn) {
                        getLoudspeakerBtn().setState(LoudSpeakerWidget.State.ON);
                    } else {
                        getLoudspeakerBtn().setState(LoudSpeakerWidget.State.OFF);
                    }
                }
            }
        });
        return rootView;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.caller_calling_audio_panel;
    }

    public final void mute(boolean on) {
        AUICall1V1Model mCallModel = getMCallModel();
        if (mCallModel != null) {
            mCallModel.openMic(!on);
        }
        CallerActionCallback callerActionCallback = this.mCallback;
        if (callerActionCallback == null) {
            return;
        }
        callerActionCallback.onCallerMuteMic(!on);
    }

    public final void hangup() {
        if (mCallModel != null) {
            mCallModel.hangup(false);
        }
        CallerActionCallback callerActionCallback = this.mCallback;
        if (callerActionCallback == null) {
            return;
        }
        callerActionCallback.onCallerHangup();
    }

    @Override 
    public void resetState() {
        super.resetState();
        getVoiceMuteBtn().setState(VoiceMuteWidget.State.MIC_ON);
        this.voiceMuted = false;
        getLoudspeakerBtn().setState(LoudSpeakerWidget.State.ON);
        this.loudspeakerOn = true;
    }
}
