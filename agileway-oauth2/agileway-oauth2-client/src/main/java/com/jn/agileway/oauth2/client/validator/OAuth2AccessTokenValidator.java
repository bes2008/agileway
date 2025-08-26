package com.jn.agileway.oauth2.client.validator;

import com.jn.agileway.oauth2.client.exception.InvalidAccessTokenException;

public interface OAuth2AccessTokenValidator<T> {
    void validate(T token) throws InvalidAccessTokenException;
}
