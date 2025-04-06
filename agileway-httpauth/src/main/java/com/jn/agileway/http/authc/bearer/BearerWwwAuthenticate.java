package com.jn.agileway.http.authc.bearer;

import com.jn.agileway.http.authc.AuthHeaders;
import com.jn.agileway.http.authc.AuthScheme;
import com.jn.agileway.http.authc.WwwAuthenticate;

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
        return AuthHeaders.getFieldAsList(scope, " ");
    }
}
