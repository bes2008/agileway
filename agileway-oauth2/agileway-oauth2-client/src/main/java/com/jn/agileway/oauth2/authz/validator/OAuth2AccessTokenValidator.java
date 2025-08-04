package com.jn.agileway.oauth2.authz.validator;

import com.jn.agileway.oauth2.authz.exception.InvalidAccessTokenException;

public interface OAuth2AccessTokenValidator<T> {
    void validate(T token) throws InvalidAccessTokenException;
}
