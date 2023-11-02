package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.widget.MemberTag;
import com.aliyun.auikits.auicall.R;

public final class MemberOneViewController extends BaseMeetingViewController {

    private MemberTag mMemberTag;

    private ViewGroup mPreviewContainer;

    private ImageView mUserIcon;

    public MemberOneViewController(ViewGroup containerView, AUICallNVNModel model) {
        super(containerView, model);
    }

    @Override 
    public View inflateView( Context ctx) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.meeting_call_single_view, getMRootContainerView(), false);
        this.mPreviewContainer = (ViewGroup) view.findViewById(R.id.main_preview_container);
        this.mUserIcon = (ImageView) view.findViewById(R.id.user_icon);
        MemberTag memberTag = (MemberTag) view.findViewById(R.id.member_tag);
        this.mMemberTag = memberTag;
        memberTag.setBizModel(mMeetingModel);
        return view;
    }

    @Override 
    public void updateCurrentMic( UserInfo user) {
    }

    @Override 
    public void show( Context context) {
        super.show(context);
        UserInfo currentUser = mMeetingModel.getCurrentUser();
        ViewGroup viewGroup = this.mPreviewContainer;
        ImageView imageView = this.mUserIcon;
        if (currentUser != null && currentUser.getCameraOn()) {
            mMeetingModel.setViewContainer(currentUser.getUserId(), this.mPreviewContainer, false);
            viewGroup.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        } else {
            mMeetingModel.setViewContainer(currentUser.getUserId(), null, false);
            viewGroup.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
        MemberTag memberTag = this.mMemberTag;
        if (memberTag == null) {
            return;
        }
        memberTag.bindData(currentUser);
    }
}
