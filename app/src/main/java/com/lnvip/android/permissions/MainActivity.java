package com.lnvip.android.permissions;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class MainActivity extends AppCompatActivity {

    protected TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
//        doOnCreate();
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscriber(tag = "on_init_permissions_result")
    public void onInitPermissionsResult(String msg) {
        textView.setText(msg);
    }

    @RequestPermissions({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION})
    private void doOnCreate() {
    }

    public void requestUsePermissions(View view) {
        doRequestUsePermissions();
    }

    private void doRequestUsePermissions() {
        Toast.makeText(getApplicationContext(), "requestUsePermissions", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.text_view);
    }
}