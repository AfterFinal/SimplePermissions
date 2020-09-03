package com.lnvip.android.permissions.aspect;

import com.lnvip.android.permissions.PermissionRequestCallback;
import com.lnvip.android.permissions.Permissions;
import com.lnvip.android.permissions.RequestPermissions;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@SuppressWarnings("all")
public class PermissionsAspect {

    @Pointcut("execution(@com.lnvip.android.permissions.RequestPermissions * **(..))")
    public void withinAnnotatedMethods() {
    }

    @Around("withinAnnotatedMethods()")
    public void aroundRequestPermissionsPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            final RequestPermissions requestPermissions = method.getAnnotation(RequestPermissions.class);
            if (null == requestPermissions) {
                joinPoint.proceed();
                return;
            }
            String[] permissionsArr = requestPermissions.value();
            if (null == permissionsArr || 0 == permissionsArr.length) {
                joinPoint.proceed();
                return;
            }
            Permissions.request(Permissions.getApplication(), requestPermissions.tipMode(), new Permissions.Callback() {
                @Override
                public void onResult(List<String> granted, List<String> rejected) {
                    if (joinPoint.getThis() instanceof PermissionRequestCallback) {
                        PermissionRequestCallback callback = (PermissionRequestCallback) joinPoint.getThis();
                        callback.onPermissionRequestResult(new ProceedingJoinPointIml(joinPoint), granted, rejected);
                    } else {
                        if (0 == rejected.size()) {
                            try {
                                joinPoint.proceed(joinPoint.getArgs());
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                }
            }, permissionsArr);
        } else {
            joinPoint.proceed();
        }
    }
}
