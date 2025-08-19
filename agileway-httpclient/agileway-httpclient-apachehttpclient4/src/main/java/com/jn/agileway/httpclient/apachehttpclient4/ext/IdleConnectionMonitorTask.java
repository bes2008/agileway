package com.jn.agileway.httpclient.apachehttpclient4.ext;

import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class IdleConnectionMonitorTask implements Runnable {
    private HttpClientConnectionManager connectionManager;
    private volatile boolean shutdown;
    private int idleConnectionTimeoutInMills;
    private int idleConnectionCleanupIntervalInMills;

    public IdleConnectionMonitorTask(HttpClientConnectionManager connectionManager, int idleConnectionTimeoutInMills, int idleConnectionCleanupIntervalInMills) {
        super();
        this.connectionManager = connectionManager;
        this.idleConnectionTimeoutInMills = idleConnectionTimeoutInMills;
        this.idleConnectionCleanupIntervalInMills = idleConnectionCleanupIntervalInMills;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(idleConnectionCleanupIntervalInMills);
                    this.connectionManager.closeExpiredConnections();
                    this.connectionManager.closeIdleConnections(idleConnectionTimeoutInMills, TimeUnit.MILLISECONDS);
                }
            }
        } catch (InterruptedException ex) {
            //
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}