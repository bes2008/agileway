package com.jn.agileway.httpclient.core.multipart;

import com.jn.agileway.httpclient.util.ContentDisposition;
import com.jn.langx.annotation.NonNull;

import java.nio.charset.Charset;

public class Part {
    /**
     * field name
     */
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
    private String contentType = "text/plain";
    /**
     * 当 content-type 为 "text/plain" 时，它来指定content 的 charset
     */
    private Charset charset;
}
