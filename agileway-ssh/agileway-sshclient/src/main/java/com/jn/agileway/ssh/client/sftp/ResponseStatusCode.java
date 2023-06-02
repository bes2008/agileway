package com.jn.agileway.ssh.client.sftp;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum ResponseStatusCode implements CommonEnum {

    UNKNOWN(-1, "UNKNOWN", "unknown"),
    OK(0, "OK", "ok"),
    EOF(1, "EOF", "EOF"),
    NO_SUCH_FILE(2, "NO_SUCH_FILE", "no such file"),
    PERMISSION_DENIED(3, "PERMISSION_DENIED", "permission denied"),
    FAILURE(4, "FAILURE", "failure"),
    BAD_MESSAGE(5, "BAD_MESSAGE", "bad message"),
    NO_CONNECTION(6, "NO_CONNECTION", "no connection"),
    CONNECITON_LOST(7, "CONNECITON_LOST", "connection lost"),
    OP_UNSUPPORTED(8, "OP_UNSUPPORTED", "operation is unsupported");

    private EnumDelegate delegate;

    ResponseStatusCode(int code, String name, String displayText) {
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

