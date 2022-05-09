package com.jn.agileway.jaxrs.rest;

import com.jn.agileway.http.rest.AbstractGlobalRestResponseHandler;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.http.rest.RestRespBody;

public class JaxrsGlobalRestResponseBodyHandler extends AbstractGlobalRestResponseHandler {
    @Override
    public RestRespBody handle(HttpRequest request, HttpResponse response, Object o, Object actionReturnValue) {
        // 获取不到 handler，只能根据 path 来判断是否要进行 json转换

        return null;
    }
}
