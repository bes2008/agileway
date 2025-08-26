package com.jn.agileway.httpclient.auth;

import com.jn.langx.Builder;

public abstract class AuthorizationHeaderBuilder<B extends AuthorizationHeaderBuilder, W extends WwwAuthenticate, C extends Credentials> implements Builder<String> {
    protected C credentials;
    protected W wwwAuthenticate;

    public B withCredentials(C credentials) {
        this.credentials = credentials;
        return (B) this;
    }

    public B withWwwAuthenticate(W wwwAuthenticate) {
        this.wwwAuthenticate = wwwAuthenticate;
        return (B) this;
    }

}
