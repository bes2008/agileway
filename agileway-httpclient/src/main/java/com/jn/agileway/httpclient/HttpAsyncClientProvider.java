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
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;

public class HttpAsyncClientProvider implements Initializable, Lifecycle, Supplier0<HttpAsyncClient> {
    private static final Logger logger = Loggers.getLogger(HttpClientProvider.class);

    private CloseableHttpAsyncClient httpClient;


    private HttpClientProperties config;

    private final List<HttpAsyncClientCustomizer> customizers = Collects.emptyArrayList();
    private volatile boolean inited = false;

    private volatile boolean running = false;

    public HttpAsyncClient get() {
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

    public void setCustomizers(List<HttpAsyncClientCustomizer> customizers) {
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
        logger.info("===[AGILE_WAY-HTTP_CLIENT_PROVIDER]=== Initial the AGILEWAY http client provider");

        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setSocketTimeout(config.getSocketTimeoutInMills())
                .setConnectionRequestTimeout(config.getConnectionRequestTimeoutInMills())
                .setConnectTimeout(config.getConnectTimeoutInMills())
                .setAuthenticationEnabled(config.isAuthcEnabled());

        if (config.isContentCompressionEnabled()) {
            Method method = Reflects.getPublicMethod(RequestConfig.Builder.class, "setContentCompressionEnabled");
            Reflects.invoke(method, requestConfigBuilder, new Object[]{true}, true, true);
        }

        Pipeline.of(this.customizers).forEach(new Consumer<HttpAsyncClientCustomizer>() {
            @Override
            public void accept(HttpAsyncClientCustomizer httpClientCustomizer) {
                httpClientCustomizer.customizeHttpRequest(requestConfigBuilder);
            }
        });

        RequestConfig requestConfig = requestConfigBuilder.build();

        final HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(new AgilewayConnectionKeepAliveStrategy())
                .setMaxConnPerRoute(config.getPoolMaxPerRoute())
                .setMaxConnTotal(config.getPoolMaxConnections())
                .setDefaultCookieSpecRegistry(CookieSpecs.createDefaultCookieSpecProviderBuilder().build());

        Pipeline.of(this.customizers).forEach(new Consumer<HttpAsyncClientCustomizer>() {
            @Override
            public void accept(HttpAsyncClientCustomizer httpClientCustomizer) {
                httpClientCustomizer.customizeAsyncHttpClient(httpClientBuilder);
            }
        });
        httpClient = httpClientBuilder.build();
        running = true;
    }

    @Override
    public void startup() {
        init();
    }

    @Override
    public void shutdown() {
        running = false;
        if (httpClient != null) {
            IOs.close(this.httpClient);
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

    private static class AgilewayRetryHandler extends DefaultHttpRequestRetryHandler {
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

            return super.retryRequest(exception, executionCount, context);
        }
    }
}
