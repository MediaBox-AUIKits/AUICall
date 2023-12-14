package com.aliyun.auikits.auicall.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.auikits.auicall.AUICallNVNActivity;
import com.aliyun.auikits.auicall.R;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.model.callback.CreateRoomCallback;
import com.aliyun.auikits.auicall.util.AUICallConfig;
import com.aliyun.auikits.room.util.AliyunLog;

public final class AUICallNVNCreateRoomFragment extends BaseFragment {

    private Callback callback;
    private boolean mCameraOn;

    private Switch mCameraSwitch;

    private View mJoinBtn;

    private Switch mLoudspeakerSwitch;
    private boolean mMicOn;

    private Switch mMicSwitch;

    private TextView mMyId;

    private EditText mRoomIdInput;

    private View mClear;

    public interface Callback {
        void onActionJoinRoom( String str, boolean z, boolean z2);

        void onActionJoinRoomLoudspeaker(boolean z);

        void onCreateRoomFailed(int code, String msg);
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
        this.mRoomIdInput = rootView.findViewById(R.id.room_id_input);
        this.mCameraSwitch = rootView.findViewById(R.id.camera_switch);
        this.mMicSwitch = rootView.findViewById(R.id.mic_switch);
        this.mLoudspeakerSwitch = rootView.findViewById(R.id.loudspeaker_switch);
        this.mMyId = rootView.findViewById(R.id.my_id);
        this.mJoinBtn = rootView.findViewById(R.id.join_room_btn);
        this.mClear = rootView.findViewById(R.id.room_input_clear);
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
            public void onClick(View view3) {
                if(bizModel == null){
                    return;
                }
                String roomId = mRoomIdInput.getText().toString();
                if(TextUtils.isEmpty(roomId) || TextUtils.isEmpty(roomId.trim())){
                    return;
                }
                mClear.setEnabled(false);
                final String finalRoomId = roomId.trim();
                bizModel.create(finalRoomId, new CreateRoomCallback() {
                    @Override
                    public void onError(int i, String str) {
                        mClear.setEnabled(true);
                        if(callback != null){
                            callback.onCreateRoomFailed(i, str);
                        }
                    }

                    @Override
                    public void onSuccess(String str) {
                        mClear.setEnabled(true);
                        if (callback != null) {
                            callback.onActionJoinRoom(finalRoomId, mCameraOn, mMicOn);
                        }
                    }
                });

            }
        });
        this.mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRoomIdInput == null) {
                    return;
                }
                mRoomIdInput.setText("");
            }
        });
    }

    public void resetState() {
        if (mCameraSwitch != null) {
            mCameraSwitch.setChecked(false);
        }
        if (mMicSwitch != null) {
            mMicSwitch.setChecked(false);
        }
        if (mRoomIdInput != null) {
            mRoomIdInput.setText("");
        }
    }
}
