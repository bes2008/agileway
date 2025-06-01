package com.jn.agileway.httpclient.test.auth;

import com.jn.agileway.httpclient.auth.WwwAuthenticateUtils;
import com.jn.agileway.httpclient.auth.WwwAuthenticate;
import org.junit.Test;

public class AuthHeadersTests {
    @Test
    public void testHeaderParse() {
        String headerValue = "Digest realm=\"http-auth@example.org\",  qop=\"auth, auth-int\", algorithm=SHA-256, nonce=\"7ypf/xlj9XXwfDPEoM4URrv/xwf94BcCAzFZH4GiTo0v\", opaque=\"FQhe/qaU925kfnzjCev0ciny7QMkPqMAFRtzCUYo5tdS\"";
        WwwAuthenticate authenticate = WwwAuthenticateUtils.parseWwwAuthenticate(headerValue);
        System.out.println(authenticate.toHeaderValue());
    }
}
