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
public final class LoudSpeakerWidget extends FrameLayout {

    private View mContentView;

    private ImageView mIcon;

    private State mState = State.ON;

    private TextView mTips;

    public enum State {
        ON,
        OFF
    }

    public LoudSpeakerWidget( Context context) {
        super(context);
    }

    public LoudSpeakerWidget( Context context,  AttributeSet attri) {
        super(context, attri);
    }

    public LoudSpeakerWidget( Context context,  AttributeSet attri, int defStyle) {
        super(context, attri, defStyle);
    }

    public LoudSpeakerWidget( Context context,  AttributeSet attri, int defStyle, int defStyleRes) {
        super(context, attri, defStyle, defStyleRes);
    }

    @Override 
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mContentView == null) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.loudspeaker_btn, (ViewGroup) this, true);
            this.mContentView = inflate;
            ImageView imageView = inflate == null ? null : (ImageView) inflate.findViewById(R.id.loudspeaker_icon);
            this.mIcon = imageView;
            View view = this.mContentView;
            TextView textView = view != null ? (TextView) view.findViewById(R.id.loudspeaker_tips) : null;
            this.mTips = textView;
        }
    }

    private final void onClose() {
        ImageView imageView = this.mIcon;
        if (imageView != null) {
            imageView.setImageResource(R.drawable.ic_loudspeaker_open);
        }
        TextView textView = this.mTips;
        if (textView == null) {
            return;
        }
        textView.setText("开扬声器");
    }

    private final void onOpen() {
        ImageView imageView = this.mIcon;
        if (imageView != null) {
            imageView.setImageResource(R.drawable.ic_loudspeaker_close);
        }
        TextView textView = this.mTips;
        if (textView == null) {
            return;
        }
        textView.setText("关扬声器");
    }

    public final void setState( State state) {
        if (this.mState == state) {
            return;
        }
        this.mState = state;
        if (mState == State.ON) {
            onOpen();
        } else if (mState == State.OFF) {
            onClose();
        }
    }

    public final State getState() {
        return this.mState;
    }
}
