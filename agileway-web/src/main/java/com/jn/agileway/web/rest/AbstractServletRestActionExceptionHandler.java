package com.jn.agileway.web.rest;

import com.jn.agileway.http.rest.RestActionExceptionHandler;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.http.rest.RestRespBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractServletRestActionExceptionHandler<Entity> implements RestActionExceptionHandler<Entity> {
    @Override
    public RestRespBody<Entity> handle(HttpRequest request, HttpResponse response, Object handler, Exception ex) {
        return this.handle((HttpServletRequest) request.getDelegate(), (HttpServletResponse) response.getDelegate(), handler, ex);
    }

    public abstract RestRespBody<Entity> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);
}
