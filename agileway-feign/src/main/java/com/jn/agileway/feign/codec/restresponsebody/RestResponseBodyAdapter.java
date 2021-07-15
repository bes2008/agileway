package com.jn.agileway.feign.codec.restresponsebody;

import com.jn.langx.Filter;
import com.jn.langx.http.rest.RestRespBody;
import feign.Response;

import java.lang.reflect.Type;

public interface RestResponseBodyAdapter extends Filter<Object> {
    @Override
    boolean accept(Object result);

    RestRespBody adapt(Response response, Type type, Object o);
}
