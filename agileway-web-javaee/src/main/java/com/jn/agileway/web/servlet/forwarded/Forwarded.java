package com.jn.agileway.web.servlet.forwarded;


import com.jn.langx.util.Strings;
import com.jn.langx.util.net.HostAndPort;

/**
 * 表示 HTTP 请求转发信息（适用于代理或后端服务场景）
 * 使用场景：
 * 1. 代理模式（如 Nginx 转发请求）
 * 2. 后端服务模式（如直接由 UserManager 处理请求）
 */
public class Forwarded {
    /**
     * WebServer 使用的协议（如 http/https）
     */
    private String proto;

    /**
     * 请求发送到 WebServer 时的 Host 头内容
     * 示例：通过代理访问时可能是 "api.example.com"
     */
    private HostAndPort host;

    /**
     * WebServer 接收到的请求来源（客户端信息）
     * 示例：客户端 IP 和端口 "192.168.1.100:12345"
     */
    private HostAndPort client;

    /**
     * WebServer 自身的代理信息（当存在多层代理时使用）
     */
    private HostAndPort proxy;

    /**
     * 接收请求时的上下文路径（未指定时为 null）
     * 示例："/api/v2"
     */
    private String contextPath;

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    public HostAndPort getHost() {
        return host;
    }

    public void setHost(HostAndPort host) {
        this.host = host;
    }

    public HostAndPort getClient() {
        return client;
    }

    public void setClient(HostAndPort client) {
        this.client = client;
    }

    public HostAndPort getProxy() {
        return proxy;
    }

    public void setProxy(HostAndPort proxy) {
        this.proxy = proxy;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = Strings.trim(contextPath);
    }

    public boolean isValid() {
        return host != null && host.isValid();
    }

}