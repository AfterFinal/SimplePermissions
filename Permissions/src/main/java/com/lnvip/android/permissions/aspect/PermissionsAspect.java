package com.lnvip.android.permissions.aspect;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

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
            Permissions.request(new Permissions.Callback() {
                @Override
                public void onResult(List<String> granted, List<String> rejected) {
                    if (null != Permissions.getPermissionResultInterceptor()) {
                        Permissions.getPermissionResultInterceptor().intercept(new ProceedingJoinPointIml(joinPoint), granted, rejected);
                    } else {
                        if (null == rejected || 0 == rejected.size()) {
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
                            } else if (requestPermissions.showTipsWhenRejected()) {
                                new AlertDialog.Builder(Permissions.getApplication())
                                        .setCancelable(false)
                                        .setTitle("缺少必要权限：")
                                        .setMessage(Permissions.getPermissionNames(rejected, "\n"))
                                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", Permissions.getApplication().getPackageName(), null);
                                                intent.setData(uri);
                                                Permissions.getApplication().startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }).show();
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
