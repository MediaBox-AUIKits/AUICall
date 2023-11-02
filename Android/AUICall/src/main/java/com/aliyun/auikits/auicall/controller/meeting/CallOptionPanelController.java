package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.R;

public final class CallOptionPanelController {

    private AUICallNVNModel bizModel;

    private Callback callback;

    private View mBeautyBtn;

    private ImageView mBeautyIcon;
    private boolean mBeautyPanelOpen;

    private TextView mBeautyTips;

    private View mContentView;

    private View mMemberBtn;
    private int mMemberCount;

    private ImageView mMemberIcon;

    private TextView mMemberTips;

    private View mMoreBtn;

    private ImageView mMoreIcon;

    private TextView mMoreTips;

    private View mMuteCameraBtn;

    private ImageView mMuteCameraIcon;

    private TextView mMuteCameraTips;

    private View mMuteMicBtn;

    private ImageView mMuteMicIcon;

    private TextView mMuteMicTips;
    private boolean mShowMorePanel;
    private boolean mMicOpen = true;
    private boolean mCameraOpen = true;

    public interface Callback {
        void onBeautyClick(boolean z);

        void onMemberClick();

        void onMoreClick(boolean z);

        void onOpenMic(boolean z);
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    public final AUICallNVNModel getBizModel() {
        return this.bizModel;
    }

    public final void setBizModel( AUICallNVNModel meetingCallModel) {
        this.bizModel = meetingCallModel;
    }

    private final int getLayoutResId() {
        return R.layout.meeting_call_bottom_panel;
    }

    public final void inflate( Context ctx,  ViewGroup container) {
        View inflate = LayoutInflater.from(ctx).inflate(getLayoutResId(), container, true);
        this.mContentView = inflate;
        this.mMuteMicBtn = inflate.findViewById(R.id.mute_voice_btn);
        this.mMuteMicIcon = (ImageView) inflate.findViewById(R.id.mute_voice_icon);
        this.mMuteMicTips = (TextView) inflate.findViewById(R.id.mute_voice_tips);
        this.mMuteCameraBtn = inflate.findViewById(R.id.mute_camera_btn);
        this.mMuteCameraIcon = (ImageView) inflate.findViewById(R.id.mute_camera_icon);
        this.mMuteCameraTips = (TextView) inflate.findViewById(R.id.mute_camera_tips);
        this.mMemberBtn = inflate.findViewById(R.id.member_btn);
        this.mMemberIcon = (ImageView) inflate.findViewById(R.id.member_icon);
        this.mMemberTips = (TextView) inflate.findViewById(R.id.member_tips);
        this.mBeautyBtn = inflate.findViewById(R.id.beauty_btn);
        this.mBeautyIcon = (ImageView) inflate.findViewById(R.id.beauty_icon);
        this.mBeautyTips = (TextView) inflate.findViewById(R.id.beauty_tips);
        this.mMoreBtn = inflate.findViewById(R.id.more_btn);
        this.mMoreIcon = (ImageView) inflate.findViewById(R.id.more_icon);
        this.mMoreTips = (TextView) inflate.findViewById(R.id.more_tips);
        mMuteMicBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view16) {
                boolean micOpen = !mMicOpen;
                if (callback == null) {
                    return;
                }
                callback.onOpenMic(micOpen);
            }
        });
        mMuteCameraBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view17) {
                mCameraOpen = !mCameraOpen;
                if (bizModel != null) {
                    UserInfo currentUser = bizModel.getCurrentUser();
                    bizModel.openCamera(currentUser.getUserId(), mCameraOpen);
                }
                if (mCameraOpen) {
                    onCameraOn();
                } else {
                    onCameraOff();
                }
            }
        });
        mMemberBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view18) {
                if (callback == null) {
                    return;
                }
                callback.onMemberClick();
            }
        });
        mBeautyBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view19) {
                mBeautyPanelOpen = !mBeautyPanelOpen;
                if (callback == null) {
                    return;
                }
                callback.onBeautyClick(mBeautyPanelOpen);
            }
        });
        mMoreBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view20) {
                mShowMorePanel = !mShowMorePanel;
                if (callback == null) {
                    return;
                }
                callback.onMoreClick(mShowMorePanel);
            }
        });
    }

    public final void onUserOnline() {
        this.mMemberCount++;
        TextView textView = this.mMemberTips;
        if (textView == null) {
            return;
        }
        textView.setText("成员(" + this.mMemberCount + ')');
    }

    public final void onUserOffline() {
        this.mMemberCount--;
        TextView textView = this.mMemberTips;
        if (textView == null) {
            return;
        }
        textView.setText("成员(" + this.mMemberCount + ')');
    }

    public final void onMicOn() {
        this.mMicOpen = true;
        ImageView imageView = this.mMuteMicIcon;
        if (imageView != null) {
            imageView.setImageResource(R.mipmap.mic_open_icon);
        }
        TextView textView = this.mMuteMicTips;
        if (textView == null) {
            return;
        }
        textView.setText("静音");
    }

    public final void onMicOff() {
        this.mMicOpen = false;
        ImageView imageView = this.mMuteMicIcon;
        if (imageView != null) {
            imageView.setImageResource(R.mipmap.mic_close_icon);
        }
        TextView textView = this.mMuteMicTips;
        if (textView == null) {
            return;
        }
        textView.setText("解除静音");
    }

    public final void onCameraOn() {
        this.mCameraOpen = true;
        ImageView imageView = this.mMuteCameraIcon;
        if (imageView != null) {
            imageView.setImageResource(R.mipmap.camera_open_icon);
        }
        TextView textView = this.mMuteCameraTips;
        if (textView == null) {
            return;
        }
        textView.setText("关摄像头");
    }

    public final void onCameraOff() {
        this.mCameraOpen = false;
        ImageView imageView = this.mMuteCameraIcon;
        if (imageView != null) {
            imageView.setImageResource(R.mipmap.camera_close_icon);
        }
        TextView textView = this.mMuteCameraTips;
        if (textView == null) {
            return;
        }
        textView.setText("开摄像头");
    }

    public final void updateCurrent() {
        AUICallNVNModel meetingCallModel = this.bizModel;
        UserInfo currentUser = meetingCallModel == null ? null : meetingCallModel.getCurrentUser();
        if (currentUser != null) {
            this.mMicOpen = currentUser.getMicOn();
            this.mCameraOpen = currentUser.getCameraOn();
            if (currentUser.getMicOn()) {
                onMicOn();
            } else {
                onMicOff();
            }
            if (currentUser.getCameraOn()) {
                onCameraOn();
            } else {
                onCameraOff();
            }
        }
    }

    public final void resetState() {
        this.mBeautyPanelOpen = false;
        this.mMicOpen = true;
        this.mCameraOpen = true;
        this.mShowMorePanel = false;
        this.mMemberCount = 0;
        onMicOff();
        onCameraOff();
    }
}
