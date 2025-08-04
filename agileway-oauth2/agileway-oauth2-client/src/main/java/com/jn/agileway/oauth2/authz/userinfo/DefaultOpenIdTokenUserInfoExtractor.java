package com.jn.agileway.oauth2.authz.userinfo;

/**
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken">OpenID Connect Core 1.0 - 2. ID Token</a>
 */
public class DefaultOpenIdTokenUserInfoExtractor implements OpenIdTokenUserinfoExtractor {
    @Override
    public OpenIdTokenUserinfo extract(OpenIdToken idToken) {
        if (idToken != null) {
            return new OpenIdTokenUserinfo(idToken);
        }
        return null;
    }
}
