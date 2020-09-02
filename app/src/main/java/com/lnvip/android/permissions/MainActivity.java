package com.lnvip.android.permissions;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
    }

    public void requestUsePermissions(View view) {
        doRequestUsePermissions();
    }

    @RequestPermissions({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION})
    private void doRequestUsePermissions() {
        Toast.makeText(getApplicationContext(), "requestUsePermissions", Toast.LENGTH_SHORT).show();
    }
}