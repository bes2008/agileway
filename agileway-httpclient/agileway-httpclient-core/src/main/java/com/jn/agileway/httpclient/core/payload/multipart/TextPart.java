package com.jn.agileway.httpclient.core.payload.multipart;

import com.jn.agileway.httpclient.util.ContentDisposition;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;

public class TextPart extends Part<String> {
    public TextPart(String fieldName, String content) {
        this(fieldName, content, null);
    }

    public TextPart(@NotEmpty String fieldName, @Nullable String content, @Nullable String contentType) {
        super();
        setName(Preconditions.checkNotEmpty(fieldName));
        setContent(content);
        setContentType(Objs.useValueIfEmpty(contentType, "text/plain"));
        setContentDisposition(ContentDisposition.forFormData(fieldName, null));
    }
}
