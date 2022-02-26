package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractGlobalRestResponseBodyHandler<ACTION> extends AbstractGlobalRestResponseHandler<ACTION, Object>{
    @Override
    public abstract RestRespBody handle(HttpServletRequest request, HttpServletResponse response, ACTION action, Object actionReturnValue);
}
