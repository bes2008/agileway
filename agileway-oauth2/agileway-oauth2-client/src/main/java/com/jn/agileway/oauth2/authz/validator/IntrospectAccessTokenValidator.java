package com.jn.agileway.oauth2.authz.validator;

import com.jn.agileway.oauth2.authz.IntrospectResult;
import com.jn.agileway.oauth2.authz.exception.ExpiredAccessTokenException;
import com.jn.agileway.oauth2.authz.exception.InvalidAccessTokenException;

public class IntrospectAccessTokenValidator implements OAuth2AccessTokenValidator<IntrospectResult> {
    @Override
    public void validate(IntrospectResult introspectResult) throws InvalidAccessTokenException {
        if (!introspectResult.isActive()) {
            throw new InvalidAccessTokenException("access token is inactive");
        }

        long now = System.currentTimeMillis() / 1000;

        if (introspectResult.getExp() != null && introspectResult.getExp() < now) {
            throw new ExpiredAccessTokenException("access token is expired");
        }

        if (introspectResult.getIat() != null && introspectResult.getIat() > now) {
            throw new InvalidAccessTokenException("access token is not yet valid");
        }

        if (introspectResult.getNbf() != null && introspectResult.getNbf() > now) {
            throw new InvalidAccessTokenException("access token is not yet valid");
        }

    }
}
