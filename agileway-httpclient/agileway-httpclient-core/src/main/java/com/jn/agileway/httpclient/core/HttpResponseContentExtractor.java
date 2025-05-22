package com.jn.agileway.httpclient.core;

import com.jn.langx.annotation.NonNull;

public interface HttpResponseContentExtractor {
    @NonNull
    HttpResponse extract(UnderlyingHttpResponse underlyingHttpResponse);
}
