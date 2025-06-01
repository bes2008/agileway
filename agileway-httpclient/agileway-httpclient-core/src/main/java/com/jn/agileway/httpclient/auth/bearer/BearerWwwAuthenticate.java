package com.jn.agileway.httpclient.auth.bearer;

import com.jn.agileway.httpclient.auth.WwwAuthenticateUtils;
import com.jn.agileway.httpclient.auth.AuthScheme;
import com.jn.agileway.httpclient.auth.WwwAuthenticate;

import java.util.List;

public class BearerWwwAuthenticate extends WwwAuthenticate {
    public BearerWwwAuthenticate() {
        super();
        this.setAuthScheme(AuthScheme.BEARER.getScheme());
    }

    public String getScope() {
        return getField("scope");
    }

    public List<String> getScopeAsList() {
        String scope = getScope();
        return WwwAuthenticateUtils.getFieldAsList(scope, " ");
    }
}
