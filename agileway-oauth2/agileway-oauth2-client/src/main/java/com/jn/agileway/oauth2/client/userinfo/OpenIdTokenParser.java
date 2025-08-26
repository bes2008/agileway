package com.jn.agileway.oauth2.client.userinfo;

public interface OpenIdTokenParser {
    OpenIdToken parse(String idTokenString);
}
