package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.widget.PhoneCallWidget;
import com.aliyun.auikits.auicall.R;

public final class BeCallPanelController {

    private Callback callback;

    private String caller;

    private AUICallNVNModel mCallModel;

    private View mContentView;

    private PhoneCallWidget mHangonBtn;

    private PhoneCallWidget mHangupBtn;

    private View mVideoAcceptBtn;

    public interface Callback {
        void onAudioHangon();

        void onHangup();

        void onVideoHangon();
    }

    public BeCallPanelController(AUICallNVNModel model) {
        this.mCallModel = model;
    }

    public final String getCaller() {
        return this.caller;
    }

    public final void setCaller( String str) {
        this.caller = str;
    }


    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    public View inflate( Context ctx,  View container) {
        View inflate = LayoutInflater.from(ctx).inflate(R.layout.meeting_be_called_panel, (ViewGroup) container, false);
        this.mContentView = inflate;
        mHangupBtn = (PhoneCallWidget) inflate.findViewById(R.id.hangup_btn);
        mHangupBtn.setState(PhoneCallWidget.State.AUDIO_HANGUP);
        mHangonBtn = (PhoneCallWidget) mContentView.findViewById(R.id.hangon_btn);
        mHangonBtn.setState(PhoneCallWidget.State.AUDIO_HANGON);
        mVideoAcceptBtn = mContentView.findViewById(R.id.video_accept_call);
        mHangupBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view3) {
                if (mCallModel != null) {
                    mCallModel.refuse(caller, false);
                }
                if (callback == null) {
                    return;
                }
                callback.onHangup();
            }
        });
        mHangonBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view3) {
                if (mCallModel != null) {
                    mCallModel.accept(caller, false);
                }
                if (callback == null) {
                    return;
                }
                callback.onAudioHangon();
            }
        });
        mVideoAcceptBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view4) {
                if (mCallModel != null) {
                    mCallModel.accept(caller, true);
                }
                if (callback == null) {
                    return;
                }
                callback.onVideoHangon();
            }
        });
        return mContentView;
    }

    public final void addToContainer( ViewGroup container) {
        if (container.getChildAt(0) == this.mContentView) {
            return;
        }
        container.removeAllViews();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.addView(this.mContentView, lp);
    }
}
