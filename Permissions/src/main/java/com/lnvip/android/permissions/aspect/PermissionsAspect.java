package com.lnvip.android.permissions.aspect;

import android.widget.Toast;

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
public class PermissionsAspect {

    @Pointcut("execution(@com.lnvip.android.permissions.RequestPermissions * **(..))")
    public void withinAnnotatedMethods() {
    }

    @Around("withinAnnotatedMethods()")
    public void aroundRequestPermissionsPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            RequestPermissions permissions = method.getAnnotation(RequestPermissions.class);
            if (null == permissions) {
                joinPoint.proceed();
                return;
            }
            String[] permissionsArr = permissions.value();
            if (null == permissionsArr || 0 == permissionsArr.length) {
                joinPoint.proceed();
                return;
            }
            Permissions.request(new Permissions.Callback() {
                @Override
                public void onResult(List<String> granted, List<String> rejected) {
                    if (null != Permissions.getPermissionResultInterceptor()) {
                        Permissions.getPermissionResultInterceptor().intercept(new ProceedingJoinPointIml(joinPoint), granted, rejected);
                    } else {
                        if (0 == rejected.size()) {
                            try {
                                joinPoint.proceed();
                            } catch (Throwable throwable) {
                                RuntimeException exception = new RuntimeException(throwable);
                                exception.setStackTrace(throwable.getStackTrace());
                                throw exception;
                            }
                        } else {
                            if (null != Permissions.getPermissionDeniedCallback()) {
                                Permissions.getPermissionDeniedCallback().onPermissionsDenied(new ProceedingJoinPointIml(joinPoint), granted, rejected);
                            } else {
                                Toast.makeText(Permissions.getApplication(), "以下权限申请失败:\n" + Permissions.getPermissionNames(rejected, "\n"), Toast.LENGTH_SHORT).show();
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
