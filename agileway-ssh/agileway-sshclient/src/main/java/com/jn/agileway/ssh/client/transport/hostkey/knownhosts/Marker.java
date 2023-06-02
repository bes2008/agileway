package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;


import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum Marker implements CommonEnum {
    CA_CERT(0, "@cert-authority"),
    REVOKED(1, "@revoked");

    private EnumDelegate delegate;

    Marker(int code, String name) {
        this.delegate = new EnumDelegate(code, name, name);
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
