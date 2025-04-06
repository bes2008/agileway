package com.jn.agileway.http.authc.bearer;

import com.jn.agileway.http.authc.AuthScheme;
import com.jn.agileway.http.authc.WwwAuthenticate;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;

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
        if (Strings.isNotBlank(scope)) {
            return Pipeline.of(Strings.split(scope, " ")).asList();
        }
        return Lists.immutableList();
    }
}
