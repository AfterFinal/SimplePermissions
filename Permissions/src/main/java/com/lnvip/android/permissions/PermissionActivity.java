package com.lnvip.android.permissions;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 0x7038;
    private String callbackId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = 1;
        lp.height = 1;
        lp.x = -1;
        lp.y = -1;
        window.setAttributes(lp);
        callbackId = getIntent().getStringExtra(Permissions.KEY_CALLBACKID);
        String[] permissions = getIntent().getStringArrayExtra(Permissions.KEY_PERMISSIONS);
        if (null == permissions || 0 == permissions.length) {
            Permissions.Callback callback = Permissions.callbackMap.get(callbackId);
            callback.onResult(new ArrayList<String>(), new ArrayList<String>());
            finish();
            return;
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> granted = new ArrayList<>();
        List<String> rejected = new ArrayList<>();
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, permission)) {
                granted.add(permission);
            } else {
                rejected.add(permission);
            }
        }
        Permissions.Callback callback = Permissions.callbackMap.get(callbackId);
        callback.onResult(granted, rejected);
        finish();
    }

    @Override
    protected void onDestroy() {
        Permissions.callbackMap.remove(callbackId);
        super.onDestroy();
    }
}
