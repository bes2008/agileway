package com.jn.agileway.httpclient.auth.inject;

import com.jn.agileway.httpclient.auth.Credentials;

public class NoopCredentialsInjector<R> extends AbstractCredentialsInjector<R> {
    public NoopCredentialsInjector(RequestMatcher matcher) {
        super("NOOP", matcher, new NullCredentialsProvider());
    }

    @Override
    protected void injectCredentials(R httpRequest, Credentials credentials) {
        // do nothing
    }

    @Override
    public String getName() {
        return "NOOP Credentials Injector";
    }
}
