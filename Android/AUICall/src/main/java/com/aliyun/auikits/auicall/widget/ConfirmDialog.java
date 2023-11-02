package com.aliyun.auikits.auicall.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.aliyun.auikits.auicall.R;
public final class ConfirmDialog extends Dialog {

    private Callback callback;

    private TextView mCancelBtn;

    private TextView mConfirmBtn;

    private TextView mTips;

    private TextView mTitle;

    public interface Callback {
        void onActionCancel( Dialog dialog);

        void onActionConfirm( Dialog dialog);
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    public ConfirmDialog( Context context) {
        super(context);
        setContentView(R.layout.confirm_dialog_layout);
        View findViewById = findViewById(R.id.confirm);
        this.mConfirmBtn = (TextView) findViewById;
        View findViewById2 = findViewById(R.id.cancel);
        this.mCancelBtn = (TextView) findViewById2;
        View findViewById3 = findViewById(R.id.tips);
        this.mTips = (TextView) findViewById3;
        View findViewById4 = findViewById(R.id.title);
        this.mTitle = (TextView) findViewById4;
        TextView textView = this.mConfirmBtn;
        textView.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (callback == null) {
                    return;
                }
                callback.onActionConfirm(ConfirmDialog.this);
            }
        });
        TextView textView2 = this.mCancelBtn;
        textView2.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (callback == null) {
                    return;
                }
                callback.onActionCancel(ConfirmDialog.this);
            }
        });
    }

    public final void setTitle( String str) {
        TextView textView = this.mTitle;
        if (textView == null) {
            return;
        }
        textView.setText(str);
    }

    public final void setContent( String str) {
        TextView textView = this.mTips;
        if (textView == null) {
            return;
        }
        textView.setText(str);
    }
}
