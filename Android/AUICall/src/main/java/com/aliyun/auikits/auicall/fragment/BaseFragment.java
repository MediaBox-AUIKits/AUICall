package com.aliyun.auikits.auicall.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.aliyun.auikits.auicall.BaseActivity;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.R;

import java.util.List;
public abstract class BaseFragment extends Fragment {

    private static String SPF_FILE_NAME = "meeting_call_info";

    private static String SPF_KEY = "my_id";

    protected AUICallNVNModel bizModel;

    private View mBackBtn;

    protected View mContentView;

    private TextView mTitle;

    public abstract int getLayoutResId();

    public abstract String getTitle();

    public abstract void onInitContentView( View view);

    public final AUICallNVNModel getBizModel() {
        return this.bizModel;
    }

    public final void setBizModel( AUICallNVNModel meetingCallModel) {
        this.bizModel = meetingCallModel;
    }

    protected final void setMContentView( View view) {
        this.mContentView = view;
    }


    protected final View getMBackBtn() {
        return this.mBackBtn;
    }

    protected final void setMBackBtn( View view) {
        this.mBackBtn = view;
    }

    @Override 

    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        if (this.mContentView == null) {
            this.mContentView = inflater.inflate(getLayoutResId(), container, false);
            View bgMask = mContentView.findViewById(R.id.bg_mask);
            if (bgMask != null) {
                bgMask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
            this.mBackBtn = mContentView.findViewById(R.id.back_btn);
            if (mBackBtn != null) {
                mBackBtn.setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public final void onClick(View view2) {
                        onBackPressed();
                    }
                });
            }
            this.mTitle = (TextView) mContentView.findViewById(R.id.title);
            if (mTitle != null) {
                mTitle.setText(getTitle());
            }
            onInitContentView(mContentView);
        }
        return this.mContentView;
    }

    public void onBackPressed() {
        if (this.isAdded()) {
            FragmentActivity act = requireActivity();
            if (act != null && act.getSupportFragmentManager().getFragments().size() > 0) {
                List<Fragment> fragments = act.getSupportFragmentManager().getFragments();
                Fragment headFragment = fragments != null && fragments.size() > 0 ? fragments.get(fragments.size()-1) : null;
                if (headFragment != null && headFragment == this) {
                    ((BaseActivity) act).popBackFragment(this);
                }
            }
        }
    }

    public final void saveUserIdToSPF( String id) {
        if (id == null) {
            return;
        }
        SharedPreferences spf = requireContext().getApplicationContext().getSharedPreferences(SPF_FILE_NAME, 0);
        spf.edit().putString(SPF_KEY, id).apply();
    }

    public final String getUserIdFromSPF() {
        SharedPreferences spf = requireContext().getApplicationContext().getSharedPreferences(SPF_FILE_NAME, 0);
        return spf.getString(SPF_KEY, "");
    }

    public void inFront() {
    }

    public void inBack() {
    }
}
