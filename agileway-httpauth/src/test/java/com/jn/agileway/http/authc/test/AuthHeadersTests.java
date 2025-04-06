package com.jn.agileway.http.authc.test;

import com.jn.agileway.http.authc.AuthHeaders;
import com.jn.agileway.http.authc.WwwAuthenticate;
import org.junit.Test;

public class AuthHeadersTests {
    @Test
    public void testHeaderParse() {
        String headerValue = "Digest realm=\"http-auth@example.org\",  qop=\"auth, auth-int\", algorithm=SHA-256, nonce=\"7ypf/xlj9XXwfDPEoM4URrv/xwf94BcCAzFZH4GiTo0v\", opaque=\"FQhe/qaU925kfnzjCev0ciny7QMkPqMAFRtzCUYo5tdS\"";
        WwwAuthenticate authenticate = AuthHeaders.parseWwwAuthenticate(headerValue);
        System.out.println(authenticate.toHeaderValue());
    }
}
