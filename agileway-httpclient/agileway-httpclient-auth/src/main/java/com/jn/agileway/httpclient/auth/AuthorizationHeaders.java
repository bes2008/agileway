package com.jn.agileway.httpclient.auth;

import com.jn.agileway.httpclient.auth.basic.BasicAuthorizationHeaderBuilder;
import com.jn.agileway.httpclient.auth.basic.UserPasswordCredentials;
import com.jn.agileway.httpclient.auth.bearer.BearerAuthorizationHeaderBuilder;
import com.jn.agileway.httpclient.auth.bearer.BearerCredentials;

public class AuthorizationHeaders {
    public static final String AUTHORIZATION = "Authorization";

    public static String newBasicAuthToken(String username, String pswd) {
        return new BasicAuthorizationHeaderBuilder().withCredentials(new UserPasswordCredentials(username, pswd)).build();
    }


    public static String newBearerAuthToken(String token) {
        return new BearerAuthorizationHeaderBuilder().withCredentials(new BearerCredentials(token)).build();
    }

    public static String getBearerToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length());
        }
        return null;
    }
}
