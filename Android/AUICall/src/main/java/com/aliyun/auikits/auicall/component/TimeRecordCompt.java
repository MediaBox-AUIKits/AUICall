package com.aliyun.auikits.auicall.component;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
public final class TimeRecordCompt {

    private WeakReference<TextView> mTimeViewRef;

    private Timer mTimer;

    private Handler mUIHandler;

    private Long startTime;

    public TimeRecordCompt(TextView view) {
        this.mTimeViewRef = new WeakReference<>(view);
        this.mUIHandler = new Handler(Looper.getMainLooper());
    }

    public final void startTimeRecord() {
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
            this.mTimer = null;
        }
        this.mTimer = new Timer();
        if (this.startTime == null) {
            this.startTime = Long.valueOf(System.currentTimeMillis());
        }
        mTimer.schedule(new TimerTask() { 
            @Override 
            public void run() {
                TimeRecordCompt.this.updateTimeStamp();
            }
        }, 0L, 1000L);
    }

    public final void updateTimeStamp() {
        this.mUIHandler.post(new Runnable() { 
            @Override 
            public final void run() {
                TextView textView = mTimeViewRef.get();
                if (startTime != null && textView != null) {
                    long currentTimeMillis = System.currentTimeMillis();
                    long duration = currentTimeMillis - startTime.longValue();
                    long seconds = duration / 1000;
                    if (seconds < 3600) {
                        long j = 60;
                        String format = String.format("%02d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(seconds / j), Long.valueOf(seconds % j)}, 2));
                        textView.setText(format);
                    }else{
                        long j2 = 3600;
                        long j3 = 60;
                        String format = String.format("%02d:%02d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(seconds / j2), Long.valueOf((seconds - j2) / j3), Long.valueOf((seconds - j2) % j3)}, 3));
                        textView.setText(format);
                    }
                }
            }
        });
    }

    public final void stopTimeRecord() {
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
        }
        this.mTimer = null;
        this.startTime = null;
    }

    public final void updateRecordStartTime( Long time) {
        if (time == null) {
            return;
        }
        this.startTime = time;
    }


    public final Long getRecordStartTime() {
        return this.startTime;
    }
}
