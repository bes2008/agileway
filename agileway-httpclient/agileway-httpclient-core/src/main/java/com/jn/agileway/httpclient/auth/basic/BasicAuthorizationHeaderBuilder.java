package com.jn.agileway.httpclient.auth.basic;

import com.jn.agileway.httpclient.auth.AuthScheme;
import com.jn.agileway.httpclient.auth.AuthorizationHeaderBuilder;
import com.jn.agileway.httpclient.auth.UserPasswordCredentials;
import com.jn.langx.codec.StringifyFormat;
import com.jn.langx.codec.Stringifys;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.http.HttpHeaderValueBuilders;

/**
 * <pre>
 * 参考链接：https://datatracker.ietf.org/doc/html/rfc7617#section-2
 * </pre>
 */
public class BasicAuthorizationHeaderBuilder extends AuthorizationHeaderBuilder<BasicAuthorizationHeaderBuilder, BasicWwwAuthenticate, UserPasswordCredentials> {
    public BasicAuthorizationHeaderBuilder() {
    }

    @Override
    public String build() {
        UserPasswordCredentials credentials = this.credentials;
        String userPass = credentials.getUsername() + ":" + credentials.getPassword();
        String encoded = Stringifys.stringify(userPass.getBytes(Charsets.UTF_8), StringifyFormat.BASE64);
        return HttpHeaderValueBuilders.buildHeaderValueWithType(AuthScheme.BASIC.getScheme(), " ", encoded);
    }
}
