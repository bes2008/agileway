package com.jn.agileway.httpclient.core.serialize;

import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentDisposition;
import com.jn.langx.io.resource.InputStreamResource;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 处理附件下载
 */
public class GeneralAttachmentReader extends CustomMediaTypesHttpResponseReader<Resource> {

    public GeneralAttachmentReader() {
        addSupportedMediaType(MediaType.APPLICATION_OCTET_STREAM);
    }

    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        String contentDispositionValue = response.getHeaders().getFirstHeader("Content-Disposition");
        if (Strings.isBlank(contentDispositionValue)) {
            return false;
        }

        ContentDisposition contentDisposition = ContentDisposition.parseResponseHeader(contentDispositionValue);
        if (contentDisposition == null) {
            return false;
        }

        if (!contentDisposition.isAttachment()) {
            return false;
        }

        return true;
    }

    @Override
    public Resource read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws IOException {
        String contentDispositionValue = response.getHeaders().getFirstHeader("Content-Disposition");
        ContentDisposition contentDisposition = ContentDisposition.parseResponseHeader(contentDispositionValue);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOs.copy(response.getContent(), bos);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bos.toByteArray());

        return new InputStreamResource(inputStream, contentDisposition.getFilename());
    }
}
