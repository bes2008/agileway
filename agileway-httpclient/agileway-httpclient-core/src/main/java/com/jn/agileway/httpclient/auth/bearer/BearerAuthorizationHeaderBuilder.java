package com.jn.agileway.httpclient.auth.bearer;

import com.jn.agileway.httpclient.auth.AuthScheme;
import com.jn.agileway.httpclient.auth.AuthorizationHeaderBuilder;
import com.jn.agileway.httpclient.auth.WwwAuthenticate;
import com.jn.langx.util.net.http.HttpHeaderValueBuilders;

public class BearerAuthorizationHeaderBuilder extends AuthorizationHeaderBuilder<BearerAuthorizationHeaderBuilder, WwwAuthenticate, BearerCredentials> {
    @Override
    public String build() {
        return HttpHeaderValueBuilders.buildHeaderValueWithType(AuthScheme.BEARER.getScheme(), " ", this.credentials.getToken());
    }
}
