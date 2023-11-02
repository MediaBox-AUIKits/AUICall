package com.aliyun.auikits.auicall.fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.auikits.auicall.util.AUICallConfig;
import com.aliyun.auikits.auicall.R;

import java.util.ArrayList;
import java.util.List;
public final class AUICallNVNInviteFragment extends BaseFragment {

    private static final int MAX_INPUT_LEN = 15;

    private Callback callback;

    private View mConfirmBtn;

    private EditText mUserInput;

    private View mUserInputClear;

    private TextView mUserInputStat;

    public interface Callback {
        void onActionInvite( String str);
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    @Override 
    public String getTitle() {
        return "邀请成员";
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.fragment_invite_member;
    }

    private final List<String> getUserList() {
        List<String> userList = new ArrayList<>();
        if (bizModel == null) {
            return userList;
        }
        List<String> onlines = new ArrayList<>(bizModel.getMeetingMembers().keySet());
        if (onlines != null) {
            userList.addAll(onlines);
        }
        List<String> callings = new ArrayList<>(bizModel.getCallingMembers().keySet());
        if (callings != null) {
            userList.addAll(callings);
        }
        if (bizModel.getCurrentUser() != null) {
            userList.add(bizModel.getCurrentUser().getUserId());
        }
        return userList;
    }

    @Override 
    public void onInitContentView( View rootView) {
        this.mUserInput = (EditText) rootView.findViewById(R.id.user_id_input);
        this.mUserInputStat = (TextView) rootView.findViewById(R.id.user_input_stat);
        this.mUserInputClear = rootView.findViewById(R.id.user_input_clear);
        mUserInputClear.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (mUserInput == null) {
                    return;
                }
                mUserInput.setText("");
            }
        });
        this.mConfirmBtn = rootView.findViewById(R.id.confirm_btn);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                String input = mUserInput.getText() != null ? mUserInput.getText().toString().trim() : "";
                if (!TextUtils.isEmpty(input)) {
                    List<String> userList = getUserList();
                    if (userList.contains(input)) {
                        Toast.makeText(requireContext(), "用户已经在房间内", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (bizModel != null) {
                        bizModel.invite(input);
                    }
                    if (callback == null) {
                        return;
                    }
                    callback.onActionInvite(input);
                }
            }
        });
        mUserInput.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(AUICallConfig.MAX_ID_LEN)});
        mUserInput.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void beforeTextChanged( CharSequence s, int start, int count, int after) {
            }

            @Override 
            public void onTextChanged( CharSequence s, int start, int before, int count) {
            }

            @Override 
            public void afterTextChanged( Editable s) {
                if (mUserInputStat == null) {
                    return;
                }
                mUserInputStat.setText((s == null ? 0 : s.length()) + "/15");
            }
        });
    }
}
