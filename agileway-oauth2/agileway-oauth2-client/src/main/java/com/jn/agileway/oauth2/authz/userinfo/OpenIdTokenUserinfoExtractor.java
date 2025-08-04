package com.jn.agileway.oauth2.authz.userinfo;

public interface OpenIdTokenUserinfoExtractor extends UserinfoExtractor<OpenIdToken, OpenIdTokenUserinfo> {
    OpenIdTokenUserinfo extract(OpenIdToken idToken);
}
