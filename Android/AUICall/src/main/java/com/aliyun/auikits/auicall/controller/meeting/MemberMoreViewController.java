package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.widget.MeetingMemberView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public abstract class MemberMoreViewController extends BaseMeetingViewController {

    private final BaseMeetingViewController.UserPriorityComparator mComparator;

    protected MeetingMemberView mHostView;

    protected List<MeetingMemberView> mMemberViews;

    public MemberMoreViewController(ViewGroup containerView, AUICallNVNModel model) {
        super(containerView, model);
        this.mMemberViews = new ArrayList();
        this.mComparator = new BaseMeetingViewController.UserPriorityComparator(model);
    }

    private final int convertDp2Px(int dp) {
        float scale = getMRootContainerView().getContext().getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + 0.5f);
    }

    private final int getItemCompatWidth() {
        int screenWidth = getMRootContainerView().getContext().getResources().getDisplayMetrics().widthPixels;
        return (screenWidth - convertDp2Px(17)) / 2;
    }

    public final void adaptItemUI( View itemView) {
        ViewGroup.LayoutParams lp = itemView.getLayoutParams();
        lp.width = getItemCompatWidth();
        lp.height = getItemCompatWidth();
        itemView.setLayoutParams(lp);
    }

    @Override 
    public void show( Context context) {
        super.show(context);
        List<UserInfo> users = new ArrayList<>(mMeetingModel.getMeetingMembers().values());
        UserInfo currentUser = mMeetingModel.getCurrentUser();
        users.add(currentUser);
        Collections.sort(users, this.mComparator);
        int size = this.mMemberViews.size();
        int i = 0;
        while (i < size) {
            this.mMemberViews.get(i).bindData((UserInfo) users.get(i));
            i++;
        }
    }

    @Override 
    public void updateCurrentMic( UserInfo user) {
    }
}
