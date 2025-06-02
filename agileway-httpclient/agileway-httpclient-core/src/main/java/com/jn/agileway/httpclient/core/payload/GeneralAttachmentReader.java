package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.util.ContentDisposition;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.io.resource.InputStreamResource;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.net.mime.MediaType;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

/**
 * 处理附件下载
 */
public class GeneralAttachmentReader extends CustomMediaTypesHttpResponseReader<Resource> {

    public GeneralAttachmentReader() {
        addSupportedMediaType(MediaType.APPLICATION_OCTET_STREAM);
    }

    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        String contentDispositionValue = response.getHttpHeaders().getFirstHeader("Content-Disposition");
        return HttpClientUtils.isAttachmentResponse(contentDispositionValue);
    }

    @Override
    public Resource read(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) throws Exception {
        String contentDispositionValue = response.getHttpHeaders().getFirstHeader("Content-Disposition");
        ContentDisposition contentDisposition = ContentDisposition.parseResponseHeader(contentDispositionValue);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getPayload());

        return new InputStreamResource(inputStream, contentDisposition.getFilename());
    }
}
