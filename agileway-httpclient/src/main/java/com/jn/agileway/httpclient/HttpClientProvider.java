package com.jn.agileway.httpclient;

import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.lifecycle.Lifecycle;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Numbers;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Supplier0;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpClientProvider implements Initializable, Lifecycle, Supplier0<HttpClient> {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientProvider.class);

    private PoolingHttpClientConnectionManager connectionManager;

    private CloseableHttpClient httpClient;

    private IdleConnectionMonitorThread monitorThread;

    private HttpClientProperties config;

    private final List<HttpClientCustomizer> customizers = Collects.emptyArrayList();
    private volatile boolean inited = false;

    private volatile boolean running = false;

    public CloseableHttpClient get() {
        if (!running) {
            return null;
        }
        return httpClient;
    }

    public HttpClientProperties getConfig() {
        return config;
    }

    public void setConfig(HttpClientProperties config) {
        if (config != null) {
            this.config = config;
        }
    }

    public void setCustomizers(List<HttpClientCustomizer> customizers) {
        if (Emptys.isNotEmpty(customizers)) {
            this.customizers.addAll(customizers);
        }
    }

    @Override
    public void init() throws InitializationException {
        if (inited) {
            return;
        }
        inited = true;
        Preconditions.checkNotNull(config, "the httpclient provider's config is null");
        logger.info("===[AGILE_WAY-HTTP_CLIENT_PROVIDER]=== Initial the aigleway http client provider");
        this.connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getPoolMaxConnections());
        connectionManager.setDefaultMaxPerRoute(config.getPoolMaxPerRoute());
        connectionManager.setValidateAfterInactivity(config.getPoolIdleConnValidateDelayInMills());

        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setSocketTimeout(config.getSocketTimeoutInMills())
                .setConnectionRequestTimeout(config.getConnectionRequestTimeoutInMills())
                .setConnectTimeout(config.getConnectTimeoutInMills())
                .setContentCompressionEnabled(config.isContentCompressionEnabled())
                .setAuthenticationEnabled(config.isAuthcEnabled());

        Pipeline.of(this.customizers).forEach(new Consumer<HttpClientCustomizer>() {
            @Override
            public void accept(HttpClientCustomizer httpClientCustomizer) {
                httpClientCustomizer.customizeHttpRequest(requestConfigBuilder);
            }
        });

        RequestConfig requestConfig = requestConfigBuilder.build();


        final HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new AgilewayRetryHandler(config.getMaxRetry()))
                .setKeepAliveStrategy(new AgilewayConnectionKeepAliveStrategy());

        Pipeline.of(this.customizers).forEach(new Consumer<HttpClientCustomizer>() {
            @Override
            public void accept(HttpClientCustomizer httpClientCustomizer) {
                httpClientCustomizer.customizeHttpClient(httpClientBuilder);
            }
        });
        httpClient = httpClientBuilder.build();

        monitorThread = new IdleConnectionMonitorThread(connectionManager);
        monitorThread.start();
    }

    @Override
    public void startup() {
        init();
    }

    @Override
    public void shutdown() {
        running = false;
        this.connectionManager.shutdown();
        monitorThread.shutdown();
    }

    private class IdleConnectionMonitorThread extends Thread {
        private final HttpClientConnectionManager connectionManager;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connectionManager) {
            super();
            this.connectionManager = connectionManager;
            setDaemon(true);
            setName("Agileway-HttpClient-IdleConnectionMonitor");
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(config.getIdleConnectionCleanupIntervalInMills());
                        this.connectionManager.closeExpiredConnections();
                        this.connectionManager.closeIdleConnections(config.getIdleConnectionTimeoutInMills(), TimeUnit.MILLISECONDS);
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

    private class AgilewayConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
        @Override
        public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
            HeaderElementIterator it = new BasicHeaderElementIterator(httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement element = it.nextElement();
                String param = element.getName();
                String value = element.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    try {
                        return Numbers.createLong(value) * 1000;
                    } catch (NumberFormatException ex) {
                        // ignore it
                    }
                }
            }
            return config.getKeepAliveTimeoutInMills();
        }
    }

    private class AgilewayRetryHandler extends DefaultHttpRequestRetryHandler {
        private int retry;

        public AgilewayRetryHandler(int retry) {
            this.retry = retry;
        }

        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (executionCount > retry) {
                logger.warn("Maximum tries reached for http client pool");
                return false;
            }

            if (exception instanceof NoHttpResponseException
                    || exception instanceof ConnectTimeoutException
                    || exception instanceof SocketTimeoutException) {
                logger.warn("NoHttpResponseException on {} invoked", executionCount);
                return true;
            }

            try {
                connectionManager.closeExpiredConnections();
                connectionManager.closeIdleConnections(config.getIdleConnectionTimeoutInMills(), TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                // ignore it
            }
            return super.retryRequest(exception, executionCount, context);
        }
    }

}
