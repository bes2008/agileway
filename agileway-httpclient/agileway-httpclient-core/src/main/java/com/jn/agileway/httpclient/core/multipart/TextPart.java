package com.jn.agileway.httpclient.core.multipart;

import com.jn.agileway.httpclient.util.ContentDisposition;

public class TextPart extends Part<String> {
    public TextPart(String fieldName, String content, String contentType) {
        super();
        setName(fieldName);
        setContent(content);
        setContentType(contentType);
        setContentDisposition(ContentDisposition.ofFormData(fieldName, null));
    }
}
