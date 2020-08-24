package com.lnvip.android.permissions;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.lnvip.android.permissions.api.R;
import com.lnvip.android.permissions.aspect.IProceedingJoinPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Permissions {
    private static Application sApplication;

    static final String KEY_CALLBACKID = "KEY_CALLBACKID";
    static final String KEY_PERMISSIONS = "KEY_PERMISSIONS";

    private static final Map<String, String[]> permissionNamesMap = new HashMap<>();

    static final Map<String, Callback> callbackMap = new HashMap<>();

    private static PermissionResultInterceptor sPermissionResultInterceptor;
    private static PermissionDeniedCallback sPermissionDeniedCallback;

    public static void init(Application app) {
        sApplication = app;
        String[] permissions = app.getResources().getStringArray(R.array.com_lnvip_permissions);
        String[] permissionNames = app.getResources().getStringArray(R.array.com_lnvip_permission_names);
        String[] permissionDescriptions = app.getResources().getStringArray(R.array.com_lnvip_permission_descriptions);
        for (int i = 0; i < permissions.length; i++) {
            if (i < permissionNames.length && i < permissionDescriptions.length) {
                permissionNamesMap.put(permissions[i], new String[]{permissionNames[i], permissionDescriptions[i]});
            }
        }
    }

    public static Application getApplication() {
        return sApplication;
    }

    public static void setPermissionResultInterceptor(PermissionResultInterceptor interceptor) {
        sPermissionResultInterceptor = interceptor;
    }

    public static PermissionResultInterceptor getPermissionResultInterceptor() {
        return sPermissionResultInterceptor;
    }

    public static PermissionDeniedCallback getPermissionDeniedCallback() {
        return sPermissionDeniedCallback;
    }

    public static void setPermissionDeniedCallback(PermissionDeniedCallback sPermissionDeniedCallback) {
        Permissions.sPermissionDeniedCallback = sPermissionDeniedCallback;
    }

    public static String getPermissionName(String permission) {
        String[] v = permissionNamesMap.get(permission);
        return null == v ? permission : v[0];
    }

    public static String getPermissionDescription(String permission) {
        String[] v = permissionNamesMap.get(permission);
        return null == v ? permission : v[1];
    }

    public static void request(Callback callback, String... permissions) {
        request(sApplication, callback, permissions);
    }

    public static void request(Context context, Callback callback, String... permissions) {
        boolean granted = true;
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(context, permission)) {
                granted = false;
                break;
            }
        }
        if (granted) {
            callback.onResult(Arrays.asList(permissions), new ArrayList<String>());
            return;
        }
        String callbackId = genId(callback);
        callbackMap.put(callbackId, callback);
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(KEY_CALLBACKID, callbackId);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private static String genId(Callback callback) {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + "-" + callback.hashCode();
    }

    public static String getPermissionNames(List<String> permissions, String concat) {
        if (null == permissions || 0 == permissions.size()) {
            return "";
        }
        StringBuilder result = new StringBuilder(getPermissionName(permissions.get(0)));
        for (int i = 1; i < permissions.size(); i++) {
            result.append(concat).append(getPermissionName(permissions.get(i)));
        }
        return result.toString();
    }

    public interface Callback {

        void onResult(List<String> granted, List<String> rejected);
    }

    public interface PermissionResultInterceptor {

        void intercept(IProceedingJoinPoint joinPoint, List<String> granted, List<String> rejected);
    }

    public interface PermissionDeniedCallback {

        void onPermissionsDenied(IProceedingJoinPoint joinPoint, List<String> granted, List<String> rejected);
    }

}