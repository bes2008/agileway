package com.jn.agileway.http.rest;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.http.rest.RestRespBody;

import java.util.Map;

public interface GlobalRestResponseBodyHandler<ACTION, ACTION_RESULT> {

    void setContext(GlobalRestResponseBodyContext context);
    GlobalRestResponseBodyContext getContext();

    RestRespBody handle(HttpRequest request, HttpResponse response, ACTION action, ACTION_RESULT actionReturnValue);

    Map<String, Object> toMap(HttpRequest request, HttpResponse response, ACTION action, RestRespBody respBody);
}
