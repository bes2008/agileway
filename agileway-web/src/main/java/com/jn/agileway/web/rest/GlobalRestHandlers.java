package com.jn.agileway.web.rest;

import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.reflect.Reflects;

public class GlobalRestHandlers {
    /**
     * 记录到 request attribute中，用于判断是否已经写过
     */
    public static final String GLOBAL_REST_RESPONSE_HAD_WRITTEN = "GLOBAL_REST_RESPONSE_HAD_WRITTEN";
    /**
     * 用于记录 exception 是否处理过
     */
    public static final String GLOBAL_REST_EXCEPTION_HANDLER = Reflects.getFQNClassName(GlobalRestExceptionHandler.class);

    /**
     * 指定某个请求不是 REST 请求，或者没有启用全局处理的请求
     */
    public static final String GLOBAL_REST_NON_REST_REQUEST = "GLOBAL_REST_NON_REST_REQUEST";

    /**
     * 响应类型： JSON
     */
    public static final String RESPONSE_CONTENT_TYPE_JSON_UTF8 = Servlets.getUTF8ContentType(MediaType.APPLICATION_JSON_VALUE);
}
