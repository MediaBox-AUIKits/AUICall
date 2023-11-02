package com.aliyun.auikits.auicall.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.auikits.auicall.bean.AUICall1V1Mode;
import com.aliyun.auikits.auicall.util.AUICallConfig;
import com.aliyun.auikits.auicall.R;

public final class AUICall1V1MakeCallFragment extends BaseFragment {

    private Callback callback;

    private AUICall1V1Mode mCallMode = AUICall1V1Mode.Audio;

    private RadioGroup mCallModeGroup;

    private View mCopy;

    private View mMakeCallBtn;

    private TextView mMyId;

    private EditText mUserIdInput;

    private View mUserInputClear;

    private TextView mUserInputStat;

    public interface Callback {
        void onActionMakeCall( String str, AUICall1V1Mode callMode);

        void onMakeCallBackPress();
    }


    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback( Callback callback) {
        this.callback = callback;
    }

    @Override 
    public int getLayoutResId() {
        return R.layout.fragment_make_call;
    }

    @Override 
    public String getTitle() {
        return "1v1音视频通话";
    }

    @Override 
    public void onInitContentView( View rootView) {
        this.mUserIdInput = (EditText) rootView.findViewById(R.id.user_id_input);
        this.mUserInputClear = rootView.findViewById(R.id.user_input_clear);
        this.mCopy = rootView.findViewById(R.id.copy);
        this.mMyId = (TextView) rootView.findViewById(R.id.my_id);
        this.mUserInputStat = (TextView) rootView.findViewById(R.id.user_input_stat);
        this.mMakeCallBtn = rootView.findViewById(R.id.make_call_btn);
        this.mCallModeGroup = (RadioGroup) rootView.findViewById(R.id.call_mode);
        mUserIdInput.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(AUICallConfig.MAX_ID_LEN)});
        mUserIdInput.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void beforeTextChanged( CharSequence s, int start, int count, int after) {
            }

            @Override 
            public void onTextChanged( CharSequence s, int start, int before, int count) {
            }

            @Override 
            public void afterTextChanged( Editable s) {
                if (mUserInputStat != null) {
                    mUserInputStat.setText((s == null ? 0 : s.length()) + "/15");
                }
                if (s != null && s.length() > 0) {
                    mUserInputClear.setVisibility(View.VISIBLE);
                }else{
                    mUserInputClear.setVisibility(View.GONE);
                }
            }
        });
        mUserInputClear.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view2) {
                if (mUserIdInput == null) {
                    return;
                }
                mUserIdInput.setText("");
            }
        });
        mMyId.setText("我的ID:" + getUserIdFromSPF());
        mCopy.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view3) {
                ClipboardManager clipBoard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("id", getUserIdFromSPF());
                clipBoard.setPrimaryClip(clipData);
                Toast.makeText(requireContext(), "已复制", Toast.LENGTH_SHORT).show();
            }
        });
        mMakeCallBtn.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view4) {
                String obj = mUserIdInput.getText() != null ? mUserIdInput.getText().toString().trim() : "";
                if (obj.length() == 0) {
                    Toast.makeText(requireContext(), "用户ID为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (callback == null) {
                    return;
                }
                callback.onActionMakeCall(obj, mCallMode);
            }
        });
        mCallModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override 
            public final void onCheckedChanged(RadioGroup radioGroup2, int checkedId) {
                if (checkedId == R.id.video_call) {
                    mCallMode = AUICall1V1Mode.Video;
                    return;
                }
                mCallMode = AUICall1V1Mode.Audio;
            }
        });
    }

    @Override 
    public void onBackPressed() {
        super.onBackPressed();
        if (callback == null) {
            return;
        }
        callback.onMakeCallBackPress();
    }
}
