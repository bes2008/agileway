package com.jn.agileway.httpclient.auth.inject;

import com.jn.agileway.httpclient.auth.Credentials;
import com.jn.agileway.httpclient.auth.CredentialsProvider;

public abstract class AbstractCredentialsInjector<R> implements CredentialsInjector<R> {
    protected RequestMatcher<R> matcher;
    protected CredentialsProvider credentialsProvider;

    protected AbstractCredentialsInjector(RequestMatcher matcher, CredentialsProvider credentialsProvider) {
        this.matcher = matcher;
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    public RequestMatcher<R> getRequestMatcher() {
        return this.matcher;
    }

    public final void inject(R httpRequest) {
        if (!credentialsAbsent(httpRequest)) {
            return;
        }

        Credentials credentials = this.credentialsProvider.get(httpRequest);
        if (credentials != null) {
            injectCredentials(httpRequest, credentials);
        }
    }

    protected abstract void injectCredentials(R httpRequest, Credentials credentials);

    protected boolean credentialsAbsent(R httpRequest) {
        return true;
    }
}
