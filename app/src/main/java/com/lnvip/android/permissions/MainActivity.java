package com.lnvip.android.permissions;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lnvip.android.permissions.aspect.IProceedingJoinPoint;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
    }

    public void requestUsePermissions(View view) {
        doRequestUsePermissions();
    }

    @RequestPermissions(value = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SYSTEM_ALERT_WINDOW
    }, callbackMethod = "onPermissionRequestResult", callback = Callback1.class)
    private void doRequestUsePermissions() {
        Toast.makeText(MainActivity.this, "doRequestUsePermissions", Toast.LENGTH_SHORT).show();
    }

    public void onPermissionRequestResult(IProceedingJoinPoint joinPoint, List<String> granted, List<String> rejected) {
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static class Callback1 implements PermissionRequestCallback {

        @Override
        public void onResult(IProceedingJoinPoint joinPoint, List<String> granted, List<String> rejected) {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}