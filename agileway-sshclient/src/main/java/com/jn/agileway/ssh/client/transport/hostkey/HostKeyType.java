package com.jn.agileway.ssh.client.transport.hostkey;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum HostKeyType implements CommonEnum {
    SSH_DSS(1, "ssh-dss", "ssh-dss"),
    SSH_RSA(2, "ssh-rsa", "ssh-rsa"),
    ECDSA_SHA2_NISTP256(3, "ecdsa-sha2-nistp256", "ecdsa-sha2-nistp256"),
    ECDSA_SHA2_NISTP384(4, "ecdsa-sha2-nistp384", "ecdsa-sha2-nistp384"),
    ECDSA_SHA2_NISTP521(5, "ecdsa-sha2-nistp521", "ecdsa-sha2-nistp521"),
    ;
    private EnumDelegate delegate;

    HostKeyType(int code, String name, String displayText) {
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
