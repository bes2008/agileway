package com.jn.agileway.http.authc.basic;

import com.jn.agileway.http.authc.AuthHeaders;
import com.jn.agileway.http.authc.AuthScheme;
import com.jn.agileway.http.authc.AuthorizationHeaderBuilder;
import com.jn.agileway.http.authc.UserPasswordCredentials;
import com.jn.langx.codec.StringifyFormat;
import com.jn.langx.codec.Stringifys;
import com.jn.langx.util.io.Charsets;

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
        return AuthHeaders.buildAuthHeaderString(AuthScheme.BASIC.getScheme(), encoded);
    }
}
