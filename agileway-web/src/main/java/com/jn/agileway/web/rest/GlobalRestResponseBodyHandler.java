package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface GlobalRestResponseBodyHandler<ACTION> {

    void setContext(GlobalRestResponseBodyContext context);
    GlobalRestResponseBodyContext getContext();

    RestRespBody handleResponseBody(HttpServletRequest request, HttpServletResponse response, ACTION action, Object actionReturnValue);

    Map<String, Object> toMap(HttpServletRequest request, HttpServletResponse response, ACTION action, RestRespBody respBody);
}
