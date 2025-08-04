package com.jn.agileway.oauth2.authz.userinfo;

import com.jn.agileway.oauth2.authz.IntrospectResult;

public class IntrospectedUserinfo implements Userinfo {
    private final IntrospectResult introspectResult;

    public IntrospectedUserinfo(IntrospectResult introspectResult) {
        this.introspectResult = introspectResult;
    }

    public String getUsername() {
        return introspectResult.getUsername();
    }

    public String getEmail() {
        return null;
    }

    public String getPhoneNumber() {
        return null;
    }
}
