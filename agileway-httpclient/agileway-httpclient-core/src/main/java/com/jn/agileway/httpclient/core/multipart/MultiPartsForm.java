package com.jn.agileway.httpclient.core.multipart;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Lists;

import java.nio.charset.Charset;
import java.util.List;

public class MultiPartsForm {
    @Nullable
    private String boundary;
    @Nullable
    private Charset charset;
    private List<Part<?>> parts = Lists.newArrayList();

    public MultiPartsForm(Charset charset) {
        this.charset = charset;
    }

    public MultiPartsForm() {
    }

    public void addPart(TextPart textPart) {
        parts.add(textPart);
    }

    public void addPart(FilePart filePart) {
        parts.add(filePart);
    }

    public String getBoundary() {
        return boundary;
    }

    public Charset getCharset() {
        return charset;
    }

    public List<Part<?>> getParts() {
        return parts;
    }
}
