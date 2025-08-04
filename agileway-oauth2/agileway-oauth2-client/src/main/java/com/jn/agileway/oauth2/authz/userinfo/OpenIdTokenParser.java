package com.jn.agileway.oauth2.authz.userinfo;

public interface OpenIdTokenParser {
    OpenIdToken parse(String idTokenString);
}
