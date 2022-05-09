package com.jn.agileway.jaxrs.rest;

import com.jn.agileway.http.rest.AbstractGlobalRestExceptionHandler;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;

public class JaxrsGlobalRestExceptionHandler extends AbstractGlobalRestExceptionHandler {
    @Override
    protected boolean isSupportedRestAction(HttpRequest request, HttpResponse response, Object action, Exception ex) {
        return false;
    }
}
