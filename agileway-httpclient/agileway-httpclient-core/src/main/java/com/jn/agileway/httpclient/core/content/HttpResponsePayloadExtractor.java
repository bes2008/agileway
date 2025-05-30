package com.jn.agileway.httpclient.core.content;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.annotation.NonNull;

public interface HttpResponsePayloadExtractor {
    @NonNull
    HttpResponse extract(UnderlyingHttpResponse underlyingHttpResponse) throws Exception;
}
