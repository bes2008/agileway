package com.jn.agileway.httpclient.auth.inject;

import com.jn.agileway.httpclient.auth.Credentials;
import com.jn.agileway.httpclient.auth.CredentialsProvider;
import com.jn.langx.AbstractNameable;
import com.jn.langx.annotation.NonNull;

public abstract class AbstractCredentialsInjector<R> extends AbstractNameable implements CredentialsInjector<R> {
    @NonNull
    protected RequestMatcher<R> matcher;

    @NonNull
    protected CredentialsInjectionContext context;
    @NonNull
    protected CredentialsProvider credentialsProvider;

    protected AbstractCredentialsInjector(String name, RequestMatcher matcher, CredentialsProvider credentialsProvider) {
        setName(name);
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

    @Override
    public CredentialsInjectionContext getContext() {
        return this.context;
    }

    @Override
    public void setContext(CredentialsInjectionContext context) {
        this.context = context;
        if (this.matcher instanceof PathRequestMatcher) {
            ((PathRequestMatcher) this.matcher).setContext(context);
        }
    }
}
