package com.aliyun.auikits.auicall.controller.single;

import android.content.Context;
import android.view.View;

import com.aliyun.auikits.auicall.controller.meeting.VideoBeCallActionCallback;
import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.widget.PhoneCallWidget;
import com.aliyun.auikits.auicall.R;

public final class BeCalledVideoPanelController extends BaseBeCalledPanelController {
    protected VideoBeCallActionCallback mCallback;
    protected PhoneCallWidget mHangonBtn;
    protected PhoneCallWidget mHangupBtn;
    protected View mSwitchToAudioBtn;

    public BeCalledVideoPanelController(AUICall1V1Model callModel, VideoBeCallActionCallback callback) {
        super(callModel);
        this.mCallback = callback;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.be_called_waiting_video_panel;
    }

    public View inflate( Context ctx,  View container) {
        super.inflate(ctx, container);
        mHangupBtn = rootView.findViewById(R.id.hangup_btn);
        mHangupBtn.setState(PhoneCallWidget.State.VIDEO_HANGUP);
        mHangupBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                if (mCallModel != null) {
                    String callerId = getCallerId();
                    mCallModel.refuse(callerId, false);
                }
                mCallback.onVideoHangup();
            }
        });
        mHangonBtn = rootView.findViewById(R.id.accept_btn);
        mHangonBtn.setState(PhoneCallWidget.State.VIDEO_HANGON);
        mHangonBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                if (mCallModel != null) {
                    String callerId = getCallerId();
                    mCallModel.accept(callerId);
                }
                mCallback.onVideoHangon();
            }
        });
        mSwitchToAudioBtn = rootView.findViewById(R.id.switch_to_audio);
        mSwitchToAudioBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                if (mCallModel != null) {
                    mCallModel.switchToAudioMode();
                }
                mCallback.onSwitchToAudio();
            }
        });
        return rootView;
    }
}
