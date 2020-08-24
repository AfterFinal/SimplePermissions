package com.lnvip.android.permissions.aspect;

public interface ISourceLocation {
    Class getWithinType();

    String getFileName();

    int getLine();

    @Deprecated
    int getColumn();
}
