package com.aliyun.auikits.auicall.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.aliyun.auikits.auicall.R;
public final class AUICallNVNCreateRoomFragment extends BaseFragment {

    private Callback callback;
    private boolean mCameraOn;

    private Switch mCameraSwitch;

    private View mCopyBtn;

    private View mJoinBtn;

    private Switch mLoudspeakerSwitch;
    private boolean mMicOn;

    private Switch mMicSwitch;

    private TextView mMyId;

    private String mRoomId;

    private TextView mRoomIdInput;

    public interface Callback {
        void onActionJoinRoom( String str, boolean z, boolean z2);

        void onActionJoinRoomLoudspeaker(boolean z);
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.fragment_create_room;
    }

    @Override 
    public String getTitle() {
        return "创建房间";
    }

    @Override 
    public void onInitContentView( View rootView) {
        this.mRoomIdInput = (TextView) rootView.findViewById(R.id.room_id_input);
        this.mCameraSwitch = (Switch) rootView.findViewById(R.id.camera_switch);
        this.mMicSwitch = (Switch) rootView.findViewById(R.id.mic_switch);
        this.mLoudspeakerSwitch = (Switch) rootView.findViewById(R.id.loudspeaker_switch);
        this.mMyId = (TextView) rootView.findViewById(R.id.my_id);
        this.mJoinBtn = rootView.findViewById(R.id.join_room_btn);
        this.mCopyBtn = rootView.findViewById(R.id.copy);
        mRoomIdInput.setText(mRoomId != null ? mRoomId : "");
        mCopyBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                ClipboardManager clipBoard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("id", mRoomIdInput.getText().toString());
                clipBoard.setPrimaryClip(clipData);
                Toast.makeText(requireContext(), "已复制", Toast.LENGTH_SHORT).show();
            }
        });
        mMyId.setText("我的ID:" + getUserIdFromSPF());
        mCameraSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { 
            @Override 
            public final void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCameraOn = isChecked;
            }
        });
        mMicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { 
            @Override 
            public final void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mMicOn = isChecked;
            }
        });
        mLoudspeakerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { 
            @Override 
            public final void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (callback == null) {
                    return;
                }
                callback.onActionJoinRoomLoudspeaker(isChecked);
            }
        });
        mJoinBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view3) {
            String roomId = mRoomIdInput.getText().toString();
            if (!TextUtils.isEmpty(roomId) && callback != null) {
                callback.onActionJoinRoom(roomId, mCameraOn, mMicOn);
            }
            }
        });
    }

    public final void updateRoomId( String roomId) {
        this.mRoomId = roomId;
        if(mRoomIdInput != null){
            mRoomIdInput.setText(roomId);
        }
    }

    public final void resetState() {
        if (mCameraSwitch != null) {
            mCameraSwitch.setChecked(false);
        }
        if (mMicSwitch != null) {
            mMicSwitch.setChecked(false);
        }
        if (mRoomIdInput != null) {
            mRoomIdInput.setText("");
        }
        this.mRoomId = null;
    }
}
