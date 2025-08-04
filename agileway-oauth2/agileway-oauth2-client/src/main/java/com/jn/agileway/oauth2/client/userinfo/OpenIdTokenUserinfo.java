package com.jn.agileway.oauth2.client.userinfo;


import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

/**
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken">OpenID Connect Core 1.0 - 2. ID Token</a>
 */
public class OpenIdTokenUserinfo implements Userinfo {
    private final OpenIdToken openIdToken;

    public OpenIdTokenUserinfo(OpenIdToken openIdToken) {
        this.openIdToken = openIdToken;
    }

    @NonNull
    @Override
    public String getUsername() {
        return openIdToken.getFieldAsString("sub");
    }

    @Nullable
    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }
}
