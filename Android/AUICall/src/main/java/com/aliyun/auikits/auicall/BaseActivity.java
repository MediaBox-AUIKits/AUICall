package com.aliyun.auikits.auicall;

import android.text.TextUtils;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aliyun.auikits.auicall.model.callback.TokenAccessor;
import com.aliyun.auikits.auicall.fragment.BaseFragment;
import com.aliyun.auikits.auicall.network.HttpRequest;
import com.aliyun.auikits.auicall.util.AUICallConfig;
import com.aliyun.auikits.room.util.AliyunLog;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class BaseActivity extends BaseDebugActivity implements TokenAccessor {

    private static String DEVICE_ID = null;
    private static final String TAG = "base_activity";
    private boolean mAppLogin;

    private String mAppToken;

    private String mImToken;

    private Long mRTCTimestamp;

    private String mRTCToken;

    private String mRoomId;

    private final Object mObj = new Object();

    public final boolean getMAppLogin() {
        return this.mAppLogin;
    }

    public final void setMAppLogin(boolean z) {
        this.mAppLogin = z;
    }

    

    public final String getDeviceId() {
        if (!TextUtils.isEmpty(DEVICE_ID)) {
            return DEVICE_ID;
        }
        DEVICE_ID = new String();
        Random random = new Random(System.currentTimeMillis());
        int i = 0;
        while (i < 10) {
            char c = (char) (random.nextInt(26) + 'a');
            DEVICE_ID = DEVICE_ID +  Character.valueOf(c);
            i++;
        }
        return DEVICE_ID;
    }

    public final void appLogin( String userId) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("username", userId);
            jsonObj.put("password", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqStr = jsonObj.toString();
        Request.Builder url = new Request.Builder().url(AUICallConfig.LOGIN_URL);
        RequestBody.Companion companion = RequestBody.Companion;
        MediaType parse = MediaType.Companion.parse("application/json");
        Request request = url.post(companion.create(parse, reqStr)).build();
        HttpRequest companion2 = HttpRequest.getInstance();
        companion2.getClient().newCall(request).enqueue(new Callback() { 
            @Override // okhttp3.Callback
            public void onFailure(Call call, final IOException e) {
                AliyunLog.e("base_activity", "appLogin onFailure " + e.getMessage());
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BaseActivity.this.addDebugInfo("appLogin onFailure " + e.getMessage());
                        Toast.makeText(getApplicationContext(), R.string.app_login_failure_tips, Toast.LENGTH_SHORT).show();
                    }
                });
                synchronized (mObj) {
                    mObj.notify();
                }
            }

            @Override // okhttp3.Callback
            public void onResponse( Call call,  Response response) {
                boolean failure = false;
                if (response.code() != 200) {
                    failure = true;
                    BaseActivity.this.addDebugInfo("appLogin onResponse code " + response.code());
                } else {
                    ResponseBody body = response.body();
                    String respData = null;
                    try {
                        respData = body == null ? null : body.string();
                        JSONObject jsonObj = new JSONObject(respData);
                        BaseActivity.this.mAppToken = jsonObj.getString("token");
                    } catch (IOException e) {
                        e.printStackTrace();
                        failure = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        failure = true;
                    }
                    AliyunLog.e("base_activity", "appLogin onResponse token " + mAppToken);
                    addDebugInfo("appLogin onResponse token " + mAppToken);
                }
                if(failure){
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.app_login_failure_tips, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                synchronized (mObj) {
                    mObj.notify();
                }
            }
        });
        synchronized (this.mObj) {
            try {
                this.mObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public final void appLogout() {
        this.mAppToken = null;
        this.mRTCToken = null;
        this.mRTCTimestamp = null;
        this.mImToken = null;
    }

    @Override 
    public String getRtcToken( String userId,  String roomId) {
        String str = this.mRoomId;
        if (str == null || !TextUtils.equals(roomId, str)) {
            this.mRoomId = roomId;
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("user_id", userId);
                jsonObj.put("room_id", roomId);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            Request.Builder header = new Request.Builder().url(AUICallConfig.REQ_RTC_TOKEN_URL).header("Authorization", "Bearer " + this.mAppToken);
            RequestBody.Companion companion = RequestBody.Companion;
            MediaType parse = MediaType.Companion.parse("application/json");
            Request request = header.post(companion.create(parse, jsonObj.toString())).build();
            HttpRequest companion2 = HttpRequest.getInstance();
            companion2.getClient().newCall(request).enqueue(new Callback() {
                @Override // okhttp3.Callback
                public void onFailure( Call call,  IOException e) {
                    AliyunLog.e("base_activity", "getRtcToken onFailure " + e.getMessage());
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.request_token_failure_tips, Toast.LENGTH_SHORT).show();
                        }
                    });
                    synchronized (mObj) {
                        mObj.notify();
                    }
                }

                @Override // okhttp3.Callback
                public void onResponse( Call call,  Response response) {
                    boolean failure = false;
                    if (response.code() == 200) {
                        ResponseBody body = response.body();
                        String respData = null;
                        try {
                            respData = body == null ? null : body.string();
                            JSONObject jsonObj = new JSONObject(respData);
                            BaseActivity.this.mRTCToken = jsonObj.getString("auth_token");
                            BaseActivity.this.mRTCTimestamp = Long.valueOf(jsonObj.getLong("timestamp"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            failure = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            failure = true;
                        }
                    }else{
                        failure = true;
                    }
                    if(failure){
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.request_token_failure_tips, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    synchronized (mObj) {
                        mObj.notify();
                    }
                }
            });
            synchronized (this.mObj) {
                try {
                    this.mObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.mRTCToken;
    }

    @Override 
    public Long getRtcTimestamp() {
        return this.mRTCTimestamp;
    }

    @Override 
    public String getIMToken( String userId) {
        if (TextUtils.isEmpty(this.mImToken)) {
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("user_id", userId);
                jsonObj.put("device_id", getDeviceId());
                jsonObj.put("device_type", "android");
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            Request.Builder header = new Request.Builder().url(AUICallConfig.REQ_IM_TOKEN_URL).header("Authorization", "Bearer " + this.mAppToken);
            RequestBody.Companion companion = RequestBody.Companion;
            MediaType parse = MediaType.Companion.parse("application/json");
            Request request = header.post(companion.create(parse, jsonObj.toString())).build();
            HttpRequest companion2 = HttpRequest.getInstance();
            companion2.getClient().newCall(request).enqueue(new Callback() { 
                @Override // okhttp3.Callback
                public void onFailure( Call call,  IOException e) {
                    AliyunLog.e("base_activity", "getImToken onFailure " + e.getMessage());
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.request_token_failure_tips, Toast.LENGTH_SHORT).show();
                        }
                    });
                    synchronized (mObj) {
                        mObj.notify();
                    }
                }

                @Override // okhttp3.Callback
                public void onResponse( Call call,  Response response) {
                    boolean failure = false;
                    if (response.code() == 200) {
                        ResponseBody body = response.body();
                        String respData = null;
                        try {
                            respData = body == null ? null : body.string();
                            JSONObject jsonObj = new JSONObject(respData);
                            String accessToken = jsonObj.getString("access_token");
                            String refreshToken = jsonObj.getString("refresh_token");
                            BaseActivity.this.mImToken = new StringBuilder().append((Object) accessToken).append('_').append((Object) refreshToken).toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                            failure = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            failure = true;
                        }
                        AliyunLog.e("base_activity", "getImToken onResponse code ok");

                    } else {
                        AliyunLog.e("base_activity", "get im token onResponse code " + response.code());
                        BaseActivity.this.addDebugInfo("get im token onResponse code " + response.code());
                        failure = true;
                    }
                    if(failure){
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.request_token_failure_tips, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    synchronized (mObj) {
                        mObj.notify();
                    }
                }
            });
            synchronized (this.mObj) {
                try {
                    this.mObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.mImToken;
    }

    private final Fragment getHeadFragment() {
        if (getSupportFragmentManager().getFragments().size() == 0) {
            return null;
        }
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        Fragment fragment = (Fragment) fragments.get(fragments.size()-1);
        if (fragment.isAdded()) {
            return fragment;
        }
        return null;
    }

    public final void popBackFragment( Fragment fragment) {
        if (fragment == null || !fragment.isAdded()) {
            return;
        }
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.remove(fragment);
        trans.commit();
        getSupportFragmentManager().executePendingTransactions();
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).inBack();
        }
        Fragment headFragment = getHeadFragment();
        if (headFragment != null && (headFragment instanceof BaseFragment)) {
            ((BaseFragment) headFragment).inFront();
        }
    }



    public final void popBackFragment() {
        Fragment fragment = getHeadFragment();
        if (fragment == null) {
            return;
        }
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.remove(fragment);
        trans.commit();
        getSupportFragmentManager().executePendingTransactions();
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).inBack();
        }
        Fragment headFragment = getHeadFragment();
        if (headFragment != null && (headFragment instanceof BaseFragment)) {
            ((BaseFragment) headFragment).inFront();
        }
    }

    public final void pushBackFragment( Fragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }
        Fragment headFragment = getHeadFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.fragment_container, fragment);
        trans.commit();
        getSupportFragmentManager().executePendingTransactions();
        if (headFragment != null && (headFragment instanceof BaseFragment)) {
            ((BaseFragment) headFragment).inBack();
        }
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).inFront();
        }
    }

    public final void showFragment( Fragment fragment) {
        if (fragment == null) {
            return;
        }
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.commit();
        getSupportFragmentManager().executePendingTransactions();
        if (fragments.size() > 0) {
            for (Fragment item : fragments) {
                if (item != null && (item instanceof BaseFragment)) {
                    ((BaseFragment) item).inBack();
                }
            }
        }
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).inFront();
        }
    }
}
