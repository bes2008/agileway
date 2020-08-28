package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RestActionExceptionHandler {
    <T> RestRespBody<T> handle(HttpServletRequest request, HttpServletResponse response,  Object handler, Exception ex);
}
