package com.jn.agileway.httpclient.core;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import javax.net.ssl.HostnameVerifier;
import java.net.URI;

public interface UnderlyingHttpRequestFactory {
    HostnameVerifier getHostnameVerifier();

    UnderlyingHttpRequest create(@NonNull HttpMethod method, @NonNull URI uri, @Nullable HttpHeaders httpHeaders) throws Exception;
}
