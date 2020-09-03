package com.lnvip.android.permissions.aspect;

import android.text.TextUtils;

import com.lnvip.android.permissions.PermissionRequestCallback;
import com.lnvip.android.permissions.Permissions;
import com.lnvip.android.permissions.RequestPermissions;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
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
                    Class<? extends PermissionRequestCallback> callbackClass = requestPermissions.callback();
                    if (!Permissions.Callback.class.equals(callbackClass)) {
                        try {
                            PermissionRequestCallback callback = callbackClass.newInstance();
                            callback.onResult(new ProceedingJoinPointIml(joinPoint), granted, rejected);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String callbackMethod = requestPermissions.callbackMethod();
                        if (!TextUtils.isEmpty(callbackMethod)) {
                            try {
                                Object thisObj = joinPoint.getThis();
                                Method method = thisObj.getClass().getMethod(callbackMethod, IProceedingJoinPoint.class, List.class, List.class);
                                if (!method.isAccessible()) {
                                    method.setAccessible(true);
                                }
                                method.invoke(thisObj, new ProceedingJoinPointIml(joinPoint), granted, rejected);
                            } catch (Exception e) {
                                RuntimeException exception = new RuntimeException(e.getMessage());
                                exception.setStackTrace(e.getStackTrace());
                                throw exception;
                            }
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
                }
            }, permissionsArr);
        } else {
            joinPoint.proceed();
        }
    }
}
