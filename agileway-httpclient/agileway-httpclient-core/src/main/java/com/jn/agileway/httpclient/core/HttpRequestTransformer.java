package com.jn.agileway.httpclient.core;

import com.jn.langx.Transformer;

public interface HttpRequestTransformer extends Transformer<FilteringHttpRequest, FilteringHttpRequest> {
    @Override
    FilteringHttpRequest transform(FilteringHttpRequest request);

    boolean canTransform(FilteringHttpRequest request);
}
