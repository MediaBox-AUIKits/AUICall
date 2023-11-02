package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.widget.MeetingMemberView;
import com.aliyun.auikits.auicall.widget.MemberTag;
import com.aliyun.auikits.auicall.R;

import java.util.ArrayList;

public final class MemberTwoViewController extends BaseMeetingViewController {

    private ViewGroup mMainPreviewView;

    private MemberTag mMemberTag;

    private MeetingMemberView mRemoteView;

    private ImageView mUserIcon;

    public MemberTwoViewController(ViewGroup containerView, AUICallNVNModel model) {
        super(containerView, model);
    }

    @Override 
    public View inflateView( Context ctx) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.meeting_call_two_view, getMRootContainerView(), false);
        this.mMainPreviewView = (ViewGroup) view.findViewById(R.id.main_preview_container);
        this.mUserIcon = (ImageView) view.findViewById(R.id.user_icon);
        MeetingMemberView meetingMemberView = (MeetingMemberView) view.findViewById(R.id.remote_view);
        this.mRemoteView = meetingMemberView;
        meetingMemberView.setBizModel(mMeetingModel);
        meetingMemberView.setSurfaceTop(true);
        MemberTag memberTag = (MemberTag) view.findViewById(R.id.member_tag);
        this.mMemberTag = memberTag;
        memberTag.setBizModel(mMeetingModel);
        return view;
    }

    @Override 
    public void show( Context context) {
        super.show(context);
        UserInfo currentUser = mMeetingModel.getCurrentUser();
        ViewGroup viewGroup = this.mMainPreviewView;
        ImageView imageView = this.mUserIcon;
        if (currentUser != null && currentUser.getCameraOn()) {
            mMeetingModel.setViewContainer(currentUser.getUserId(), this.mMainPreviewView, false);
            viewGroup.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        } else {
            mMeetingModel.setViewContainer(currentUser.getUserId(), null, false);
            viewGroup.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
        MemberTag memberTag = this.mMemberTag;
        if (memberTag != null) {
            memberTag.bindData(currentUser);
        }
        if (!mMeetingModel.getMeetingMembers().isEmpty()) {
            UserInfo remoteUser = new ArrayList<>(mMeetingModel.getMeetingMembers().values()).get(0);
            MeetingMemberView meetingMemberView = this.mRemoteView;
            if (meetingMemberView == null) {
                return;
            }
            meetingMemberView.bindData(remoteUser);
        }
    }

    @Override 
    public void updateCurrentMic( UserInfo user) {
    }
}
