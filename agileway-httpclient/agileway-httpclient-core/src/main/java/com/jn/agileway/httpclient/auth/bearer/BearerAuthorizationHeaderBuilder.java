package com.jn.agileway.httpclient.auth.bearer;

import com.jn.agileway.httpclient.auth.AuthHeaders;
import com.jn.agileway.httpclient.auth.AuthScheme;
import com.jn.agileway.httpclient.auth.AuthorizationHeaderBuilder;
import com.jn.agileway.httpclient.auth.WwwAuthenticate;

public class BearerAuthorizationHeaderBuilder extends AuthorizationHeaderBuilder<BearerAuthorizationHeaderBuilder, WwwAuthenticate, BearerCredentials> {
    @Override
    public String build() {
        return AuthHeaders.buildAuthHeaderString(AuthScheme.BEARER.getScheme(), this.credentials.getToken());
    }
}
