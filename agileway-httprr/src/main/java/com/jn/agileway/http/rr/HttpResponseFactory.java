package com.jn.agileway.http.rr;

import com.jn.langx.Factory;

public interface HttpResponseFactory<I> extends Factory<I,HttpResponse> {
    @Override
    public HttpResponse get(I response) ;
}
