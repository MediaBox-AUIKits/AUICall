package com.aliyun.auikits.auicall.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.auikits.auicall.R;
public final class PhoneCallWidget extends FrameLayout {

    private View mContentView;
    private ImageView mIcon;

    private State mState;
    private TextView mTips;

    public enum State {
        AUDIO_HANGUP,
        AUDIO_HANGON,
        VIDEO_HANGUP,
        VIDEO_HANGON
    }

    public PhoneCallWidget( Context context) {
        super(context);
        this.mState = State.AUDIO_HANGUP;
    }

    
    public PhoneCallWidget( Context context,  AttributeSet attri) {
        super(context, attri);
        this.mState = State.AUDIO_HANGUP;
    }

    
    public PhoneCallWidget( Context context,  AttributeSet attri, int defStyle) {
        super(context, attri, defStyle);
        this.mState = State.AUDIO_HANGUP;
    }

    
    public PhoneCallWidget( Context context,  AttributeSet attri, int defStyle, int defStyleRes) {
        super(context, attri, defStyle, defStyleRes);
        this.mState = State.AUDIO_HANGUP;
    }

    @Override 
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private final void init() {
        if (this.mContentView == null) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.phone_call_btn, (ViewGroup) this, true);
            this.mContentView = inflate;
            ImageView imageView = inflate == null ? null : (ImageView) inflate.findViewById(R.id.hangup_icon);
            this.mIcon = imageView;
            View view = this.mContentView;
            TextView textView = view != null ? (TextView) view.findViewById(R.id.hangup_tips) : null;
            this.mTips = textView;
        }
    }

    private final void onAudioHangup() {
        init();
        if(mIcon == null || mTips == null) return;
        mIcon.setImageResource(R.drawable.ic_audio_hangup);
        mTips.setText("挂断");
    }

    private final void onAudioHangon() {
        init();
        if(mIcon == null || mTips == null) return;
        mIcon.setImageResource(R.drawable.ic_audio_hangon);
        mTips.setText("接听");
    }

    private final void onVideoHangup() {
        init();
        if(mIcon == null || mTips == null) return;
        mIcon.setImageResource(R.drawable.ic_audio_hangup);
        mTips.setText("挂断");
    }

    private final void onVideoHangon() {
        init();
        if(mIcon == null || mTips == null) return;
        mIcon.setImageResource(R.drawable.ic_video_hangon);
        mTips.setText("接听");
    }

    public final void setState( State state) {
        if (this.mState == state) {
            return;
        }
        this.mState = state;
        if (mState == State.AUDIO_HANGON) {
            onAudioHangon();
        } else if (mState == State.AUDIO_HANGUP) {
            onAudioHangup();
        } else if (mState == State.VIDEO_HANGUP) {
            onVideoHangup();
        } else if (mState == State.VIDEO_HANGON) {
            onVideoHangon();
        }
    }
}
