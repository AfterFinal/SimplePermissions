package com.lnvip.android.permissions;

import com.lnvip.android.permissions.aspect.IProceedingJoinPoint;

import java.util.List;

public interface PermissionRequestCallback {
    void onResult(IProceedingJoinPoint joinPoint, List<String> granted, List<String> rejected);
}
