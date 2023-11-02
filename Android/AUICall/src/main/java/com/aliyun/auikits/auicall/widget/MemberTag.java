package com.aliyun.auikits.auicall.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.R;

public final class MemberTag extends LinearLayout {

    private AUICallNVNModel bizModel;

    private View mContentView;

    private View mHostTag;

    private ImageView mMicState;

    private TextView mUserName;

    public MemberTag( Context context) {
        super(context);
    }

    public MemberTag( Context context,  AttributeSet attri) {
        super(context, attri);
    }

    public MemberTag( Context context,  AttributeSet attri, int defStyle) {
        super(context, attri, defStyle);
    }

    public MemberTag( Context context,  AttributeSet attri, int defStyle, int defStyleRes) {
        super(context, attri, defStyle, defStyleRes);
    }

    public final AUICallNVNModel getBizModel() {
        return this.bizModel;
    }

    public final void setBizModel( AUICallNVNModel meetingCallModel) {
        this.bizModel = meetingCallModel;
    }

    @Override 
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mContentView == null) {
            setOrientation(LinearLayout.HORIZONTAL);
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.member_tag, (ViewGroup) this, true);
            this.mContentView = inflate;
            this.mHostTag = inflate.findViewById(R.id.host_tag);
            this.mMicState = (ImageView) inflate.findViewById(R.id.member_mic_state);
            this.mUserName = (TextView) inflate.findViewById(R.id.member_name);
        }
    }

    public final void bindData( UserInfo user) {
        if (user == null) {
            return;
        }
        if (user.isHost()) {
            View view = this.mHostTag;
            if (view != null) {
                view.setVisibility(VISIBLE);
            }
        } else {
            View view2 = this.mHostTag;
            if (view2 != null) {
                view2.setVisibility(GONE);
            }
        }
        if (user.getMicOn()) {
            ImageView imageView = this.mMicState;
            if (imageView != null) {
                imageView.setImageResource(R.mipmap.mic_open_icon_dark);
            }
        } else {
            ImageView imageView2 = this.mMicState;
            if (imageView2 != null) {
                imageView2.setImageResource(R.mipmap.mic_close_icon);
            }
        }
        AUICallNVNModel meetingCallModel = this.bizModel;
        UserInfo currentUser = meetingCallModel == null ? null : meetingCallModel.getCurrentUser();
        if (currentUser != null && TextUtils.equals(currentUser.getUserId(), user.getUserId())) {
            TextView textView = this.mUserName;
            if (textView == null) {
                return;
            }
            textView.setText(getContext().getString(R.string.me));
            return;
        }
        TextView textView2 = this.mUserName;
        if (textView2 == null) {
            return;
        }
        textView2.setText(user.getUserId());
    }
}
