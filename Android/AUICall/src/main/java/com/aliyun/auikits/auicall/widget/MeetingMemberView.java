package com.aliyun.auikits.auicall.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.R;

public final class MeetingMemberView extends FrameLayout {
    private boolean isSurfaceTop;

    private AUICallNVNModel mBizModel;
    private boolean mInited;

    private MemberTag mMemberTag;

    private ViewGroup mPreviewContainer;

    private ImageView mUserIcon;

    public MeetingMemberView( Context context) {
        super(context);
    }

    public MeetingMemberView( Context context,  AttributeSet attri) {
        super(context, attri);
    }

    public MeetingMemberView( Context context,  AttributeSet attri, int defStyle) {
        super(context, attri, defStyle);
    }

    public MeetingMemberView( Context context,  AttributeSet attri, int defStyle, int defStyleRes) {
        super(context, attri, defStyle, defStyleRes);
    }

    public final boolean isSurfaceTop() {
        return this.isSurfaceTop;
    }

    public final void setSurfaceTop(boolean z) {
        this.isSurfaceTop = z;
    }

    @Override 
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mInited) {
            return;
        }
        LayoutInflater.from(getContext()).inflate(R.layout.meeting_member_view, (ViewGroup) this, true);
        this.mUserIcon = (ImageView) findViewById(R.id.user_icon);
        MemberTag memberTag = (MemberTag) findViewById(R.id.member_tag);
        this.mMemberTag = memberTag;
        if (this.mBizModel != null) {
            memberTag.setBizModel(this.mBizModel);
        }
        this.mPreviewContainer = (ViewGroup) findViewById(R.id.preview);
        this.mInited = true;
    }

    private final void onCameraState(String userId, boolean open) {
        AUICallNVNModel meetingCallModel = this.mBizModel;
        ImageView imageView = this.mUserIcon;
        ViewGroup viewGroup = this.mPreviewContainer;
        if (open) {
            if (meetingCallModel != null) {
                meetingCallModel.setViewContainer(userId, this.mPreviewContainer, this.isSurfaceTop);
            }
            imageView.setVisibility(INVISIBLE);
            viewGroup.setVisibility(VISIBLE);
            return;
        }
        if (meetingCallModel != null) {
            meetingCallModel.setViewContainer(userId, null, false);
        }
        imageView.setVisibility(VISIBLE);
        viewGroup.setVisibility(INVISIBLE);
    }

    public final void bindData( UserInfo userInfo) {
        MemberTag memberTag = this.mMemberTag;
        if (memberTag != null) {
            memberTag.bindData(userInfo);
        }
        onCameraState(userInfo.getUserId(), userInfo.getCameraOn());
    }

    public final void hidePreview( UserInfo userInfo) {
        AUICallNVNModel meetingCallModel = this.mBizModel;
        if (meetingCallModel != null) {
            meetingCallModel.setViewContainer(userInfo.getUserId(), null, false);
        }
        ImageView imageView = this.mUserIcon;
        imageView.setVisibility(VISIBLE);
        ViewGroup viewGroup = this.mPreviewContainer;
        viewGroup.setVisibility(INVISIBLE);
    }

    public final void setBizModel( AUICallNVNModel model) {
        this.mBizModel = model;
        MemberTag memberTag = this.mMemberTag;
        if (memberTag == null) {
            return;
        }
        memberTag.setBizModel(model);
    }
}
