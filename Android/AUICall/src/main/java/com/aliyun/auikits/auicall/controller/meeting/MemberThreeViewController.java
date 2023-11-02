package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.widget.MeetingMemberView;
import com.aliyun.auikits.auicall.R;

public final class MemberThreeViewController extends MemberMoreViewController {
    public MemberThreeViewController(ViewGroup containerView, AUICallNVNModel model) {
        super(containerView, model);
    }

    @Override 
    public View inflateView( Context ctx) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.meeting_call_three_view, getMRootContainerView(), false);
        mHostView = view.findViewById(R.id.host_view);
        MeetingMemberView view1 = (MeetingMemberView) view.findViewById(R.id.member1_view);
        MeetingMemberView view2 = (MeetingMemberView) view.findViewById(R.id.member2_view);
        mHostView.setBizModel(mMeetingModel);
        view1.setBizModel(mMeetingModel);
        view2.setBizModel(mMeetingModel);
        adaptItemUI(mHostView);
        adaptItemUI(view1);
        adaptItemUI(view2);
        mMemberViews.add(mHostView);
        mMemberViews.add(view1);
        mMemberViews.add(view2);
        return view;
    }

    @Override 
    public void updateCurrentMic( UserInfo user) {
    }
}
