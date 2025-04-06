package com.jn.agileway.http.authc.bearer;

import com.jn.agileway.http.authc.AuthHeaders;
import com.jn.agileway.http.authc.AuthScheme;
import com.jn.agileway.http.authc.AuthorizationHeaderBuilder;
import com.jn.agileway.http.authc.WwwAuthenticate;

public class BearerAuthorizationHeaderBuilder extends AuthorizationHeaderBuilder<BearerAuthorizationHeaderBuilder, WwwAuthenticate, BearerCredentials> {
    @Override
    public String build() {
        return AuthHeaders.buildAuthHeaderString(AuthScheme.BEARER.getScheme(), this.credentials.getToken());
    }
}
