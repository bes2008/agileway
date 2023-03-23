package com.jn.agileway.syslog.protocol;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum Severity implements CommonEnum {
    Emergency(0, "Emergency", "Emergency"),
    Alert(1, "Alert", "Alert"),
    Critical(2, "Critical", "Critical"),
    Error(3, "Error", "Error"),
    Warning(4, "Warning", "Warning"),
    Notice(5, "Notice", "Notice"),
    Informational(6, "Informational", "Informational"),
    Debug(0, "Debug", "Debug");

    private EnumDelegate delegate;

    Severity(int code, String name, String display) {
        this.delegate = new EnumDelegate(code, name, display);
    }

    @Override
    public int getCode() {
        return this.delegate.getCode();
    }

    @Override
    public String getDisplayText() {
        return this.delegate.getDisplayText();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }
}
