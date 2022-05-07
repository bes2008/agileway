package com.jn.agileway.web.rr;

import com.jn.langx.Factory;

public interface HttpResponseFactory<I> extends Factory<I,HttpResponse> {
    @Override
    public HttpResponse get(I response) ;
}
