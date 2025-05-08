package com.jn.agileway.httpclient.core;

import java.io.IOException;

public interface HttpRequestInterceptor {
    /**
     * 对请求进行拦截处理。如果需要继续处理，请返回true，否则返回false。
     * 拦截处理过程中，可以进行修改请求地址，请求方法，请求头，请求体。
     *
     * @param uri     请求地址
     * @param method  请求方法
     * @param headers 请求头
     * @param body    请求体
     * @return 是否继续处理
     * @throws IOException 拦截器抛出的异常
     */
    boolean intercept(FilteringHttpRequest request) throws IOException;
}
