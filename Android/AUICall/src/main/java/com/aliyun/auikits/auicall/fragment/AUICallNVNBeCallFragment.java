package com.aliyun.auikits.auicall.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.controller.meeting.BeCallPanelController;
import com.aliyun.auikits.auicall.R;

public final class AUICallNVNBeCallFragment extends Fragment implements BeCallPanelController.Callback {

    private AUICallNVNModel bizModel;

    private Callback callback;

    private View mContentView;

    private BeCallPanelController mMeetingBeCallPanel;

    private ViewGroup mPanelContainer;

    private TextView mTips;

    private ImageView mUserIcon;

    private TextView mUserName;

    public interface Callback {
        void onActionRefuse();
    }

    public final AUICallNVNModel getBizModel() {
        return this.bizModel;
    }

    public final void setBizModel( AUICallNVNModel meetingCallModel) {
        this.bizModel = meetingCallModel;
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    @Override 
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        if (this.mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_meeting_call_becall, container, false);
            View bgMask = mContentView.findViewById(R.id.bg_mask);
            if (bgMask != null) {
                bgMask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
            this.mUserIcon = (ImageView) mContentView.findViewById(R.id.opposite_user_icon);
            this.mUserName = (TextView) mContentView.findViewById(R.id.opposite_user_name);
            this.mTips = (TextView) mContentView.findViewById(R.id.call_tips);
            this.mPanelContainer = (ViewGroup) mContentView.findViewById(R.id.bottom_panel);
        }
        return this.mContentView;
    }

    public final void onInvite( String caller, String roomId) {
        if(TextUtils.isEmpty(caller) || TextUtils.isEmpty(roomId)) return;
        if (this.mMeetingBeCallPanel == null) {
            mMeetingBeCallPanel = new BeCallPanelController(bizModel);
            Context requireContext = requireContext();
            mMeetingBeCallPanel.inflate(requireContext, mPanelContainer);
        }
        mMeetingBeCallPanel.setCaller(caller);
        mMeetingBeCallPanel.setCallback(this);
        mMeetingBeCallPanel.addToContainer(mPanelContainer);
        if (mUserName != null) {
            mUserName.setText(caller);
        }
        if (mTips == null) {
            return;
        }
        mTips.setText("邀请您加入房间【" + roomId + "】");
    }

    @Override 
    public void onHangup() {
        if (callback == null) {
            return;
        }
        callback.onActionRefuse();
    }

    @Override 
    public void onVideoHangon() {
    }

    @Override 
    public void onAudioHangon() {
    }
}
