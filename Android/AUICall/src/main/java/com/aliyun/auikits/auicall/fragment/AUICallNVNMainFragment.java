package com.aliyun.auikits.auicall.fragment;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.controller.meeting.CallMiddlePanelController;
import com.aliyun.auikits.auicall.controller.meeting.CallOptionPanelController;
import com.aliyun.auikits.auicall.controller.meeting.CallTopPanelController;
import com.aliyun.auikits.auicall.widget.ConfirmNotitleDialog;
import com.aliyun.auikits.auicall.R;

public final class AUICallNVNMainFragment extends BaseFragment implements CallOptionPanelController.Callback, CallTopPanelController.Callback, ConfirmNotitleDialog.Callback {

    private static final String TAG = "MeetingCallFragment";

    private Callback callback;

    private SeekBar mBeautySkinBlurBar;

    private TextView mBeautySkinBlurHint;

    private SeekBar mBeautyWhiteBar;

    private TextView mBeautyWhiteHint;

    private PopupWindow mBeautyWin;

    private CallOptionPanelController mBottomPanel;

    private ConfirmNotitleDialog mDialog;

    private CallMiddlePanelController mMiddlePanel;

    private View mMirror;

    private PopupWindow mMoreActionWin;

    private View mSwitchCamera;

    private CallTopPanelController mTopPanel;

    public interface Callback {
        void onShowMemberList();
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.fragment_meeting_call;
    }

    @Override 
    public String getTitle() {
        return "";
    }

    @Override 
    public void onInitContentView( View rootView) {
        ViewGroup bottomContainer = (ViewGroup) rootView.findViewById(R.id.bottom_panel);
        ViewGroup topContainer = (ViewGroup) rootView.findViewById(R.id.top_panel);
        ViewGroup middleContainer = (ViewGroup) rootView.findViewById(R.id.middle_panel);
        mBottomPanel = new CallOptionPanelController();
        mBottomPanel.setCallback(this);
        mBottomPanel.setBizModel(getBizModel());
        Context requireContext = requireContext();
        mBottomPanel.inflate(requireContext, bottomContainer);

        mTopPanel = new CallTopPanelController();
        mTopPanel.setBizModel(getBizModel());
        mTopPanel.setCallback(this);
        mTopPanel.inflate(requireContext, topContainer);

        mMiddlePanel = new CallMiddlePanelController();
        mMiddlePanel.setBizModel(getBizModel());
        mMiddlePanel.inflate(requireContext, middleContainer);
    }

    public final void onJoin( String roomId) {
        if (mBottomPanel != null) {
            mBottomPanel.resetState();
            mBottomPanel.onUserOnline();
            mBottomPanel.updateCurrent();
        }
        if (mTopPanel != null) {
            mTopPanel.resetState();
            mTopPanel.setTitle(roomId);
            mTopPanel.startTimeRecord();
        }
        if (mMiddlePanel != null) {
            mMiddlePanel.resetState();
            mMiddlePanel.updateMeetingView();
        }
    }

    public final void onLeave() {
        if (mBottomPanel != null) {
            mBottomPanel.onUserOffline();
            mBottomPanel.resetState();
        }
        if (mTopPanel != null) {
            mTopPanel.resetState();
            mTopPanel.stopTimeRecord();
        }
        if (mMiddlePanel != null) {
            mMiddlePanel.resetState();
        }
    }

    public final void onUserJoin( String userId) {
        if (mBottomPanel != null) {
            mBottomPanel.onUserOnline();
        }
        if (mMiddlePanel != null) {
            mMiddlePanel.notifyMemberChange();
        }
    }

    public final void onUserLeave( String userId) {
        if (mBottomPanel != null) {
            mBottomPanel.onUserOffline();
        }
        if (mMiddlePanel != null) {
            bizModel.setViewContainer(userId, null, false);
            mMiddlePanel.notifyMemberChange();
        }
    }

    public final void onCameraOn( String userId) {
        if(bizModel == null) return;
        if (bizModel.getCurrentUser() != null && bizModel.getCurrentUser().getUserId().equals(userId) && this.mBottomPanel != null) {
            mBottomPanel.onCameraOn();
        }
        if (mMiddlePanel != null) {
            mMiddlePanel.updateMeetingView();
        }
    }

    public final void onCameraOff( String userId) {
        if(bizModel == null) return;
        if (bizModel.getCurrentUser() != null && bizModel.getCurrentUser().getUserId().equals(userId) && this.mBottomPanel != null) {
            mBottomPanel.onCameraOff();
        }
        if (mMiddlePanel != null) {
            mMiddlePanel.updateMeetingView();
        }
    }

    public final void onMicOn( String userId) {
        if(bizModel == null) return;
        if (bizModel.getCurrentUser() != null && bizModel.getCurrentUser().getUserId().equals(userId) && this.mBottomPanel != null) {
            mBottomPanel.onMicOn();
        }
        if (mMiddlePanel != null) {
            mMiddlePanel.updateMeetingView();
        }
    }

    public final void onMicOff( String userId) {
        if(bizModel == null) return;
        if (bizModel.getCurrentUser() != null && bizModel.getCurrentUser().getUserId().equals(userId) && this.mBottomPanel != null) {
            mBottomPanel.onMicOff();
        }
        if (mMiddlePanel != null) {
            mMiddlePanel.updateMeetingView();
        }
    }

    public final void onUpdateHost( UserInfo host) {
        if (mMiddlePanel != null) {
            mMiddlePanel.updateMeetingView();
        }
    }

    @Override 
    public void onBeautyClick(boolean show) {
        Toast.makeText(requireContext(), "该功能暂未支持", Toast.LENGTH_SHORT).show();
    }

    @Override 
    public void onMemberClick() {
        if (callback == null) {
            return;
        }
        callback.onShowMemberList();
    }

    @Override 
    public void onOpenMic(boolean open) {
        if (bizModel != null && bizModel.getCurrentUser() != null) {
            if (bizModel.isMuteAll() && !bizModel.isHost()) {
                Toast.makeText(requireContext(), R.string.host_mute_tips, Toast.LENGTH_SHORT).show();
                return;
            }
            bizModel.openMic(bizModel.getCurrentUser().getUserId(), open);
        }
    }

    @Override 
    public void onMoreClick(boolean show) {
        if (this.isAdded()) {
            if (this.mMoreActionWin == null) {
                View view = LayoutInflater.from(requireContext()).inflate(R.layout.more_options_layout, (ViewGroup) null);
                mMoreActionWin  = new PopupWindow(view);
                mMoreActionWin.setWidth(-1);
                mMoreActionWin.setHeight((int) ((100 * requireContext().getResources().getDisplayMetrics().density) + 0.5f));
                mMoreActionWin.setOutsideTouchable(true);
                mSwitchCamera = view.findViewById(R.id.switch_camera);
                mMirror = view.findViewById(R.id.mirror);
                mMirror.setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public final void onClick(View view2) {
                        if (bizModel == null) {
                            return;
                        }
                        bizModel.toggleMirror();
                    }
                });
                mSwitchCamera.setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public final void onClick(View view3) {
                        if (bizModel == null) {
                            return;
                        }
                        bizModel.switchCamera();
                    }
                });
            }
            mMoreActionWin.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        }
    }

    @Override 
    public void onActionLeave() {
        if(bizModel == null) return;
        if (this.mDialog == null) {
            Context requireContext = requireContext();
            mDialog = new ConfirmNotitleDialog(requireContext);
            mDialog.setCallback(this);
        }
        if (bizModel.isHost()) {
            mDialog.setContent("您确定结束会议？");
        } else {
            mDialog.setContent("您确定离开会议？");
        }
        mDialog.show();
    }

    @Override 
    public void onActionConfirm( Dialog dialog) {
        if(bizModel == null) return;
        if (bizModel.isHost()) {
            bizModel.dismiss();
        } else {
            bizModel.leave();
        }
        mDialog.dismiss();
    }

    @Override 
    public void onActionCancel( Dialog dialog) {
        mDialog.dismiss();
    }

    @Override 
    public void onBackPressed() {
        onActionLeave();
    }

    @Override 
    public void inBack() {
        super.inBack();
        if (mMiddlePanel == null) {
            return;
        }
        mMiddlePanel.inBack();
    }

    @Override 
    public void inFront() {
        super.inFront();
        if (mMiddlePanel == null) {
            return;
        }
        mMiddlePanel.inFront();
    }
}
