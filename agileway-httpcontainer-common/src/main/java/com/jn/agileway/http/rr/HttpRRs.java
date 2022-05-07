package com.jn.agileway.http.rr;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.LinkedMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

public class HttpRRs {
    public static final String getUTF8ContentType(@NonNull String mediaType) {
        return getContentType(mediaType, "UTF-8");
    }

    public static final String getContentType(@NonNull String mediaType, @Nullable String encoding) {
        return mediaType + ";charset=" + encoding;
    }

    public static MultiValueMap<String, String> headersToMultiValueMap(final HttpRequest request) {
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        Collects.forEach(request.getHeaderNames(), new Consumer<String>() {
            @Override
            public void accept(String headerName) {
                map.addAll(headerName, Pipeline.<String>of(request.getHeaders(headerName)).asList());
            }
        });
        return map;
    }

    public static MultiValueMap<String, String> headersToMultiValueMap(final HttpResponse response) {
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        Collects.forEach(response.getHeaderNames(), new Consumer<String>() {
            @Override
            public void accept(String headerName) {
                map.addAll(headerName, Pipeline.<String>of(response.getHeaders(headerName)).asList());
            }
        });
        return map;
    }

    public static HttpMethod getMethod(HttpRequest request) {
        return HttpMethod.valueOf(request.getMethod());
    }

    /**
     * Content-Length
     *
     * @param response
     * @return
     */
    public static long getContentLength(HttpResponse response) {
        return Objs.useValueIfNull(getContentLength(response, true), 0L);
    }

    public static Long getContentLength(HttpResponse response, boolean useZeroIfNull) {
        String contentLengthStr = response.getHeader(HttpHeaders.CONTENT_LENGTH);
        if (useZeroIfNull) {
            contentLengthStr = Strings.useValueIfBlank(contentLengthStr, "0");
        }
        if (Emptys.isEmpty(contentLengthStr)) {
            return null;
        }
        return Long.parseLong(contentLengthStr);
    }

}
