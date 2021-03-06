package com.lnvip.android.permissions;

import com.lnvip.android.permissions.aspect.IProceedingJoinPoint;

import java.util.List;

public interface PermissionRequestCallback {
    void onPermissionRequestResult(IProceedingJoinPoint joinPoint, boolean must, List<String> granted, List<String> rejected);
}
