package com.aliyun.auikits.auicall.fragment;

import android.view.View;
import com.aliyun.auikits.auicall.R;
public final class AUICallNVNOptionFragment extends BaseFragment {

    private Callback callback;

    private View mCreateRoom;

    private View mJoinRoom;

    public interface Callback {
        void onActionCreateRoom();

        void onActionJoinRoom();

        void onMeetingOptionBackPress();
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.fragment_meeting_option;
    }

    @Override 
    public String getTitle() {
        return "多人音视频通话";
    }

    @Override 
    public void onInitContentView( View rootView) {
        this.mCreateRoom = rootView.findViewById(R.id.create_room_btn);
        this.mJoinRoom = rootView.findViewById(R.id.join_room_btn);
        mCreateRoom.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                if (callback == null) {
                    return;
                }
                callback.onActionCreateRoom();
            }
        });
        mJoinRoom.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view3) {
                if (callback == null) {
                    return;
                }
                callback.onActionJoinRoom();
            }
        });
    }

    @Override 
    public void onBackPressed() {
        super.onBackPressed();
        if (callback == null) {
            return;
        }
        callback.onMeetingOptionBackPress();
    }
}
