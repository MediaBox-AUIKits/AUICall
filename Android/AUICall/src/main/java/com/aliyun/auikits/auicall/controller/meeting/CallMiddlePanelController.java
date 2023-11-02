package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.R;
import com.aliyun.auikits.auicall.controller.meeting.BaseMeetingViewController;
import com.aliyun.auikits.auicall.controller.meeting.MemberFourViewController;
import com.aliyun.auikits.auicall.controller.meeting.MemberPagerViewController;
import com.aliyun.auikits.auicall.controller.meeting.MemberOneViewController;
import com.aliyun.auikits.auicall.controller.meeting.MemberThreeViewController;
import com.aliyun.auikits.auicall.controller.meeting.MemberTwoViewController;
import java.util.LinkedHashMap;
import java.util.Map;
public final class CallMiddlePanelController {

    private AUICallNVNModel bizModel;

    private ViewGroup mContainer;

    private View mContentView;

    private Map<Integer, BaseMeetingViewController> mControllers = new LinkedHashMap();
    private boolean mInFront;
    private int mMemberCount;

    public final AUICallNVNModel getBizModel() {
        return this.bizModel;
    }

    public final void setBizModel( AUICallNVNModel meetingCallModel) {
        this.bizModel = meetingCallModel;
    }

    public final void inflate( Context ctx,  ViewGroup container) {
        View inflate = LayoutInflater.from(ctx).inflate(R.layout.meeting_call_middle_panel, container, true);
        this.mContentView = inflate;
        this.mContainer = (ViewGroup) inflate.findViewById(R.id.container);
    }

    public final void notifyMemberChange() {
        AUICallNVNModel meetingCallModel;
        if (!this.mInFront || (meetingCallModel = this.bizModel) == null) {
            return;
        }
        int i = this.mMemberCount;
        if (i != meetingCallModel.getMeetingMembers().size()) {
            AUICallNVNModel meetingCallModel2 = this.bizModel;
            this.mMemberCount = meetingCallModel2.getMeetingMembers().size();
            updateMeetingView();
        }
    }

    private final void onSingle() {
        if (!this.mControllers.containsKey(1) || this.mControllers.get(1) == null) {
            Map<Integer, BaseMeetingViewController> map = this.mControllers;
            ViewGroup viewGroup = this.mContainer;
            AUICallNVNModel meetingCallModel = this.bizModel;
            map.put(1, new MemberOneViewController(viewGroup, meetingCallModel));
        }
        for (Integer num : this.mControllers.keySet()) {
            int k = num.intValue();
            if (k == 1) {
                BaseMeetingViewController baseMeetingView = this.mControllers.get(Integer.valueOf(k));
                View view = this.mContentView;
                Context context = view.getContext();
                baseMeetingView.show(context);
            } else {
                BaseMeetingViewController baseMeetingView2 = this.mControllers.get(Integer.valueOf(k));
                baseMeetingView2.dismiss();
            }
        }
    }

    private final void onTwo() {
        if (!this.mControllers.containsKey(2) || this.mControllers.get(2) == null) {
            Map<Integer, BaseMeetingViewController> map = this.mControllers;
            ViewGroup viewGroup = this.mContainer;
            AUICallNVNModel meetingCallModel = this.bizModel;
            map.put(2, new MemberTwoViewController(viewGroup, meetingCallModel));
        }
        for (Integer num : this.mControllers.keySet()) {
            int k = num.intValue();
            if (k == 2) {
                BaseMeetingViewController baseMeetingView = this.mControllers.get(Integer.valueOf(k));
                View view = this.mContentView;
                Context context = view.getContext();
                baseMeetingView.show(context);
            } else {
                BaseMeetingViewController baseMeetingView2 = this.mControllers.get(Integer.valueOf(k));
                baseMeetingView2.dismiss();
            }
        }
    }

    private final void onThree() {
        if (!this.mControllers.containsKey(3) || this.mControllers.get(3) == null) {
            Map<Integer, BaseMeetingViewController> map = this.mControllers;
            ViewGroup viewGroup = this.mContainer;
            AUICallNVNModel meetingCallModel = this.bizModel;
            map.put(3, new MemberThreeViewController(viewGroup, meetingCallModel));
        }
        for (Integer num : this.mControllers.keySet()) {
            int k = num.intValue();
            if (k == 3) {
                BaseMeetingViewController baseMeetingView = this.mControllers.get(Integer.valueOf(k));
                View view = this.mContentView;
                Context context = view.getContext();
                baseMeetingView.show(context);
            } else {
                BaseMeetingViewController baseMeetingView2 = this.mControllers.get(Integer.valueOf(k));
                baseMeetingView2.dismiss();
            }
        }
    }

    private final void onFour() {
        if (!this.mControllers.containsKey(4) || this.mControllers.get(4) == null) {
            Map<Integer, BaseMeetingViewController> map = this.mControllers;
            ViewGroup viewGroup = this.mContainer;
            AUICallNVNModel meetingCallModel = this.bizModel;
            map.put(4, new MemberFourViewController(viewGroup, meetingCallModel));
        }
        for (Integer num : this.mControllers.keySet()) {
            int k = num.intValue();
            if (k == 4) {
                BaseMeetingViewController baseMeetingView = this.mControllers.get(Integer.valueOf(k));
                View view = this.mContentView;
                Context context = view.getContext();
                baseMeetingView.show(context);
            } else {
                BaseMeetingViewController baseMeetingView2 = this.mControllers.get(Integer.valueOf(k));
                baseMeetingView2.dismiss();
            }
        }
    }

    private final void onMore() {
        if (!this.mControllers.containsKey(100) || this.mControllers.get(100) == null) {
            Map<Integer, BaseMeetingViewController> map = this.mControllers;
            ViewGroup viewGroup = this.mContainer;
            AUICallNVNModel meetingCallModel = this.bizModel;
            map.put(100, new MemberPagerViewController(viewGroup, meetingCallModel));
        }
        for (Integer num : this.mControllers.keySet()) {
            int k = num.intValue();
            if (k == 100) {
                BaseMeetingViewController baseMeetingView = this.mControllers.get(Integer.valueOf(k));
                View view = this.mContentView;
                Context context = view.getContext();
                baseMeetingView.show(context);
            } else {
                BaseMeetingViewController baseMeetingView2 = this.mControllers.get(Integer.valueOf(k));
                baseMeetingView2.dismiss();
            }
        }
    }

    public final void updateMeetingView() {
        Map<String, UserInfo> meetingMembers;
        if (this.mInFront) {
            int count = bizModel.getMeetingMembers().size();
            switch (count){
                case 0:
                    onSingle();
                    break;
                case 1:
                    onTwo();
                    break;
                case 2:
                    onThree();
                    break;
                case 3:
                    onFour();
                    break;
                default:
                    onMore();
                    break;
            }
        }
    }

    private final void hideAllMeetingView() {
        for (Integer num : this.mControllers.keySet()) {
            int k = num.intValue();
            BaseMeetingViewController baseMeetingView = this.mControllers.get(Integer.valueOf(k));
            baseMeetingView.dismiss();
        }
    }

    public final void resetState() {
        this.mMemberCount = 0;
    }

    public final void inBack() {
        this.mInFront = false;
        hideAllMeetingView();
    }

    public final void inFront() {
        this.mInFront = true;
        AUICallNVNModel meetingCallModel = this.bizModel;
        if (meetingCallModel != null) {
            this.mMemberCount = meetingCallModel.getMeetingMembers().size();
        }
        updateMeetingView();
    }
}
