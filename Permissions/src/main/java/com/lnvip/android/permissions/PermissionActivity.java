package com.lnvip.android.permissions;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 0x7038;
    private static final int REQUEST_PERMISSIONS_SETTINGS = 0x7347;

    private TipMode tipMode = TipMode.Dialog;

    private String[] permissions;
    private Permissions.Callback callback;

    private final List<String> granted = new ArrayList<>();
    private final List<String> rejected = new ArrayList<>();

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

        permissions = getIntent().getStringArrayExtra(Permissions.KEY_PERMISSIONS);
        tipMode = (TipMode) getIntent().getSerializableExtra(Permissions.KEY_TIP_MODE);
        callback = Permissions.callbackMap.remove(getIntent().getStringExtra(Permissions.KEY_CALLBACKID));

        if (null == permissions || 0 == permissions.length) {
            check();
            dispatchRequestFinish();
            return;
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        check();
        if (0 == rejected.size()) {
            dispatchRequestFinish();
        } else {
            switch (tipMode) {
                case None:
                    dispatchRequestFinish();
                    break;
                case Toast:
                    Toast.makeText(getApplicationContext(), "缺少必要权限:\n" + Permissions.getPermissionNames(rejected, "\n"), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case Dialog:
                    showRequestPermissionSettingDialog();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_PERMISSIONS_SETTINGS == requestCode) {
            check();
            if (rejected.size() > 0) {
                showRequestPermissionSettingDialog();
            } else {
                dispatchRequestFinish();
            }
        }
    }

    private void check() {
        granted.clear();
        rejected.clear();
        if (null != permissions && permissions.length > 0) {
            for (String permission : permissions) {
                if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, permission)) {
                    granted.add(permission);
                } else {
                    rejected.add(permission);
                }
            }
        }
    }

    private void dispatchRequestFinish() {
        if (null != callback) {
            callback.onResult(granted, rejected);
        }
        finish();
    }

    private void showRequestPermissionSettingDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("缺少必要权限：")
                .setMessage(Permissions.getPermissionNames(rejected, "\n"))
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", Permissions.getApplication().getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSIONS_SETTINGS);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "无法打开设置", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNeutralButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                }).show();
    }
}
