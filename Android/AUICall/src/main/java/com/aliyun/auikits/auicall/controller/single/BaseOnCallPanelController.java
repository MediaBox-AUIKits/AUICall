package com.aliyun.auikits.auicall.controller.single;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aliyun.auikits.auicall.model.AUICall1V1Model;
import com.aliyun.auikits.auicall.R;
import com.aliyun.auikits.auicall.component.TimeRecordCompt;

public abstract class BaseOnCallPanelController extends BaseCallPanelController {
    private TextView mTimeRecord;

    private TimeRecordCompt mTimeRecordComp;

    public abstract void toggleOperationsVisibility();

    public BaseOnCallPanelController(AUICall1V1Model callModel) {
        super(callModel);
    }

    @Override 
    public View inflate( Context ctx,  View container) {
        super.inflate(ctx, container);
        this.mTimeRecord = rootView == null ? null : (TextView) rootView.findViewById(R.id.time_record);
        this.mTimeRecordComp = new TimeRecordCompt(mTimeRecord);
        return rootView;
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

    public final void updateRecordStartTime( Long time) {
        TimeRecordCompt timeRecordCompt = this.mTimeRecordComp;
        if (timeRecordCompt == null) {
            return;
        }
        timeRecordCompt.updateRecordStartTime(time);
    }


    public final Long getRecordStartTime() {
        TimeRecordCompt timeRecordCompt = this.mTimeRecordComp;
        if (timeRecordCompt == null) {
            return null;
        }
        return timeRecordCompt.getRecordStartTime();
    }

    @Override 
    public void resetState() {
        super.resetState();
        stopTimeRecord();
    }
}
