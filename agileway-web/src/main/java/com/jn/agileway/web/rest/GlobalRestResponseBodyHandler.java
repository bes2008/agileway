package com.jn.agileway.web.rest;

import com.jn.easyjson.core.JSONFactory;
import com.jn.langx.http.rest.RestRespBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface GlobalRestResponseBodyHandler<ACTION> {
    void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration);

    void setJsonFactory(JSONFactory jsonFactory);
    JSONFactory getJsonFactory();

    RestRespBody handleResponseBody(HttpServletRequest request, HttpServletResponse response, ACTION action, Object actionReturnValue);
}
