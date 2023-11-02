package com.aliyun.auikits.auicall.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.auikits.auicall.adapter.MemberListAdapter;
import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.widget.ConfirmDialog;
import com.aliyun.auikits.auicall.R;

public final class AUICallNVNMemberListFragment extends BaseFragment implements MemberListAdapter.Callback, ConfirmDialog.Callback {

    private Callback callback;
    private boolean mAllMute = false;

    private ConfirmDialog mDialog;

    private ImageView mHostCameraState;

    private ImageView mHostIcon;

    private View mHostInfoContainer;

    private View mHostInviteUserBtn;

    private ImageView mHostMicState;

    private TextView mHostMuteAllBtn;

    private TextView mHostName;

    private View mHostOperateArea;

    private String mKickOutUserId;

    private MemberListAdapter mMemberAdapter;

    private RecyclerView mMemberListView;

    private View mNormalInviteUserBtn;

    public interface Callback {
        void onActionCancelInvite( String str);

        void onInviteBtnClick();
    }


    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.fragment_member_list;
    }

    @Override 
    public String getTitle() {
        return "成员列表";
    }

    @Override 
    public void onInitContentView( View rootView) {
        mMemberListView = (RecyclerView) rootView.findViewById(R.id.member_list);
        mMemberAdapter = new MemberListAdapter();
        mMemberAdapter.setCallback(this);
        mMemberAdapter.setBizModel(getBizModel());
        mMemberListView.setAdapter(this.mMemberAdapter);
        mMemberListView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mHostIcon = (ImageView) rootView.findViewById(R.id.host_icon);
        mHostName = (TextView) rootView.findViewById(R.id.host_name);
        mHostCameraState = (ImageView) rootView.findViewById(R.id.host_camera_state);
        mHostMicState = (ImageView) rootView.findViewById(R.id.host_mic_state);
        mNormalInviteUserBtn = rootView.findViewById(R.id.normal_invite_user_btn);
        mNormalInviteUserBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (callback == null) {
                    return;
                }
                callback.onInviteBtnClick();
            }
        });
        mHostInviteUserBtn  = rootView.findViewById(R.id.host_invite_user_btn);
        mHostInviteUserBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (callback == null) {
                    return;
                }
                callback.onInviteBtnClick();
            }
        });
        mHostMuteAllBtn = (TextView) rootView.findViewById(R.id.mute_all_btn);
        mHostMuteAllBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (bizModel == null) {
                    return;
                }
                mAllMute = !mAllMute;
                if (mAllMute) {
                    bizModel.muteAll();
                    mHostMuteAllBtn.setText("解除全员静音");
                    mHostMuteAllBtn.setTextColor(Color.parseColor("#F53F3F"));
                    Toast.makeText(requireContext(), R.string.host_mute_mic_own_tips, Toast.LENGTH_SHORT).show();
                    return;
                }
                bizModel.unMuteAll();
                mHostMuteAllBtn.setText("全员静音");
                mHostMuteAllBtn.setTextColor(Color.parseColor("#FCFCFD"));
                Toast.makeText(requireContext(), R.string.host_unmute_mic_own_tips, Toast.LENGTH_SHORT).show();
            }
        });
        mHostOperateArea = rootView.findViewById(R.id.host_operate_area);
        mHostInfoContainer = rootView.findViewById(R.id.host_info_container);
    }

    public final void onShow() {
        if (this.isAdded()) {
            if (bizModel != null && bizModel.isHost()) {
                mNormalInviteUserBtn.setVisibility(View.INVISIBLE);
                mHostOperateArea.setVisibility(View.VISIBLE);
            } else {
                mNormalInviteUserBtn.setVisibility(View.VISIBLE);
                mHostOperateArea.setVisibility(View.GONE);
            }
            updateHost();
            mMemberAdapter.notifyData();
        }
    }

    private final void updateHost() {
        UserInfo host = bizModel == null ? null : bizModel.getHost();
        if (host != null) {
            mHostInfoContainer.setVisibility(View.VISIBLE);
            mHostName.setText(host.getUserId());
            if (host.getCameraOn()) {
                mHostCameraState.setImageResource(R.mipmap.camera_open_icon);
            } else {
                mHostCameraState.setImageResource(R.mipmap.camera_close_icon);
            }
            if (host.getMicOn()) {
                mHostMicState.setImageResource(R.mipmap.mic_open_icon);
            }else{
                mHostMicState.setImageResource(R.mipmap.mic_close_icon);
            }
        }else{
            mHostInfoContainer.setVisibility(View.GONE);
        }
    }

    public final void onMemberChanged( String userId) {
        UserInfo host = bizModel == null ? null : bizModel.getHost();
        if (host == null || host.getUserId().equals(userId)) {
            updateHost();
            return;
        }
        mMemberAdapter.notifyData();
    }

    @Override 
    public void onActionCancelInvite( String userId) {
        if (callback == null) {
            return;
        }
        callback.onActionCancelInvite(userId);
    }

    @Override 
    public void onActionKickOut( String userId) {
        if (this.mDialog == null) {
            Context requireContext = requireContext();
            mDialog = new ConfirmDialog(requireContext);
            mDialog.setCallback(this);
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { 
                @Override 
                public final void onDismiss(DialogInterface dialogInterface) {
                    mKickOutUserId = null;
                }
            });
        }
        this.mKickOutUserId = userId;
        mDialog.setTitle("移除成员");
        mDialog.setContent("您确定要移除【" + userId + "】吗？\n移除后操作不可恢复。");
        mDialog.show();
    }

    @Override 
    public void onActionConfirm( Dialog dialog) {
        if (this.mKickOutUserId != null && bizModel != null) {
            bizModel.kickOut(mKickOutUserId);
        }
        mDialog.dismiss();
    }

    @Override 
    public void onActionCancel( Dialog dialog) {
        mDialog.dismiss();
    }
}
