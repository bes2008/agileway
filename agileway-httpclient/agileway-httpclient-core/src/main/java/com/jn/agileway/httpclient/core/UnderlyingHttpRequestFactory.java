package com.jn.agileway.httpclient.core;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

import java.net.URI;

public interface UnderlyingHttpRequestFactory {
    UnderlyingHttpRequest create(@NonNull HttpMethod method, @NonNull URI uri, @Nullable MediaType contentType) throws Exception;
}
