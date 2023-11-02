package com.aliyun.auikits.auicall.controller.single;

import android.content.Context;
import android.view.View;

import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.widget.LoudSpeakerWidget;
import com.aliyun.auikits.auicall.widget.PhoneCallWidget;
import com.aliyun.auikits.auicall.widget.VoiceMuteWidget;
import com.aliyun.auikits.auicall.R;

public final class OnCallAudioPanelController extends BaseOnCallPanelController {
    public PhoneCallWidget hangupBtn;
    public LoudSpeakerWidget loudSpeakerBtn;
    private boolean loudspeakerOn;
    public VoiceMuteWidget voiceMuteBtn;
    private boolean voiceMuted;

    public OnCallAudioPanelController(AUICall1V1Model callModel) {
        super(callModel);
        this.loudspeakerOn = true;
    }

    public final VoiceMuteWidget getVoiceMuteBtn() {
        return voiceMuteBtn;
    }

    public final void setVoiceMuteBtn( VoiceMuteWidget voiceMuteWidget) {
        this.voiceMuteBtn = voiceMuteWidget;
    }

    public final PhoneCallWidget getHangupBtn() {
        return this.hangupBtn;
    }

    public final void setHangupBtn( PhoneCallWidget phoneCallWidget) {
        this.hangupBtn = phoneCallWidget;
    }

    public final LoudSpeakerWidget getLoudSpeakerBtn() {
        return this.loudSpeakerBtn;
    }

    public final void setLoudSpeakerBtn( LoudSpeakerWidget loudSpeakerWidget) {
        this.loudSpeakerBtn = loudSpeakerWidget;
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
        PhoneCallWidget phoneCallWidget = rootView == null ? null : (PhoneCallWidget) rootView.findViewById(R.id.hangup_btn);
        setHangupBtn(phoneCallWidget);
        getHangupBtn().setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                hangup();
            }
        });
        LoudSpeakerWidget loudSpeakerWidget = rootView != null ? (LoudSpeakerWidget) rootView.findViewById(R.id.switch_audio_out_btn) : null;
        setLoudSpeakerBtn(loudSpeakerWidget);
        getLoudSpeakerBtn().setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (mCallModel != null && mCallModel.openLoudspeaker(!loudspeakerOn)) {
                    loudspeakerOn = !loudspeakerOn;
                    if (loudspeakerOn) {
                        getLoudSpeakerBtn().setState(LoudSpeakerWidget.State.ON);
                    } else {
                        getLoudSpeakerBtn().setState(LoudSpeakerWidget.State.OFF);
                    }
                }
            }
        });
        return rootView;
    }

    @Override 
    public void toggleOperationsVisibility() {
        if (getHangupBtn().getVisibility() != View.GONE) {
            getVoiceMuteBtn().setVisibility(View.GONE);
            getHangupBtn().setVisibility(View.GONE);
            getLoudSpeakerBtn().setVisibility(View.GONE);
            return;
        }
        getVoiceMuteBtn().setVisibility(View.VISIBLE);
        getHangupBtn().setVisibility(View.VISIBLE);
        getLoudSpeakerBtn().setVisibility(View.VISIBLE);
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.on_call_audio_panel;
    }

    public final void hangup() {
        if (mCallModel == null) {
            return;
        }
        mCallModel.hangup(false);
    }

    public final void mute(boolean on) {
        if (mCallModel == null) {
            return;
        }
        mCallModel.openMic(!on);
    }

    @Override 
    public void resetState() {
        super.resetState();
        getVoiceMuteBtn().setState(VoiceMuteWidget.State.MIC_ON);
        this.voiceMuted = false;
        getLoudSpeakerBtn().setState(LoudSpeakerWidget.State.ON);
        this.loudspeakerOn = true;
    }
}
