package com.jn.agileway.http.rr.requestmapping;

import com.jn.langx.Parser;

public interface RequestMappingAccessorParser<RequestHandler> extends Parser<RequestHandler,  RequestMappingAccessor> {
    @Override
    RequestMappingAccessor parse(RequestHandler requestHandler);
}
