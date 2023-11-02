package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;

import java.util.Comparator;
public abstract class BaseMeetingViewController {

    protected View mContentView;

    protected AUICallNVNModel mMeetingModel;

    protected ViewGroup mRootContainerView;

    public abstract View inflateView( Context context);

    public abstract void updateCurrentMic( UserInfo userInfo);

    public static final class UserPriorityComparator implements Comparator<UserInfo> {

        private final AUICallNVNModel mModel;

        public UserPriorityComparator( AUICallNVNModel model) {
            this.mModel = model;
        }

        @Override 
        public int compare( UserInfo o1, UserInfo o2) {
            if (o1 == null || o2 == null) {
                return 0;
            }
            return convert2Val(o2) - convert2Val(o1);
        }

        private final int convert2Val(UserInfo user) {
            if (user.isHost()) {
                return 2;
            }
            String userId = user.getUserId();
            UserInfo currentUser = this.mModel.getCurrentUser();
            if (TextUtils.equals(userId, currentUser.getUserId())) {
                return 1;
            }
            return 0;
        }
    }

    public BaseMeetingViewController(ViewGroup containerView, AUICallNVNModel model) {
        this.mMeetingModel = model;
        this.mRootContainerView = containerView;
    }

    public final ViewGroup getMRootContainerView() {
        return this.mRootContainerView;
    }

    public void show( Context context) {
        if (this.mContentView == null) {
            this.mContentView = inflateView(context);
        }
        if (this.mRootContainerView.getChildCount() <= 0 || this.mRootContainerView.getChildAt(0) != this.mContentView) {
            this.mRootContainerView.removeAllViews();
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.mRootContainerView.addView(this.mContentView, lp);
        }
    }

    public final void dismiss() {
        if (this.mRootContainerView.getChildCount() > 0) {
            this.mRootContainerView.removeView(this.mContentView);
        }
    }
}
