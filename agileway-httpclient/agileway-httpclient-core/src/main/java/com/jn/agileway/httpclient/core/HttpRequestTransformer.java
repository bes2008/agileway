package com.jn.agileway.httpclient.core;

import com.jn.langx.Transformer;


/**
 * 用于对请求体内容进行转换
 */
public interface HttpRequestTransformer extends Transformer<FilteringHttpRequest, FilteringHttpRequest> {
    @Override
    FilteringHttpRequest transform(FilteringHttpRequest request);

    boolean canTransform(FilteringHttpRequest request);
}
