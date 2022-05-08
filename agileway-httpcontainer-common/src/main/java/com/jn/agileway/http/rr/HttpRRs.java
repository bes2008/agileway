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
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.util.List;

public class HttpRRs {
    protected HttpRRs(){}


    private static List<String> clientIpHeadersInProxy = Collects.newArrayList(
            "X-Real-IP", // nginx
            "Proxy-Client-IP", // apache http server
            "WL-Proxy-Client-IP", // WebLogic 服务代理
            "X-Forwarded-For", // squid
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    );

    public static String getClientIP(final HttpRequest request) {
        String header = Collects.findFirst(clientIpHeadersInProxy, new Predicate<String>() {
            @Override
            public boolean test(String headerName) {
                String ips = request.getHeader(headerName);
                if (Emptys.isNotEmpty(ips) && !Strings.equalsIgnoreCase("unknown", ips)) {
                    return true;
                }
                return false;
            }
        });
        String ip = Strings.isEmpty(header) ? request.getRemoteAddr() : request.getHeader(header);
        if (Strings.isNotEmpty(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            }
        }
        return ip;
    }

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
