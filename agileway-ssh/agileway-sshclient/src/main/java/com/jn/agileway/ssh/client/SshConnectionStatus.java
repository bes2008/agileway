package com.jn.agileway.ssh.client;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum SshConnectionStatus implements CommonEnum {
    INITIALING(0, "initialing", "初始化"),
    CONNECTED(1, "connected", "已连接"),
    CLOSED(2, "closed", "已关闭");

    private EnumDelegate delegate;

    SshConnectionStatus(int code, String name, String displayText) {
        this.delegate = new EnumDelegate(code, name, displayText);
    }

    @Override
    public int getCode() {
        return this.delegate.getCode();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public String getDisplayText() {
        return this.delegate.getDisplayText();
    }
}
