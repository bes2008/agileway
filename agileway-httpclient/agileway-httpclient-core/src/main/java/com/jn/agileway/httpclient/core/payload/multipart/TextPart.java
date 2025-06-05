package com.jn.agileway.httpclient.core.payload.multipart;

import com.jn.agileway.httpclient.util.ContentDisposition;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.net.mime.MediaType;

public class TextPart extends Part<String> {
    public TextPart(String fieldName, String content) {
        this(fieldName, content, null);
    }

    public TextPart(@NotEmpty String fieldName, @Nullable String content, @Nullable String contentType) {
        super();
        setName(Preconditions.checkNotEmpty(fieldName));
        setContent(content);

        MediaType mediaType = MediaType.parseMediaType(contentType);
        setContentType(mediaType.getType() + "/" + mediaType.getSubtype());
        if (mediaType.getCharset() != null) {
            setCharset(mediaType.getCharset());
        }
        setContentDisposition(ContentDisposition.forFormData(fieldName, null));
    }
}
