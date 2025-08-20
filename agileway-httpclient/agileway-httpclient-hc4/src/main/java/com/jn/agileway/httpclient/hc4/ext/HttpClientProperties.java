package com.jn.agileway.httpclient.hc4.ext;

public class HttpClientProperties {

    /**
     * pool configs
     */
    private int poolMaxConnections = 100;
    private int poolMaxPerRoute = 100;
    private int poolIdleConnValidateDelayInMills = 5 * 1000;
    private int poolMaxIdleConnections = 3;

    /**
     * connect and request configs
     */
    private int socketTimeoutInMills = 60 * 1000;
    private int connectTimeoutInMills = 5 * 1000;
    private int connectionRequestTimeoutInMills = 60 * 1000;
    private boolean authcEnabled = false;
    private boolean contentCompressionEnabled = false;


    /**
     * expire
     */
    private int idleConnectionTimeoutInMills;
    private int idleConnectionCleanupIntervalInMills;

    private int maxRetry = 3;

    private int keepAliveTimeoutInMills = 15 * 1000;


    public int getPoolMaxConnections() {
        return poolMaxConnections;
    }

    public void setPoolMaxConnections(int poolMaxConnections) {
        if (poolMaxConnections > 0) {
            this.poolMaxConnections = poolMaxConnections;
        }
    }

    public int getPoolMaxPerRoute() {
        return poolMaxPerRoute;
    }

    public void setPoolMaxPerRoute(int poolMaxPerRoute) {
        if (poolMaxPerRoute > 0) {
            this.poolMaxPerRoute = poolMaxPerRoute;
        }
    }

    public int getPoolIdleConnValidateDelayInMills() {
        return poolIdleConnValidateDelayInMills;
    }

    public void setPoolIdleConnValidateDelayInMills(int poolIdleConnValidateDelayInMills) {
        if (poolIdleConnValidateDelayInMills > 0) {
            this.poolIdleConnValidateDelayInMills = poolIdleConnValidateDelayInMills;
        }
    }

    public int getPoolMaxIdleConnections() {
        return poolMaxIdleConnections;
    }

    public void setPoolMaxIdleConnections(int poolMaxIdleConnections) {
        this.poolMaxIdleConnections = poolMaxIdleConnections;
    }

    public int getSocketTimeoutInMills() {
        return socketTimeoutInMills;
    }

    public void setSocketTimeoutInMills(int socketTimeoutInMills) {
        if (socketTimeoutInMills > 0) {
            this.socketTimeoutInMills = socketTimeoutInMills;
        }
    }

    public int getConnectTimeoutInMills() {
        return connectTimeoutInMills;
    }

    public void setConnectTimeoutInMills(int connectTimeoutInMills) {
        if (connectTimeoutInMills > 0) {
            this.connectTimeoutInMills = connectTimeoutInMills;
        }
    }

    public int getConnectionRequestTimeoutInMills() {
        return connectionRequestTimeoutInMills;
    }

    public void setConnectionRequestTimeoutInMills(int connectionRequestTimeoutInMills) {
        if (connectionRequestTimeoutInMills > 0) {
            this.connectionRequestTimeoutInMills = connectionRequestTimeoutInMills;
        }
    }

    public boolean isAuthcEnabled() {
        return authcEnabled;
    }

    public void setAuthcEnabled(boolean authcEnabled) {
        this.authcEnabled = authcEnabled;
    }

    public boolean isContentCompressionEnabled() {
        return contentCompressionEnabled;
    }

    public void setContentCompressionEnabled(boolean contentCompressionEnabled) {
        this.contentCompressionEnabled = contentCompressionEnabled;
    }

    public int getIdleConnectionTimeoutInMills() {
        return idleConnectionTimeoutInMills;
    }

    public void setIdleConnectionTimeoutInMills(int idleConnectionTimeoutInMills) {
        if (idleConnectionTimeoutInMills > 0) {
            this.idleConnectionTimeoutInMills = idleConnectionTimeoutInMills;
        }
    }

    public int getIdleConnectionCleanupIntervalInMills() {
        return idleConnectionCleanupIntervalInMills;
    }

    public void setIdleConnectionCleanupIntervalInMills(int idleConnectionCleanupIntervalInMills) {
        if (idleConnectionCleanupIntervalInMills > 0) {
            this.idleConnectionCleanupIntervalInMills = idleConnectionCleanupIntervalInMills;
        }
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public int getKeepAliveTimeoutInMills() {
        return keepAliveTimeoutInMills;
    }

    public void setKeepAliveTimeoutInMills(int keepAliveTimeoutInMills) {
        if (keepAliveTimeoutInMills > 0) {
            this.keepAliveTimeoutInMills = keepAliveTimeoutInMills;
        }
    }

    @Override
    public String toString() {
        return "HttpClientProperties{" +
                "poolMaxConnections=" + poolMaxConnections +
                ", poolMaxPerRoute=" + poolMaxPerRoute +
                ", poolIdleConnValidateDelayInMills=" + poolIdleConnValidateDelayInMills +
                ", socketTimeoutInMills=" + socketTimeoutInMills +
                ", connectTimeoutInMills=" + connectTimeoutInMills +
                ", connectionRequestTimeoutInMills=" + connectionRequestTimeoutInMills +
                ", authcEnabled=" + authcEnabled +
                ", contentCompressionEnabled=" + contentCompressionEnabled +
                ", idleConnectionTimeoutInMills=" + idleConnectionTimeoutInMills +
                ", idleConnectionCleanupIntervalInMills=" + idleConnectionCleanupIntervalInMills +
                ", maxRetry=" + maxRetry +
                ", keepAliveTimeoutInMills=" + keepAliveTimeoutInMills +
                '}';
    }
}
