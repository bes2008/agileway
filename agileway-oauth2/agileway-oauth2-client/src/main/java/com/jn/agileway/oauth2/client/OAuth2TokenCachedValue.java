package com.jn.agileway.oauth2.client;

public class OAuth2TokenCachedValue {
    private OAuth2Token oauth2Token;
    private long createdTime;


    public OAuth2TokenCachedValue(OAuth2Token oauth2Token) {
        this.oauth2Token = oauth2Token;
        this.createdTime = System.currentTimeMillis();
    }

    public OAuth2Token getOauth2Token() {
        return oauth2Token;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setOauth2Token(OAuth2Token oauth2Token) {
        this.oauth2Token = oauth2Token;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
