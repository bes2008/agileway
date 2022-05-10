package com.jn.agileway.jaxrs.rest;

import com.jn.agileway.http.rest.AbstractGlobalRestExceptionHandler;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.mime.MediaType;

public class JaxrsGlobalRestExceptionHandler extends AbstractGlobalRestExceptionHandler {
    @Override
    protected boolean isSupportedRestAction(HttpRequest request, HttpResponse response, Object action, Exception ex) {
        String header = request.getHeader(HttpHeaders.ACCEPT);
        return header.contains(MediaType.APPLICATION_JSON_VALUE) || header.contains("*/*");
    }
}
