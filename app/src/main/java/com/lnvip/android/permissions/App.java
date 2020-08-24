package com.lnvip.android.permissions;

import android.Manifest;
import android.app.Application;
import android.os.Handler;

import org.simple.eventbus.EventBus;

import java.util.List;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Permissions.init(this);
//        Permissions.setPermissionResultInterceptor(new Permissions.PermissionResultInterceptor() {
//            @Override
//            public void intercept(IProceedingJoinPoint joinPoint, List<String> granted, List<String> rejected) {
//                Permissions.setPermissionResultInterceptor(null);
//                if (0 == rejected.size()) {
//                    EventBus.getDefault().post(new Object(), "on_init_permissions_succ");
//                } else {
//                    Toast.makeText(Permissions.getApplication(), "以下权限申请失败:\n" + Permissions.getPermissionNames(rejected, "\n"), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        doOnCreate();
        Permissions.request(new Permissions.Callback() {
            @Override
            public void onResult(final List<String> granted, final List<String> rejected) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == rejected.size()) {
                            EventBus.getDefault().post("success", "on_init_permissions_result");
                        } else {
                            EventBus.getDefault().post("以下权限申请失败:\n" + Permissions.getPermissionNames(rejected, "\n"), "on_init_permissions_result");
                        }
                    }
                }, 3000);
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @RequestPermissions({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION})
    private void doOnCreate() {
    }
}
