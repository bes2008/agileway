package com.jn.agileway.ssh.client.transport.hostkey;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum StrictHostKeyChecking implements CommonEnum {
    NO(0, "no", "NO"),  // 不进行严格检查，当known_hosts中不存在时，会写入 known_hosts；当known_hosts中存在时，进行验证。
    ASK(1, "ask", "ASK"),// 询问使用者，这个需要人工参与
    YES(2, "yes", "YES");// 进行严格检查。当known_hosts中不存在时，直接失败；当known_hosts中存在时，进行验证。

    private EnumDelegate delegate;

    StrictHostKeyChecking(int code, String name, String displayText) {
        this.delegate = new EnumDelegate(code, name, displayText);
    }

    @Override
    public int getCode() {
        return delegate.getCode();
    }

    @Override
    public String getDisplayText() {
        return delegate.getDisplayText();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}
