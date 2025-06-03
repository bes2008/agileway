package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.multipart.MultiPartsForm;

import java.io.ByteArrayOutputStream;

public interface UnderlyingHttpExecutor<TARGET> {
    UnderlyingHttpResponse executeBufferedRequest(HttpRequest<ByteArrayOutputStream> request) throws Exception;

    UnderlyingHttpResponse executeAttachmentUploadRequest(HttpRequest<MultiPartsForm> request) throws Exception;

}
