package com.aliyun.auikits.auicall.controller.single;

import android.content.Context;
import android.view.View;

import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.widget.PhoneCallWidget;
import com.aliyun.auikits.auicall.R;

public final class BeCalledAudioPanelController extends BaseBeCalledPanelController {
    protected PhoneCallWidget mAcceptBtn;
    protected BeCalledActionCallback mCallback;
    protected PhoneCallWidget mHangupBtn;

    public BeCalledAudioPanelController(AUICall1V1Model callModel, BeCalledActionCallback callback) {
        super(callModel);
        this.mCallback = callback;
    }

    @Override 
    public View inflate( Context ctx,  View container) {
        super.inflate(ctx, container);
        PhoneCallWidget phoneCallWidget = null;
        mHangupBtn = rootView == null ? null : (PhoneCallWidget) rootView.findViewById(R.id.hangup_btn);
        mHangupBtn.setState(PhoneCallWidget.State.AUDIO_HANGUP);
        mHangupBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                hangup();
            }
        });
        mAcceptBtn = rootView == null ? null : (PhoneCallWidget) rootView.findViewById(R.id.accept_btn);
        mAcceptBtn.setState(PhoneCallWidget.State.AUDIO_HANGON);
        mAcceptBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                hangon();
            }
        });
        return rootView;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.be_called_waiting_audio_panel;
    }

    public final void hangup() {
        if (mCallModel != null) {
            String callerId = getCallerId();
            mCallModel.refuse(callerId, false);
        }
        this.mCallback.onHangup();
    }

    public final void hangon() {
        if (mCallModel != null) {
            String callerId = getCallerId();
            mCallModel.accept(callerId);
        }
        this.mCallback.onAccept();
    }
}
