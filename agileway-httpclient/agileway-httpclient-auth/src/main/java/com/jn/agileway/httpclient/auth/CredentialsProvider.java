package com.jn.agileway.httpclient.auth;

import com.jn.langx.Provider;

public interface CredentialsProvider<R, C extends Credentials> extends Provider<R, C> {
    @Override
    C get(R httpRequest);
}
