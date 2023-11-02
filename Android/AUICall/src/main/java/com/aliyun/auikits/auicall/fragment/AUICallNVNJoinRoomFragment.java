package com.aliyun.auikits.auicall.fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.aliyun.auikits.auicall.R;
public final class AUICallNVNJoinRoomFragment extends BaseFragment {

    private static final int MAX_ROOMID_LEN = 40;

    private Callback callback;
    private boolean mCameraOn;

    private Switch mCameraSwitch;

    private View mInputClear;

    private TextView mInputStat;

    private View mJoinBtn;

    private Switch mLoudspeakerSwitch;
    private boolean mMicOn;

    private Switch mMicSwitch;

    private TextView mMyId;

    private EditText mRoomIdInput;

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
        return R.layout.fragment_join_room;
    }

    @Override 
    public String getTitle() {
        return "加入房间";
    }

    @Override 
    public void onInitContentView( View rootView) {
        this.mRoomIdInput = (EditText) rootView.findViewById(R.id.room_id_input);
        this.mInputStat = (TextView) rootView.findViewById(R.id.room_input_stat);
        this.mInputClear = rootView.findViewById(R.id.room_input_clear);
        this.mCameraSwitch = (Switch) rootView.findViewById(R.id.camera_switch);
        this.mMicSwitch = (Switch) rootView.findViewById(R.id.mic_switch);
        this.mLoudspeakerSwitch = (Switch) rootView.findViewById(R.id.loudspeaker_switch);
        this.mMyId = (TextView) rootView.findViewById(R.id.my_id);
        this.mJoinBtn = rootView.findViewById(R.id.join_room_btn);
        this.mInputClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                if (mRoomIdInput == null) {
                    return;
                }
                mRoomIdInput.setText("");
            }
        });
        mRoomIdInput.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(MAX_ROOMID_LEN)});
        mRoomIdInput.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void beforeTextChanged( CharSequence s, int start, int count, int after) {
            }

            @Override 
            public void onTextChanged( CharSequence s, int start, int before, int count) {
            }

            @Override 
            public void afterTextChanged( Editable s) {
                if (mInputStat != null) {
                    mInputStat.setText((s == null ? 0 : s.length()) + "/40");
                }
                if (s != null && s.length() > 0) {
                    mInputClear.setVisibility(View.VISIBLE);
                    return;
                }
                mInputClear.setVisibility(View.GONE);
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
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                mMicOn = z;
            }
        });
        mLoudspeakerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { 
            @Override 
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (callback == null) {
                    return;
                }
                callback.onActionJoinRoomLoudspeaker(z);
            }
        });
        mJoinBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                String roomId = mRoomIdInput.getText().toString();
                if (!TextUtils.isEmpty(roomId) && callback != null) {
                    callback.onActionJoinRoom(roomId, mCameraOn, mMicOn);
                }
            }
        });
    }

    public final void resetState() {
        if (mMicSwitch != null) {
            mMicSwitch.setChecked(false);
        }
        if (mCameraSwitch != null) {
            mCameraSwitch.setChecked(false);
        }
    }
}
