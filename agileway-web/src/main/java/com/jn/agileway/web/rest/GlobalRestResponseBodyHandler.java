package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface GlobalRestResponseBodyHandler<ACTION> {
    void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration);
    RestRespBody handleResponseBody(HttpServletRequest request, HttpServletResponse response, ACTION action, Object actionReturnValue) ;
}
