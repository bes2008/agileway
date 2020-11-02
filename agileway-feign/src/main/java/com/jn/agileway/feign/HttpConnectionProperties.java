package com.jn.agileway.feign;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Maths;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;

import java.util.List;

public class HttpConnectionProperties {
    public static final String ACCESS_LOG_NAME = "access.log";
    private static final int DEFAULT_PORT = 6666;

    private String accessLoggerName = ACCESS_LOG_NAME;

    private static final List<String> SUPPORTED_PROTOCOLS = Collects.newArrayList("http", "https");
    private String protocol = "http";

    private int defaultPort = DEFAULT_PORT;
    private String pathPrefix = "";

    /**
     * 可取值： FULL, BASIC, HEADERS, NONE
     * <pre>
     *     NONE: 不记录访问日志
     *     BASIC： 只记录请求的： url, method, 响应的：status code, response time
     *     HEADERS: 在basic的基础上添加了 请求头、响应头
     *     FULL: 在HEADERS基础上加了 querystring, request body,  response body
     * </pre>
     */
    private String accessLogLevel = "HEADERS";

    /**
     * 当 node中的节点数大于1时，会使用该值
     */
    private String loadbalancerHost = "DEFAULT";

    /**
     * 注册中心启动后， url就是注册中心的地址
     * 注册中心未启动， url就是单机 license server 的地址
     */
    private String nodes;
    /**
     * http 请求重试
     * 0： 不启用重试
     * >0 : 总的次数
     * <0: 一直重试
     */
    private int maxRetry;

    /**
     * 重试间隔，单位：s
     * 当启用重试时（即 maxRetry !=0 时），最小值为1
     */
    private int retryInterval;

    public boolean retryEnabled() {
        return maxRetry != 0;
    }

    public int getMaxRetry() {
        return Maths.max(retryInterval, 1);
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public int getRetryInterval() {
        return Maths.max(retryInterval, 1);
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getAccessLogLevel() {
        return accessLogLevel;
    }

    public void setAccessLogLevel(String accessLogLevel) {
        this.accessLogLevel = accessLogLevel;
    }

    public int getDefaultPort() {
        if (defaultPort <= 0) {
            defaultPort = DEFAULT_PORT;
        }
        return defaultPort;
    }

    public void setDefaultPort(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public String getLoadbalancerHost() {
        return Strings.useValueIfBlank(loadbalancerHost, "DEFAULT");
    }

    public void setLoadbalancerHost(String loadbalancerHost) {
        this.loadbalancerHost = loadbalancerHost;
    }

    public String getAccessLoggerName() {
        return Strings.useValueIfBlank(accessLoggerName, ACCESS_LOG_NAME);
    }

    public void setAccessLoggerName(String accessLoggerName) {
        this.accessLoggerName = accessLoggerName;
    }

    public String getPathPrefix() {
        return Strings.useValueIfBlank(pathPrefix, "");
    }

    public void setPathPrefix(String pathPrefix) {
        if (pathPrefix != null) {
            while (pathPrefix.startsWith("/")) {
                pathPrefix = pathPrefix.substring(1);
            }
        }
        this.pathPrefix = pathPrefix;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        if (Emptys.isNotEmpty(protocol) && SUPPORTED_PROTOCOLS.contains(protocol)) {
            this.protocol = protocol;
        }
    }
}
