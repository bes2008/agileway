package com.jn.agileway.httpclient.util;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HttpClientUtils {
    public static List<ContentEncoding> getContentEncoding(HttpHeaders headers) {
        List<String> contentEncodings = headers.get("Content-Encoding");
        if (Objs.isEmpty(contentEncodings)) {
            return Lists.newArrayList();
        }

        return Pipeline.of(contentEncodings).map(new Function<String, ContentEncoding>() {
            @Override
            public ContentEncoding apply(String contentEncoding) {
                return ContentEncoding.ofName(contentEncoding);
            }
        }).clearNulls().asList();
    }

    /**
     * 处理请求头中的 Content-Encoding
     */
    public static OutputStream wrapByContentEncodings(OutputStream outputStream, List<ContentEncoding> contentEncodings) throws IOException {
        if (Objs.isNotEmpty(contentEncodings)) {
            for (ContentEncoding contentEncoding : contentEncodings) {
                if (contentEncoding == null) {
                    continue;
                }
                if (ContentEncoding.GZIP.equals(contentEncoding)) {
                    outputStream = new GZIPOutputStream(outputStream);
                } else if (ContentEncoding.DEFLATE.equals(contentEncoding)) {
                    outputStream = new DeflaterOutputStream(outputStream);
                } else {
                    throw new IOException("Unsupported http Content-Encoding: " + contentEncoding.getName());
                }
            }
        }
        return outputStream;
    }

    /**
     * 包装 http响应中的输入流
     */
    public static InputStream wrapByContentEncodings(InputStream inputStream, List<ContentEncoding> contentEncodings) throws IOException {
        if (Objs.isNotEmpty(contentEncodings)) {
            for (ContentEncoding contentEncoding : contentEncodings) {
                if (contentEncoding == null) {
                    continue;
                }
                if (ContentEncoding.GZIP.equals(contentEncoding)) {
                    inputStream = new GZIPInputStream(inputStream);
                } else if (ContentEncoding.DEFLATE.equals(contentEncoding)) {
                    inputStream = new DeflaterInputStream(inputStream);
                } else {
                    throw new IOException("Unsupported http Content-Encoding: " + contentEncoding.getName());
                }
            }
        }
        return inputStream;
    }
}
