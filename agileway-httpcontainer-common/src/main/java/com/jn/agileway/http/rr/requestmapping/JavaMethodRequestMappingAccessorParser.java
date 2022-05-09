package com.jn.agileway.http.rr.requestmapping;

import java.lang.reflect.Method;

public interface JavaMethodRequestMappingAccessorParser extends RequestMappingAccessorParser<Method>{
    @Override
    RequestMappingAccessor parse(Method method);
}
