package com.jn.agileway.web.servlet;


import com.jn.agileway.http.rr.HttpRRs;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.io.stream.bandwidthlimit.BandwidthLimitedOutputStream;
import com.jn.langx.io.stream.bandwidthlimit.BandwidthLimiter;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.*;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.file.FileIOMode;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.http.HttpRange;
import com.jn.langx.util.net.mime.MediaType;
import org.slf4j.Logger;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

public class Servlets extends HttpRRs {
    private static final Logger logger = Loggers.getLogger(Servlets.class);

    public static final String X_FRAME_OPTIONS_HEADER = "X-Frame-Options";
    public static final String X_FRAME_OPTIONS_DENY = "deny";
    public static final String X_FRAME_OPTIONS_SAME_ORIGIN = "sameorigin";
    public static final String ACCESS_DENIED_403 = "AGILEWAY_ECURITY_403_EXCEPTION";


    /***********************************************************************
     *      Filters
     ***********************************************************************/

    public static Map<String, String> extractFilterInitParameters(final FilterConfig filterConfig) {
        return Pipeline.<String>of(filterConfig.getInitParameterNames()).collect(Collects.toHashMap(new Function<String, String>() {
            @Override
            public String apply(String name) {
                return name;
            }
        }, new Function<String, String>() {
            @Override
            public String apply(String name) {
                return filterConfig.getInitParameter(name);
            }
        }, true));
    }


    /***********************************************************************
     *      Header
     ***********************************************************************/

    /**
     * Content-Length
     *
     * @param response
     * @return
     */
    public static long getContentLength(HttpServletResponse response) {
        return Objs.useValueIfNull(getContentLength(response, true), 0L);
    }

    public static Long getContentLength(HttpServletResponse response, boolean useZeroIfNull) {
        return getContentLength(ServletHttpResponseFactory.INSTANCE.get(response), useZeroIfNull);
    }

    public static HttpHeaders getRequestHeaders(HttpServletResponse response) {
        return new HttpHeaders(headersToMultiValueMap(response));
    }

    public static MultiValueMap<String, String> headersToMultiValueMap(final HttpServletResponse response) {
        return headersToMultiValueMap(ServletHttpResponseFactory.INSTANCE.get(response));
    }

    public static HttpHeaders getRequestHeaders(HttpServletRequest request) {
        return new HttpHeaders(headersToMultiValueMap(request));
    }

    public static HttpMethod getMethod(HttpServletRequest request) {
        return HttpMethod.valueOf(request.getMethod());
    }

    public static MultiValueMap<String, String> headersToMultiValueMap(final HttpServletRequest request) {
        return headersToMultiValueMap(ServletHttpRequestFactory.INSTANCE.get(request));
    }

    public static String getClientIP(HttpServletRequest request) {
        return getClientIP(ServletHttpRequestFactory.INSTANCE.get(request));
    }

    /**
     * 下载文件
     *
     * @param request  request
     * @param response response
     * @param file     file
     * @param fileName the file name
     * @since 5.0.2
     */
    public static void downloadFile(
            HttpServletRequest request,
            HttpServletResponse response,
            File file,
            String fileName) throws IOException {
        downloadFile(request, response, file, -1, fileName, null, null);
    }

    /**
     * 下支持端点续传、限流的文件下载
     *
     * @param request       request
     * @param response      response
     * @param file          file
     * @param maxPacketSize the max packet size
     * @param fileName      the file name
     * @since 4.0.1
     */
    public static void downloadFile(
            HttpServletRequest request,
            HttpServletResponse response,
            File file,
            int maxPacketSize,
            String fileName,
            String contentType,
            String encoding) throws IOException {
        downloadFile(request, response, file, maxPacketSize, fileName, contentType, encoding, -1);
    }

    /**
     * 下支持端点续传、限流的文件下载
     *
     * @param request       request
     * @param response      response
     * @param file          file
     * @param maxPacketSize the max packet size
     * @param fileName      the file name
     * @since 4.0.1
     */
    public static void downloadFile(
            HttpServletRequest request,
            HttpServletResponse response,
            File file,
            int maxPacketSize, // 用于断点续传
            String fileName,
            String contentType,
            String encoding,
            int maxRatePerSecond // 用于限速， 单位为 kb/s
    ) throws IOException {

        contentType = Strings.useValueIfBlank(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        encoding = Strings.useValueIfBlank(encoding, Charsets.UTF_8.name());
        fileName = Strings.useValueIfBlank(fileName, file.getName());
        Preconditions.checkNotEmpty(fileName);
        if (maxPacketSize == 0) {
            maxPacketSize = (int) DataSizes.ONE_MB;
        }
        if (maxPacketSize < 0) {
            maxPacketSize = Integer.MAX_VALUE;
        }
        HttpHeaders httpHeaders = getRequestHeaders(request);
        List<HttpRange> ranges = httpHeaders.getRange();
        long offset = 0;
        long end = file.length();
        if (Emptys.isNotEmpty(ranges)) {
            HttpRange range = ranges.get(0);
            offset = range.getRangeStart(0);
            end = range.getRangeEnd(file.length());
        }
        if (end == -1) {
            end = file.length();
        }
        if (end == file.length()) {
            end = end - 1;
        }
        if (offset >= end || end == -1) {
            response.sendError(400, StringTemplates.formatWithPlaceholder("Invalid http request header: Range:{}-{}", offset, end));
            return;
        }
        RandomAccessFile raf = null;
        FileChannel channel = null;
        try {
            raf = new RandomAccessFile(file, FileIOMode.READ_ONLY.getIdentifier());
            channel = raf.getChannel();
            long sizeOfWillRead = end - offset + 1;
            if (sizeOfWillRead > maxPacketSize) {
                sizeOfWillRead = maxPacketSize;
            }
            int size = Numbers.toInt(sizeOfWillRead);
            end = offset + size - 1;
            if (offset + size > file.length()) {
                end = file.length() - 1;
                size = Numbers.toInt(file.length() - offset);
            }
            MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, offset, size);
            byte[] bytes = new byte[size];
            if (byteBuffer.hasRemaining()) {
                byteBuffer.get(bytes, 0, byteBuffer.remaining());
            }
            response.setCharacterEncoding(encoding);
            response.setContentType(contentType);
            response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            response.setHeader(HttpHeaders.CONTENT_RANGE, StringTemplates.formatWithPlaceholder("bytes {}-{}/{}", offset, end, file.length()));
            String disposition = StringTemplates.formatWithPlaceholder("attachment;filename={}", new String(fileName.getBytes(Charsets.UTF_8), Charsets.ISO_8859_1));
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, disposition);
            response.setHeader(HttpHeaders.CONTENT_LENGTH, "" + size);
            if (maxRatePerSecond <= 0) {
                response.getOutputStream().write(bytes);
            } else {
                BandwidthLimiter bandwidthLimiter = new BandwidthLimiter(maxRatePerSecond);
                BandwidthLimitedOutputStream bandwidthLimitedOutputStream = new BandwidthLimitedOutputStream(response.getOutputStream(), bandwidthLimiter);
                bandwidthLimitedOutputStream.write(bytes);
            }
            response.setStatus(200);
        } catch (Throwable ex) {
            logger.error("Error occur when download file: {}", fileName);
            response.setStatus(500);
        } finally {
            IOs.close(raf);
            IOs.close(channel);
        }
    }

    /**
     * @param request       请求
     * @param response      响应
     * @param bytes         完整的字节，如果是分段下载，这里也是总的，不是分段的，分段内容由方法内部来计算
     * @param maxPacketSize 下载包上限
     * @param fileName      文件名
     * @throws IOException
     */
    public static final void downloadBytes(
            HttpServletRequest request,
            HttpServletResponse response,
            @NonNull byte[] bytes,
            int maxPacketSize,
            @NonNull String fileName,
            @Nullable String contentType
    ) throws IOException {
        Preconditions.checkNotEmpty(fileName);
        if (maxPacketSize == 0) {
            maxPacketSize = (int) DataSizes.ONE_MB;
        }
        if (maxPacketSize < 0) {
            maxPacketSize = Integer.MAX_VALUE;
        }
        HttpHeaders httpHeaders = getRequestHeaders(request);
        List<HttpRange> ranges = httpHeaders.getRange();
        long offset = 0;
        long end = bytes.length - 1L;
        if (Emptys.isNotEmpty(ranges)) {
            HttpRange range = ranges.get(0);
            offset = range.getRangeStart(0);
            end = range.getRangeEnd(bytes.length);
        }
        if (end == -1) {
            end = bytes.length;
        }
        if (end == bytes.length) {
            end = end - 1;
        }
        if (offset >= end || end == -1) {
            response.sendError(400, StringTemplates.formatWithPlaceholder("Invalid http request header: Range:{}-{}", offset, end));
            return;
        }
        try {
            long sizeOfWillRead = end - offset + 1;
            if (sizeOfWillRead > maxPacketSize) {
                sizeOfWillRead = maxPacketSize;
            }
            int size = Numbers.toInt(sizeOfWillRead);
            end = offset + size - 1;
            if (offset + size > bytes.length) {
                end = bytes.length - 1L;
                size = Numbers.toInt(bytes.length - offset);
            }

            response.setCharacterEncoding(Charsets.UTF_8.name());
            contentType = Strings.useValueIfBlank(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setContentType(contentType);
            response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            response.setHeader(HttpHeaders.CONTENT_RANGE, StringTemplates.formatWithPlaceholder("bytes {}-{}/{}", offset, end, bytes.length));
            String disposition = StringTemplates.formatWithPlaceholder("attachment;filename={}", new String(fileName.getBytes(Charsets.UTF_8), Charsets.ISO_8859_1));
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, disposition);
            response.setHeader(HttpHeaders.CONTENT_LENGTH, "" + size);
            response.getOutputStream().write(bytes, (int) offset, size);
            response.setStatus(200);
        } catch (Throwable ex) {
            logger.error("Error occur when download file: {}", fileName);
            response.setStatus(500);
        }
    }

    public static void writeToResponse(@NonNull HttpServletResponse response, @Nullable String contentType, @NonNull String content) throws IOException {
        writeToResponseUsingWriter(response, contentType, content);
    }

    public static void writeToResponseUsingOutputStream(@NonNull HttpServletResponse response, @Nullable String contentType, @NonNull String content) throws IOException {
        response.setCharacterEncoding(Charsets.UTF_8.name());
        ServletOutputStream writer = response.getOutputStream();
        if (Emptys.isNotEmpty(contentType)) {
            response.setContentType(contentType);
        }
        writer.write(content.getBytes(Charsets.UTF_8));
    }

    public static void writeToResponseUsingWriter(@NonNull HttpServletResponse response, @Nullable String contentType, @NonNull String content) throws IOException {
        response.setCharacterEncoding(Charsets.UTF_8.name());
        PrintWriter writer = response.getWriter();
        if (Emptys.isNotEmpty(contentType)) {
            response.setContentType(contentType);
        }
        writer.write(content);
    }

    public static Cookie getCookie(HttpServletRequest request, final String name) {
        return Pipeline.of(request.getCookies())
                .findFirst(new Predicate<Cookie>() {
                    @Override
                    public boolean test(Cookie cookie) {
                        return name.equals(cookie.getName());
                    }
                });
    }

    public static String buildFullRequestUrl(HttpServletRequest r) {
        return buildFullRequestUrl(r.getScheme(), r.getServerName(), r.getServerPort(), r.getRequestURI(),
                r.getQueryString());
    }

    /**
     * Obtains the full URL the client used to make the request.
     * <p>
     * Note that the server port will not be shown if it is the default server port for
     * HTTP or HTTPS (80 and 443 respectively).
     *
     * @return the full URL, suitable for redirects (not decoded).
     */
    public static String buildFullRequestUrl(String scheme, String serverName, int serverPort, String requestURI,
                                             String queryString) {
        scheme = scheme.toLowerCase();
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        // Only add port if not default
        if ("http".equals(scheme)) {
            if (serverPort != 80) {
                url.append(":").append(serverPort);
            }
        } else if ("https".equals(scheme)) {
            if (serverPort != 443) {
                url.append(":").append(serverPort);
            }
        }
        // Use the requestURI as it is encoded (RFC 3986) and hence suitable for
        // redirects.
        url.append(requestURI);
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }

    public static String getParameter(HttpServletRequest request, String name) {
        return getParameter(request, name, null);
    }

    public static String getParameter(HttpServletRequest request, String name, String defaultValue) {
        return Objs.useValueIfNull(request.getParameter(name), defaultValue);
    }

    public static List<String> getParameters(HttpServletRequest request, String name) {
        return getParameters(request, name, null);
    }

    public static List<String> getParameters(HttpServletRequest request, String name, List<String> defaultValue) {
        final String[] values = request.getParameterValues(name);
        if (values != null) {
            return Collects.asList(values);
        }
        return defaultValue;
    }

    public static Integer getIntParameter(HttpServletRequest request, String name, Integer defaultValue) {
        String value = getParameter(request, name);
        Integer ret = null;
        if (!Strings.isBlank(value)) {
            ret = Integer.parseInt(value);
        } else {
            ret = defaultValue;
        }
        return ret;
    }


    public static List<Integer> getIntParameters(HttpServletRequest request, final String name, List<Integer> defaultValue) {
        final List<String> valueOpt = getParameters(request, name);
        List<Integer> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String, Integer>() {
                @Override
                public Integer apply(String input) {
                    return Integer.parseInt(input);
                }
            }).asList();
        } else {
            ret = defaultValue;
        }
        return ret;
    }

    public static Long getLongParameter(HttpServletRequest request, final String name, Long defaultValue) {
        final String valueOpt = getParameter(request, name);
        Long ret = null;
        if (!Strings.isBlank(valueOpt)) {
            ret = Long.parseLong(valueOpt);
        } else {
            ret = defaultValue;
        }
        return ret;
    }

    public static List<Long> getLongParameters(HttpServletRequest request, final String name, List<Long> defaultValue) {
        final List<String> valueOpt = getParameters(request, name);
        List<Long> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String, Long>() {
                @Override
                public Long apply(String input) {
                    return Long.parseLong(input);
                }
            }).asList();
        } else {
            ret = defaultValue;
        }
        return ret;
    }

    public static Float getFloatParameter(HttpServletRequest request, final String name, Float defaultValue) {
        final String valueOpt = getParameter(request, name);
        Float ret = null;
        if (!Strings.isBlank(valueOpt)) {
            ret = Float.parseFloat(valueOpt);
        } else {
            ret = defaultValue;
        }
        return ret;
    }

    public static List<Float> getFloatParameters(HttpServletRequest request, final String name, List<Float> defaultValue) {
        final List<String> valueOpt = getParameters(request, name);
        List<Float> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String, Float>() {
                @Override
                public Float apply(String input) {
                    return Float.parseFloat(input);
                }
            }).asList();
        } else {
            ret = defaultValue;
        }
        return ret;
    }

    public static Double getDoubleParameter(HttpServletRequest request, final String name, Double defaultValue) {
        final String valueOpt = getParameter(request, name);
        Double ret = null;
        if (!Strings.isBlank(valueOpt)) {
            ret = Double.parseDouble(valueOpt);
        } else {
            ret = defaultValue;
        }
        return ret;
    }

    public static List<Double> getDoubleParameters(HttpServletRequest request, final String name, List<Double> defaultValue) {
        final List<String> valueOpt = getParameters(request, name);
        List<Double> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String, Double>() {
                @Override
                public Double apply(String input) {
                    return Double.parseDouble(input);
                }
            }).asList();
        } else {
            ret = defaultValue;
        }
        return ret;
    }

    private static final List<String> TRUE_VALUES;

    static {
        TRUE_VALUES = Collects.newArrayList("true", "1", "on", "yes");
    }


    public static Boolean isTrue(final String value) {
        return Pipeline.of(TRUE_VALUES).anyMatch(new Predicate<String>() {
            @Override
            public boolean test(String x) {
                return x.equalsIgnoreCase(value);
            }
        });
    }

    public static Boolean getBooleanParameter(HttpServletRequest request, final String name) {
        return getBooleanParameter(request, name, null);
    }

    public static Boolean getBooleanParameter(HttpServletRequest request, final String name, final Boolean defaultValue) {
        final String valueOpt = getParameter(request, name);
        if (!Strings.isBlank(valueOpt)) {
            return isTrue(name);
        }
        return defaultValue;
    }

    public static List<Boolean> getBooleanParameters(HttpServletRequest request, final String name, List<Boolean> defaultValue) {
        final List<String> valueOpt = getParameters(request, name);
        List<Boolean> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String, Boolean>() {
                @Override
                public Boolean apply(String input) {
                    return isTrue(input);
                }
            }).asList();
        } else {
            ret = defaultValue;
        }
        return ret;
    }
}
