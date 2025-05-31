package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface UnderlyingHttpExecutor<TARGET> {
    HttpResponse<InputStream> execute(HttpRequest<ByteArrayOutputStream> request) throws Exception;
}
