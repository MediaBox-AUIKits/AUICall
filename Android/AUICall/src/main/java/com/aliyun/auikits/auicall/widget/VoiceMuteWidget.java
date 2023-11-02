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

public final class VoiceMuteWidget extends FrameLayout {

    private View mContentView;
    private ImageView mIcon;

    private State mState;
    private TextView mTips;

    public enum State {
        MIC_ON,
        MIC_OFF
    }

    public VoiceMuteWidget( Context context) {
        super(context);
        this.mState = State.MIC_ON;
    }

    public VoiceMuteWidget( Context context,  AttributeSet attri) {
        super(context, attri);
        this.mState = State.MIC_ON;
    }

    public VoiceMuteWidget( Context context,  AttributeSet attri, int defStyle) {
        super(context, attri, defStyle);
        this.mState = State.MIC_ON;
    }

    public VoiceMuteWidget( Context context,  AttributeSet attri, int defStyle, int defStyleRes) {
        super(context, attri, defStyle, defStyleRes);
        this.mState = State.MIC_ON;
    }

    @Override 
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mContentView == null) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.voice_mute_btn, (ViewGroup) this, true);
            this.mContentView = inflate;
            ImageView imageView = inflate == null ? null : (ImageView) inflate.findViewById(R.id.mute_voice_icon);
            this.mIcon = imageView;
            View view = this.mContentView;
            TextView textView = view != null ? (TextView) view.findViewById(R.id.mute_voice_tips) : null;
            this.mTips = textView;
        }
    }

    private final void onMicClose() {
        if(mIcon == null || mTips == null) return;
        mIcon.setImageResource(R.drawable.ic_voice_open);
        mTips.setText("解除静音");
    }

    private final void onMicOpen() {
        if(mIcon == null || mTips == null) return;
        mIcon.setImageResource(R.drawable.ic_voice_mute);
        mTips.setText("静音");
    }

    public final void setState( State state) {
        if (this.mState == state) {
            return;
        }
        this.mState = state;
        if (mState == State.MIC_ON) {
            onMicOpen();
        } else if (mState == State.MIC_OFF) {
            onMicClose();
        }
    }

    public final State getState() {
        return this.mState;
    }
}
