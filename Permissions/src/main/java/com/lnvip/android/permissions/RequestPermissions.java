package com.lnvip.android.permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestPermissions {

    String[] value();

    boolean must() default true;

    TipMode tipMode() default TipMode.Dialog;
}