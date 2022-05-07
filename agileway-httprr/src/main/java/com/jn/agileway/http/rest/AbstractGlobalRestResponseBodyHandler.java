package com.jn.agileway.http.rest;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.http.rest.RestRespBody;


public abstract class AbstractGlobalRestResponseBodyHandler<ACTION> extends AbstractGlobalRestResponseHandler<ACTION, Object>{
    @Override
    public abstract RestRespBody handle(HttpRequest request, HttpResponse response, ACTION action, Object actionReturnValue);
}
