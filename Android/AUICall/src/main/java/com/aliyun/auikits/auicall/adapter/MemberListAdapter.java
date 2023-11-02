package com.aliyun.auikits.auicall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.R;
import com.aliyun.auikits.auicall.bean.UserInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {
    
    private AUICallNVNModel bizModel;
    
    private Callback callback;
    
    private List<UserInfo> mUserList;

    public interface Callback {
        void onActionCancelInvite( String str);

        void onActionKickOut( String str);
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

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        
        private WeakReference<MemberListAdapter> mAdapterRef;
        
        private AUICallNVNModel mBizModel;
        
        private View mCancelBtn;
        
        private View mKickOutBtn;
        
        private ImageView mUserCameraState;
        
        private UserInfo mUserInfo;
        
        private ImageView mUserMicState;
        
        private TextView mUserName;
        
        private View mUserState;

        public ViewHolder(AUICallNVNModel model, View contentView, MemberListAdapter adapter) {
            super(contentView);
            this.mUserName = contentView.findViewById(R.id.user_name);
            this.mUserCameraState = contentView.findViewById(R.id.user_camera_state);
            this.mUserMicState = contentView.findViewById(R.id.user_mic_state);
            this.mKickOutBtn = contentView.findViewById(R.id.kick_out_btn);
            this.mCancelBtn = contentView.findViewById(R.id.cancel_invite_btn);
            this.mUserState = contentView.findViewById(R.id.user_state);
            this.mAdapterRef = new WeakReference<>(adapter);
            this.mBizModel = model;
            this.mUserCameraState.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public final void onClick(View view) {
                    onCameraClick();
                }
            });
            this.mUserMicState.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public final void onClick(View view) {
                    onMicClick();
                }
            });
            this.mKickOutBtn.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public final void onClick(View view2) {
                    onKickout();
                }
            });
            this.mCancelBtn.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public final void onClick(View view3) {
                    onCancel();
                }
            });
        }

        
        /* renamed from: _init_$lambda-0  reason: not valid java name */
        private void onCameraClick() {
            if (mUserInfo == null || mBizModel == null) {
                return;
            }
            if (!mBizModel.isHost()) {
                return;
            }
            boolean cameraOn = !mUserInfo.getCameraOn();
            if (cameraOn) {
                mBizModel.openCamera(mUserInfo.getUserId(), true);
            }else{
                mBizModel.openCamera(mUserInfo.getUserId(), false);
            }
        }

        private void onMicClick() {
            if (mUserInfo == null || mBizModel == null) {
                return;
            }
            if (!mBizModel.isHost()) {
                return;
            }
            boolean micOn = !mUserInfo.getMicOn();
            if (micOn) {
                mBizModel.openMic(mUserInfo.getUserId(), true);
            }else{
                mBizModel.openMic(mUserInfo.getUserId(), false);
            }
        }

        private void onKickout() {
            if (mUserInfo == null || mBizModel == null || !mBizModel.isHost()) {
                return;
            }
            MemberListAdapter memberListAdapter = mAdapterRef.get();
            if (memberListAdapter != null && memberListAdapter.getCallback() != null) {
                memberListAdapter.getCallback().onActionKickOut(mUserInfo.getUserId());
            }
        }

        private void onCancel() {
            if (mUserInfo == null || mBizModel == null) {
                return;
            }
            mBizModel.cancelInvite(mUserInfo.getUserId(), false);
            MemberListAdapter memberListAdapter = mAdapterRef.get();
            if (memberListAdapter != null && memberListAdapter.getCallback() != null) {
                memberListAdapter.getCallback().onActionCancelInvite(mUserInfo.getUserId());
            }
        }

        public final void bindData( UserInfo user) {
            if (user == null || mBizModel == null) {
                return;
            }
            this.mUserInfo = user;
            this.mUserName.setText(user.getUserId());
            if (user.isCalling()) {
                this.mCancelBtn.setVisibility(View.VISIBLE);
                this.mKickOutBtn.setVisibility(View.GONE);
                this.mUserState.setVisibility(View.VISIBLE);
                this.mUserCameraState.setVisibility(View.GONE);
                this.mUserMicState.setVisibility(View.GONE);
                return;
            }
            this.mCancelBtn.setVisibility(View.GONE);
            this.mUserState.setVisibility(View.GONE);
            this.mUserCameraState.setVisibility(View.VISIBLE);
            this.mUserMicState.setVisibility(View.VISIBLE);
            UserInfo host = mBizModel.getHost();
            String hostId = host == null ? null : host.getUserId();
            UserInfo currentUser = mBizModel.getCurrentUser();
            if(currentUser == null){
                return;
            }
            String currentUserId = currentUser.getUserId();
            if (hostId != null && hostId.equals(currentUserId)) {
                this.mKickOutBtn.setVisibility(View.VISIBLE);
            } else {
                this.mKickOutBtn.setVisibility(View.GONE);
            }
            if (user.getMicOn()) {
                this.mUserMicState.setImageResource(R.mipmap.mic_open_icon);
            } else {
                this.mUserMicState.setImageResource(R.mipmap.mic_close_icon);
            }
            if (user.getCameraOn()) {
                this.mUserCameraState.setImageResource(R.mipmap.camera_open_icon);
            } else {
                this.mUserCameraState.setImageResource(R.mipmap.camera_close_icon);
            }
        }
    }

    @Override 
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        if(bizModel == null) return null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item_layout, parent, false);
        return new ViewHolder(bizModel, view, this);
    }

    @Override 
    public void onBindViewHolder( ViewHolder holder, int position) {
        if(mUserList != null && mUserList.size() > position){
            holder.bindData(mUserList.get(position));
        }
    }

    @Override 
    public int getItemCount() {
        return mUserList != null ? mUserList.size() : 0;
    }

    public final void notifyData() {
        if(bizModel == null) return;
        Map<String, UserInfo> userMap = new HashMap<>(bizModel.getMeetingMembers());
        if (bizModel.getCurrentUser() != null) {
            UserInfo currentUser = bizModel.getCurrentUser();
            userMap.put(currentUser.getUserId(), currentUser);
        }
        if (bizModel.getHost() != null) {
            userMap.remove(bizModel.getHost().getUserId());
        }
        this.mUserList = new ArrayList<>(userMap.values());
        List callingUsers = new ArrayList<>(bizModel.getCallingMembers().values());
        mUserList.addAll(callingUsers);
        notifyDataSetChanged();
    }
}
