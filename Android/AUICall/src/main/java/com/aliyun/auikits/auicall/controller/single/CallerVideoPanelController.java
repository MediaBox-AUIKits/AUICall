package com.aliyun.auikits.auicall.controller.single;

import android.content.Context;
import android.view.View;

import com.aliyun.auikits.auicall.controller.meeting.VideoCallerActionCallback;
import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.widget.PhoneCallWidget;
import com.aliyun.auikits.auicall.R;

public final class CallerVideoPanelController extends BaseCallPanelController {

    private VideoCallerActionCallback mCallback;
    private PhoneCallWidget mHangupBtn;
    private View mSwitchToAudio;

    public CallerVideoPanelController(AUICall1V1Model callModel, VideoCallerActionCallback callback) {
        super(callModel);
        this.mCallback = callback;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.caller_calling_video_panel;
    }

    @Override 
    public View inflate( Context ctx,  View container) {
        super.inflate(ctx, container);
        mSwitchToAudio = rootView.findViewById(R.id.switch_to_audio);
        mHangupBtn = rootView.findViewById(R.id.hangup_btn);
        mHangupBtn.setState(PhoneCallWidget.State.VIDEO_HANGUP);
        mSwitchToAudio.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                if (mCallModel != null) {
                    mCallModel.switchToAudioMode();
                }
                mCallback.onSwitchToAudio();
            }
        });
        mHangupBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                if (mCallModel != null) {
                    mCallModel.hangup(false);
                }
                mCallback.onCancelVideoCall();
            }
        });
        return rootView;
    }
}
