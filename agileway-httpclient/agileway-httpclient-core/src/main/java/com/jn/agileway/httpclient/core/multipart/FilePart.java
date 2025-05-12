package com.jn.agileway.httpclient.core.multipart;

import com.jn.agileway.httpclient.util.ContentDisposition;
import com.jn.langx.util.Objs;

import java.io.*;

public class FilePart extends Part<InputStream> {
    public FilePart(String fieldName, String filename, byte[] content, String contentType) {
        this(fieldName, filename, new ByteArrayInputStream(content), contentType);
    }

    public FilePart(String fieldName, String filename, File content, String contentType) throws IOException {
        this(fieldName, filename, new FileInputStream(content), contentType);
    }

    public FilePart(String fieldName, String filename, InputStream content, String contentType) {
        super();
        setName(fieldName);
        setContent(content);
        setContentType(Objs.useValueIfEmpty(contentType, "application/octet-stream"));
        setContentDisposition(ContentDisposition.ofFormData(fieldName, filename));
    }
}
