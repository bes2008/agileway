package com.jn.agileway.http.utils.forwarded;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.HostAndPort;

public class OriginalRequest {
    @Nullable
    private final Forwarded first;
    @NonNull
    private final Forwarded last;

    public OriginalRequest(Forwarded first, @NonNull Forwarded last) {
        this.first = first;
        this.last = Preconditions.checkNotNull(last, "last is null");
    }

    /**
     * 判断请求是否被转发（要求 first 有效）
     */
    public boolean isForwardedRequest() {
        return first != null && first.isValid();
    }

    /**
     * 获取上下文路径（如 "/app"）
     */
    public String getContextPath() {
        return isForwardedRequest() ? first.getContextPath() : last.getContextPath();
    }

    /**
     * 获取请求的主机信息（如 "example.com:8080"）
     */
    public HostAndPort getRequestHost() {
        return isForwardedRequest() ? first.getHost() : last.getHost();
    }

    /**
     * 获取请求协议（如 "https"）
     */
    public String getRequestScheme() {
        String scheme = null;
        if (isForwardedRequest()) {
            scheme = first.getProto();
            if (Strings.isBlank(scheme)) {
                scheme = last.getProto();
            }
        } else {
            scheme = last.getProto();
        }
        return scheme;
    }

    public String getRequestBaseUrl() {
        return getRequestBaseUrl(false);
    }

    /**
     * 构建基础 URL（格式：`协议://主机[:端口][/上下文路径]`）
     */
    public String getRequestBaseUrl(boolean useLastContextIfAbsent) {
        String scheme = getRequestScheme();
        HostAndPort host = getRequestHost();
        String contextPath = getContextPath();

        if (Strings.isBlank(contextPath) && isForwardedRequest()) {
            contextPath = useLastContextIfAbsent ? last.getContextPath() : contextPath;
        }

        if (Strings.isNotBlank(contextPath) && !Strings.startsWith(contextPath, "/")) {
            contextPath = "/" + contextPath;
        }
        String hostname = host.getKey();
        int port = host.getValue();
        if (isDefaultPort(scheme, port)) {
            port = -1;
        }
        String hostString = hostname + (port <= 0 ? "" : (":" + port));
        return scheme + "://" + hostString + contextPath;
    }


    /**
     * 判断是否为协议默认端口
     */
    private boolean isDefaultPort(String scheme, int port) {
        return ("http".equalsIgnoreCase(scheme) && port == 80) ||
                ("https".equalsIgnoreCase(scheme) && port == 443);
    }
}