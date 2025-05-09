package com.jn.agileway.httpclient.core;

import java.io.IOException;

/**
 * 用于在请求发送之前，进行拦截处理
 */
public interface HttpRequestInterceptor {
    /**
     * 对请求进行拦截处理。如果需要继续处理，请返回true，否则返回false。
     * 拦截处理过程中，可以进行修改请求地址，请求方法，请求头，请求体。
     *
     * @param request 请求地址
     * @throws IOException 拦截器抛出的异常
     */
    void intercept(InterceptingHttpRequest request) throws IOException;

}
