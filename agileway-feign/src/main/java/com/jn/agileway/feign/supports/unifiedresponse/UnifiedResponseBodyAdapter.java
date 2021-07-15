package com.jn.agileway.feign.supports.unifiedresponse;

import com.jn.langx.Filter;
import feign.Response;

import java.lang.reflect.Type;

public interface UnifiedResponseBodyAdapter<REST_BODY> extends Filter<Object> {
    @Override
    boolean accept(Object result);

    REST_BODY adapt(Response response, Type type, Object o);
}
