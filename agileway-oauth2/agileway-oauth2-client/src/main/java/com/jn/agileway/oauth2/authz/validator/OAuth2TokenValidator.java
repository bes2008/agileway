package com.jn.agileway.oauth2.authz.validator;

import com.jn.agileway.oauth2.authz.OAuth2Token;
import com.jn.agileway.oauth2.authz.OAuth2TokenCachedValue;
import com.jn.agileway.oauth2.authz.exception.ExpiredAccessTokenException;
import com.jn.agileway.oauth2.authz.exception.InvalidAccessTokenException;

public class OAuth2TokenValidator implements OAuth2AccessTokenValidator<OAuth2TokenCachedValue> {
    @Override
    public void validate(OAuth2TokenCachedValue oauth2TokenCachedValue) throws InvalidAccessTokenException {
        if (oauth2TokenCachedValue == null) {
            return;
        }
        OAuth2Token oauth2Token = oauth2TokenCachedValue.getOauth2Token();
        Long expiresIn = oauth2Token.getExpiresIn();

        if (expiresIn != null) {
            long now = System.currentTimeMillis();
            long createTime = oauth2TokenCachedValue.getCreatedTime();
            long durationInSeconds = (now - createTime) / 1000;
            if (durationInSeconds > expiresIn) {
                throw new ExpiredAccessTokenException("access token is expired");
            }
        }
    }
}
