package com.aliyun.auikits.auicall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aliyun.auikits.auicall.util.PermissionUtils;

@Route(path = "/auicall/CallEntryActivity")
public class CallEntryActivity extends AppCompatActivity {
    @Override 
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_entry);
        findViewById(R.id.single_call_item).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (checkPermission(false)) {
                    startActivity(new Intent(getApplicationContext(), AUICall1V1Activity.class));
                }
            }
        });
        findViewById(R.id.meeting_call_item).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                if (checkPermission(false)) {
                    startActivity(new Intent(getApplicationContext(), AUICallNVNActivity.class));
                }
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                onBackPressed();
            }
        });
        checkPermission(true);
    }

    private final boolean checkPermission(boolean req) {
        if (!PermissionUtils.checkPermissionsGroup(getApplicationContext(), PermissionUtils.getPermissions())) {
            if (!req) {
                Toast.makeText(this, "请开通权限后重试", Toast.LENGTH_SHORT).show();
            } else {
                PermissionUtils.requestPermissions(this, PermissionUtils.getPermissions(), 1000);
            }
            return false;
        }
        return true;
    }

    @Override 
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        if (requestCode == 1000) {
            if (!PermissionUtils.checkPermissionsGroup(getApplicationContext(), PermissionUtils.getPermissions())) {
                Toast.makeText(this, "权限申请失败，请重试", Toast.LENGTH_SHORT).show();
                return;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
