package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.auikits.auicall.component.TimeRecordCompt;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.R;

public final class CallTopPanelController {

    private AUICallNVNModel bizModel;

    private Callback callback;

    private View mContentView;

    private View mFinishBtn;

    private ImageView mLoudspeakerBtn;
    private boolean mLoudspeakerOn = true;

    private View mMiniWindowBtn;

    private TextView mRoomId;

    private TextView mTimeRecord;

    private TimeRecordCompt mTimeRecordComp;

    public interface Callback {
        void onActionLeave();
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

    public final void inflate( Context ctx,  ViewGroup container) {
        View inflate = LayoutInflater.from(ctx).inflate(R.layout.meeting_call_top_panel, container, true);
        this.mContentView = inflate;
        this.mMiniWindowBtn = inflate.findViewById(R.id.mini_window);
        this.mLoudspeakerBtn = (ImageView) inflate.findViewById(R.id.toggle_loudspeaker_btn);
        this.mRoomId = (TextView) inflate.findViewById(R.id.room_id);
        this.mTimeRecord = (TextView) inflate.findViewById(R.id.time_record);
        this.mFinishBtn = inflate.findViewById(R.id.finish_btn);
        TextView textView = this.mTimeRecord;
        this.mTimeRecordComp = new TimeRecordCompt(textView);
        mFinishBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view6) {
                if (callback == null) {
                    return;
                }
                callback.onActionLeave();
            }
        });
        mLoudspeakerBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view6) {
                mLoudspeakerOn = !mLoudspeakerOn;
                if (bizModel != null) {
                    bizModel.openLoudspeaker(mLoudspeakerOn);
                }
                if (mLoudspeakerOn) {
                    onLoudspeakerOn();
                } else {
                    onLoudspeakerOff();
                }
            }
        });
    }

    public final void setTitle( String title) {
        TextView textView = this.mRoomId;
        textView.setText(title);
    }

    private final void onLoudspeakerOn() {
        ImageView imageView = this.mLoudspeakerBtn;
        if (imageView == null) {
            return;
        }
        imageView.setImageResource(R.mipmap.loudspeaker_on_icon2);
    }

    private final void onLoudspeakerOff() {
        ImageView imageView = this.mLoudspeakerBtn;
        if (imageView == null) {
            return;
        }
        imageView.setImageResource(R.mipmap.loudspeaker_off_icon2);
    }

    public final void startTimeRecord() {
        TimeRecordCompt timeRecordCompt = this.mTimeRecordComp;
        if (timeRecordCompt == null) {
            return;
        }
        timeRecordCompt.startTimeRecord();
    }

    public final void stopTimeRecord() {
        TimeRecordCompt timeRecordCompt = this.mTimeRecordComp;
        if (timeRecordCompt == null) {
            return;
        }
        timeRecordCompt.stopTimeRecord();
    }

    public final void resetState() {
        this.mLoudspeakerOn = true;
        onLoudspeakerOn();
        stopTimeRecord();
    }
}
