package com.jn.agileway.httpclient.auth.inject;

import com.jn.langx.Named;

public interface CredentialsInjector<R> extends Named {
    RequestMatcher<R> getRequestMatcher();

    void inject(R httpRequest);

    CredentialsInjectionContext getContext();

    void setContext(CredentialsInjectionContext context);
}
