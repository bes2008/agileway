package com.jn.agileway.httpclient.core.content.multipart;

import com.jn.agileway.httpclient.util.ContentDisposition;
import com.jn.langx.annotation.NonNull;

import java.nio.charset.Charset;

public class Part<T> {
    /**
     * field name
     */
    @NonNull
    private String name;

    /**
     * content disposition Header
     */
    @NonNull
    private ContentDisposition contentDisposition;
    /**
     * Content-Type
     */
    @NonNull
    private String contentType;
    /**
     * 当 content-type 为 "text/plain" 时，它来指定content 的 charset
     */
    private Charset charset = null;

    private T content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContentDisposition getContentDisposition() {
        return contentDisposition;
    }

    public void setContentDisposition(ContentDisposition contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}
