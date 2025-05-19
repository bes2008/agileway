package com.jn.agileway.httpclient.util;

import com.jn.langx.security.Securitys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HttpClientUtils {
    public static boolean isSSLEnabled(String httpUri) {
        return Strings.startsWith(httpUri, "https://") || Strings.startsWith(httpUri, "wss://");
    }

    public static boolean isSSLEnabled(URI httpUri) {
        String scheme = httpUri.getScheme();
        return "https".equals(scheme) || "wss".equals(scheme);
    }

    public static boolean isForm(MediaType contentType) {
        return isSimpleForm(contentType) || isMultipartForm(contentType);
    }

    public static boolean isSimpleForm(MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public static boolean isMultipartForm(MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        return "multipart".equals(contentType.getType());
    }

    public static List<ContentEncoding> getContentEncodings(HttpHeaders headers) {
        String[] contentEncodings = Strings.split(headers.getFirst("Content-Encoding"), ",");
        return getContentEncodings(Lists.newArrayList(contentEncodings));
    }

    public static List<ContentEncoding> getContentEncodings(List<String> contentEncodings) {
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

    /**
     * 请求 content 是否要在本地先放到Buffer中，再发送。不放在Buffer中，就是直接放到不可重复读取的stream中
     */
    public static boolean requestBodyUseStreamMode(HttpMethod method, HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        // 文件上传请求时
        if (method == HttpMethod.POST && isMultipartForm(contentType)) {
            return true;
        }
        // 内容压缩时
        List<ContentEncoding> contentEncodings = getContentEncodings(headers);
        if (Objs.isNotEmpty(contentEncodings)) {
            return true;
        }
        return false;
    }

    public static boolean isWriteable(HttpMethod method) {
        return method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH;
    }

    public static String generateMultipartBoundary() {
        Random random = Securitys.getSecureRandom();
        int prefixLength = random.nextInt(7);
        byte[] boundary = new byte[prefixLength + 7];
        for (int i = 0; i < prefixLength; i++) {
            boundary[i] = '-';
        }
        for (int i = prefixLength; i < boundary.length; i++) {
            boundary[i] = BOUNDARY_ALPHABET[random.nextInt(BOUNDARY_ALPHABET.length)];
        }
        return new String(boundary, Charsets.US_ASCII);
    }

    private static final byte[] BOUNDARY_ALPHABET =
            new byte[]{'-', '_', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                    'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
                    'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                    'V', 'W', 'X', 'Y', 'Z'};


}
