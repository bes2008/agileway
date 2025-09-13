package com.jn.agileway.httpclient.core.auth;

import com.jn.agileway.httpclient.auth.inject.CredentialsInjector;
import com.jn.agileway.httpclient.auth.inject.ServiceCredentialsInjector;
import com.jn.agileway.httpclient.core.HttpRequest;

public class AuthCredentialsInjector extends ServiceCredentialsInjector<HttpRequest> {
    public AuthCredentialsInjector(String name, String baseUri, CredentialsInjector<HttpRequest>... injectors) {
        super(name, baseUri, AuthRequestUrlGetter.INSTANCE, injectors);
    }
}
