package com.jn.agileway.web.servlet.forwarded;


import com.jn.langx.Parser;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.HostAndPort;
import com.jn.langx.util.regexp.Regexp;
import com.jn.langx.util.regexp.RegexpMatcher;
import com.jn.langx.util.regexp.Regexps;
import com.jn.langx.util.struct.Holder;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;


public class OriginalRequestParser implements Parser<HttpServletRequest, OriginalRequest> {
    private static final Logger logger = Loggers.getLogger(OriginalRequestParser.class);

    // 正则表达式模板（示例格式："host=\"example.com\""）
    private static final String FORWARDED_VALUE = "\"?([^;,\"]+)\"?";

    // 匹配 host=value 的模式
    private static final Regexp FORWARDED_HOST_PATTERN = Regexps.compile("(?i:host)=" + FORWARDED_VALUE);

    // 匹配 proto=value 的模式
    private static final Regexp FORWARDED_PROTO_PATTERN = Regexps.compile("(?i:proto)=" + FORWARDED_VALUE);

    // 匹配 for=value 的模式（客户端信息）
    private static final Regexp FORWARDED_FOR_PATTERN = Regexps.compile("(?i:for)=" + FORWARDED_VALUE);

    private final boolean parseForwarded;
    private final boolean parseForwardedFor;

    // 构造函数重载
    public OriginalRequestParser() {
        this(true);
    }

    public OriginalRequestParser(boolean parseForward) {
        this(parseForward, false);
    }

    public OriginalRequestParser(boolean parseForward, boolean parseClient) {
        this.parseForwarded = parseForward;
        this.parseForwardedFor = parseClient;
    }

    /**
     * 解析请求并构建 OriginalRequest 对象
     */
    public OriginalRequest parse(HttpServletRequest request) {
        Forwarded firstForward = parseForwarded ? parseFirstForward(request) : null;
        Forwarded lastForward = parseLastForward(request);
        return new OriginalRequest(firstForward, lastForward);
    }

    /**
     * 解析代理链中的第一个转发信息（需补充具体解析逻辑）
     */
    private Forwarded parseFirstForward(HttpServletRequest request) {
        Forwarded forwarded = new Forwarded();
        String forwardedHeader = request.getHeader("Forwarded");
        try {
            if (Strings.isNotBlank(forwardedHeader)) {
                // 1. 解析协议（proto）
                RegexpMatcher matcher = FORWARDED_PROTO_PATTERN.matcher(forwardedHeader);
                if (matcher.find()) {
                    String protocol = matcher.group(1).trim();
                    forwarded.setProto(protocol);
                }

                // 2. 解析主机（host）
                matcher = FORWARDED_HOST_PATTERN.matcher(forwardedHeader);
                if (matcher.find()) {
                    HostAndPort host = HostAndPort.of(matcher.group(1).trim());
                    forwarded.setHost(host);
                }

                // 3. 解析 client
                if (parseForwardedFor) {
                    matcher = FORWARDED_FOR_PATTERN.matcher(forwardedHeader);
                    if (matcher.find()) {
                        HostAndPort host = HostAndPort.of(matcher.group(1).trim());
                        forwarded.setHost(host);
                    }
                }
            } else {
                HostAndPort host = null;
                String firstHost = splitAndGetFirstHeader(request, ",", "X-Forwarded-Host", "X-Forwarded-Server");
                if (Strings.isNotBlank(firstHost)) {
                    host = HostAndPort.of(firstHost);
                }

                String firstPortString = splitAndGetFirstHeader(request, ",", "X-Forwarded-Port");
                if (Strings.isNotBlank(firstPortString)) {
                    int port = Integer.parseInt(firstPortString);
                    if (host != null) {
                        host.setValue(port);
                    }
                }
                forwarded.setHost(host);

                if (parseForwardedFor) {
                    String firstClient = splitAndGetFirstHeader(request, ",", "X-Forwarded-For", "X-Real-Ip");
                    if (Strings.isNotBlank(firstClient)) {
                        HostAndPort client = HostAndPort.of(firstClient);
                        forwarded.setClient(client);
                    }
                }
            }
        } catch (NumberFormatException e) {
            logger.warn("invalid http request header Forwarded or X-Forwarded-*, port is not an integer", e);
        } catch (Exception e) {
            logger.warn("invalid http request header: Forwarded or X-Forwarded-*", e);
        }

        HostAndPort host = forwarded.getHost();
        int firstPort = host == null ? -1 : host.getValue();
        String protocol = forwarded.getProto();
        if (Strings.isNotBlank(protocol)) {
            protocol = splitAndGetFirstValue(protocol, ",");
        }
        if (Strings.isBlank(protocol)) {
            protocol = splitAndGetFirstHeader(request, ",",
                    "X-Forwarded-Proto", "X-Forwarded-Protocol", "X-Url-Scheme", "X-Forwarded-Ssl", "Front-End-Https");
        }
        if (Strings.isNotBlank(protocol)) {
            protocol = Strings.lowerCase(protocol);
            if ("on".equals(protocol)) {
                protocol = "https";
            }
        } else {
            if (firstPort == 443) {
                protocol = "https";
            }
        }
        forwarded.setProto(protocol);

        forwarded.setContextPath(splitAndGetFirstHeader(request, ",", "X-Forwarded-ContextPath", "X-Forwarded-Context", "X-Forwarded-Path", "X-Forwarded-Prefix"));
        return forwarded;
    }

    /**
     * 解析应用服务器接收的请求信息
     */
    private Forwarded parseLastForward(HttpServletRequest request) {
        Forwarded forwarded = new Forwarded();
        forwarded.setProto(request.getScheme());
        forwarded.setHost(new HostAndPort(request.getServerName(), request.getServerPort()));
        forwarded.setContextPath(request.getContextPath());
        forwarded.setClient(new HostAndPort(request.getRemoteHost(), request.getRemotePort()));
        return forwarded;
    }

    /**
     * 从请求头中获取指定名称的第一个有效值（支持多值分割）
     *
     * @param request     HTTP 请求对象
     * @param headerNames 请求头名称
     * @param separator   多值分隔符（如逗号）
     * @return 第一个非空的有效值，若不存在则返回 null
     */
    public static String splitAndGetFirstHeader(HttpServletRequest request, String separator, String... headerNames) {
        final Holder<String> result = new Holder<>();
        Pipeline.<String>of(headerNames)
                .forEach(new Consumer<String>() {
                    public void accept(String headerName) {
                        String headerValue = request.getHeader(headerName);
                        result.set(splitAndGetFirstValue(headerValue, separator));
                    }
                }, new Predicate<String>() {
                    public boolean test(String headerName) {
                        return Strings.isNotBlank(result.get());
                    }
                });

        return result.get();
    }

    /**
     * 分割字符串并返回第一个有效值
     *
     * @param str       需要分割的字符串
     * @param separator 分隔符（如逗号）
     * @return 第一个非空的有效值，若不存在则返回 null
     */
    public static String splitAndGetFirstValue(String str, String separator) {
        if (Strings.isBlank(str)) {
            return null;
        }

        // 使用完整分隔符进行分割（避免部分匹配）
        String[] values = Strings.split(str, separator);
        if (Objs.isEmpty(values)) {
            return null;
        }

        return Pipeline.of(values).findFirst(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return Strings.isNotBlank(s);
            }
        });
    }
}