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

public final class CameraMuteWidget extends FrameLayout {

    private View mContentView;
    private ImageView mIcon;

    private State mState;
    private TextView mTips;

    public enum State {
        CAMERA_ON,
        CAMERA_OFF
    }

    public CameraMuteWidget( Context context) {
        super(context);
        this.mState = State.CAMERA_ON;
    }

    public CameraMuteWidget( Context context,  AttributeSet attri) {
        super(context, attri);
        this.mState = State.CAMERA_ON;
    }

    public CameraMuteWidget( Context context,  AttributeSet attri, int defStyle) {
        super(context, attri, defStyle);
        this.mState = State.CAMERA_ON;
    }

    public CameraMuteWidget( Context context,  AttributeSet attri, int defStyle, int defStyleRes) {
        super(context, attri, defStyle, defStyleRes);
        this.mState = State.CAMERA_ON;
    }

    @Override 
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mContentView == null) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.camera_mute_btn, (ViewGroup) this, true);
            this.mContentView = inflate;
            ImageView imageView = inflate == null ? null : (ImageView) inflate.findViewById(R.id.mute_camera_icon);
            this.mIcon = imageView;
            View view = this.mContentView;
            TextView textView = view != null ? (TextView) view.findViewById(R.id.mute_camera_tips) : null;
            this.mTips = textView;
        }
    }

    private final void onCameraClose() {
        if(mIcon == null || mTips == null) return;
        mIcon.setImageResource(R.drawable.ic_camera_close);
        mTips.setText("关闭摄像头");
    }

    private final void onCameracOpen() {
        if(mIcon == null || mTips == null) return;
        mIcon.setImageResource(R.drawable.ic_camera_open);
        mTips.setText("开启摄像头");
    }

    public final void setState( State state) {
        if (this.mState == state) {
            return;
        }
        this.mState = state;
        if (mState == State.CAMERA_ON) {
            onCameracOpen();
        } else if (mState == State.CAMERA_OFF) {
            onCameraClose();
        }
    }
}
