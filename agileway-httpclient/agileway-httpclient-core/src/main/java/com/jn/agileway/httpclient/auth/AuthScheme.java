package com.jn.agileway.httpclient.auth;

import com.jn.langx.util.enums.base.CommonEnum;

public enum AuthScheme implements CommonEnum {
    BASIC("Basic"),
    DIGEST("Digest"),
    NTLM("NTLM"),
    SPNEGO("SPNEGO"),
    KERBEROS("Kerberos"),
    NEGOTIATE("Negotiate"),
    BEARER("Bearer");

    private String scheme;

    AuthScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }


    @Override
    public int getCode() {
        return ordinal();
    }

    @Override
    public String getDisplayText() {
        return getName();
    }

    @Override
    public String getName() {
        return getScheme();
    }
}
