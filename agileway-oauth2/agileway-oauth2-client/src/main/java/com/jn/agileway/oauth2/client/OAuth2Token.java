package com.jn.agileway.oauth2.client;

import com.jn.agileway.oauth2.client.userinfo.OpenIdToken;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

public class OAuth2Token {
    @NonNull
    private String accessToken;
    @NonNull
    private String tokenType;

    /**
     * 单位是 second
     */
    @NonNull
    private Long expiresIn;

    @NonNull
    private String refreshToken;

    @NonNull
    private String scope;

    /**
     * OpenId 规范中定义的
     */
    @NonNull
    private String idToken;

    private OpenIdToken idTokenObject;

    @NonNull
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(@NonNull String accessToken) {
        this.accessToken = accessToken;
    }

    @NonNull
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(@NonNull String tokenType) {
        this.tokenType = tokenType;
    }

    @Nullable
    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(@Nullable Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Nullable
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(@Nullable String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Nullable
    public String getScope() {
        return scope;
    }

    public void setScope(@Nullable String scope) {
        this.scope = scope;
    }

    @Nullable
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(@Nullable String idToken) {
        this.idToken = idToken;
    }

    public OpenIdToken getIdTokenObject() {
        return idTokenObject;
    }

    public void setIdTokenObject(OpenIdToken idTokenObject) {
        this.idTokenObject = idTokenObject;
    }


    @Override
    public String toString() {
        return JSONs.toJson(this);
    }
}
