package com.jn.agileway.web.rr;

import com.jn.langx.Factory;

public interface HttpRequestFactory<I> extends Factory<I,HttpRequest> {
    @Override
    HttpRequest get(I request);
}
