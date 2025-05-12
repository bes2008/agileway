package com.jn.agileway.httpclient.core.serialize;

import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.io.resource.ByteArrayResource;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralResourceHttpResponseReader extends CustomMediaTypesHttpResponseReader<Resource> {
    public GeneralResourceHttpResponseReader() {

        // 办公文件
        addSupportedMediaType(MediaType.parseMediaType("application/vnd.ms-excel")); // .xls
        addSupportedMediaType(MediaType.parseMediaType("application/pdf")); // .pdf
        addSupportedMediaType(MediaType.parseMediaType("application/msword")); // .doc
        addSupportedMediaType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")); // .docx
        addSupportedMediaType(MediaType.parseMediaType("application/vnd.ms-powerpoint")); //  .ppt
        // 未知类型的二进制文件
        addSupportedMediaType(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        if (expectedContentType == Resource.class) {
            return true;
        }
        // 是文件
        if ("image".equals(contentType.getType())) {
            return true;
        }
        if (isFont(contentType)) {
            return true;
        }
        return super.canRead(response, contentType, expectedContentType);
    }

    private boolean isFont(MediaType contentType) {
        if ("font".equals(contentType.getType())) {
            return true;
        } else if ("application".equals(contentType.getType())) {
            return Strings.startsWith(contentType.getSubtype(), "font-") || Strings.contains(contentType.getSubtype(), "-fontobject");
        }
        return false;
    }

    @Override
    public Resource read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws IOException {
        int contentLength = (int) response.getHeaders().getContentLength();
        if (contentLength < 0) {
            throw new IOException("Content-Length header is required for Content-Type " + contentType);
        }
        byte[] bytes = new byte[contentLength];
        if (contentLength > 0) {
            IOs.read(response.getContent(), bytes);
        }
        return new ByteArrayResource(bytes);
    }
}
