package com.aliyun.auikits.auicall.fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aliyun.auikits.auicall.util.AUICallConfig;
import com.aliyun.auikits.auicall.R;

public abstract class UserLoginFragment extends BaseFragment {

    private Callback callback;

    private View mClear;

    private TextView mInputStat;

    private View mLoadingMask;

    private View mLogin;

    private EditText mMyId;

    public interface Callback {
        void onActionLogin(String str);

        void onUserLoginBackPress();
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.fragment_login;
    }

    @Override 
    public void onInitContentView(View rootView) {
        this.mMyId = (EditText) rootView.findViewById(R.id.user_id_input);
        this.mLogin = rootView.findViewById(R.id.login_btn);
        this.mClear = rootView.findViewById(R.id.user_input_clear);
        this.mInputStat = (TextView) rootView.findViewById(R.id.user_input_stat);
        mLoadingMask = rootView.findViewById(R.id.loading_mask);
        mLoadingMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mClear.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                if (mMyId == null) {
                    return;
                }
                mMyId.setText("");
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view3) {
                if("".equals(mMyId.getText().toString().trim())){
                    return;
                }
                if(!getUserIdFromSPF().equals(mMyId.getText().toString().trim())){
                    saveUserIdToSPF(mMyId.getText().toString().trim());
                }
                mLoadingMask.setVisibility(View.VISIBLE);
                if (callback == null) {
                    return;
                }
                callback.onActionLogin(getUserIdFromSPF());
            }
        });
        mMyId.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(AUICallConfig.MAX_ID_LEN)});
        mMyId.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void beforeTextChanged( CharSequence s, int start, int count, int after) {
            }

            @Override 
            public void onTextChanged( CharSequence s, int start, int before, int count) {
            }

            @Override 
            public void afterTextChanged( Editable s) {
                mInputStat.setText((s == null ? 0 : s.length()) + "/15");
                if (s != null && s.length() > 0) {
                    mClear.setVisibility(View.VISIBLE);
                }else{
                    mClear.setVisibility(View.GONE);
                }
            }
        });
        if (!TextUtils.isEmpty(getUserIdFromSPF())) {
            mMyId.setText(getUserIdFromSPF());
        }
    }

    @Override 
    public void onBackPressed() {
        super.onBackPressed();
        if (callback == null) {
            return;
        }
        callback.onUserLoginBackPress();
    }

    public final boolean isLogining() {
        return mLoadingMask != null && mLoadingMask.getVisibility() == View.VISIBLE;
    }

    public final void onLoginFail() {
        if (mLoadingMask == null) {
            return;
        }
        mLoadingMask.setVisibility(View.GONE);
    }
}
