package com.jn.agileway.httpclient.core.auth;

import com.jn.agileway.httpclient.auth.inject.RequestUrlGetter;
import com.jn.agileway.httpclient.core.HttpRequest;

public class AuthRequestUrlGetter implements RequestUrlGetter<HttpRequest> {
    public static final AuthRequestUrlGetter INSTANCE = new AuthRequestUrlGetter();

    @Override
    public String getUrl(HttpRequest request) {
        return request.getUri().toString();
    }
}
