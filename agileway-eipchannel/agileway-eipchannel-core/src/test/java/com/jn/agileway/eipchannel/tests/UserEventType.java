package com.jn.agileway.eipchannel.tests;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum UserEventType implements CommonEnum {
    ADDED(0, "ADDED"),
    DELETED(1, "DELETED"),
    UPDATED(2, "UPDATED");
    private EnumDelegate delegate;

    UserEventType(int code, String name) {
        this.delegate = new EnumDelegate(code, name, name);
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
