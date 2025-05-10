package com.jn.agileway.httpclient.util;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.http.HttpHeaders;

import java.util.List;

public class HttpUtils {
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
}
