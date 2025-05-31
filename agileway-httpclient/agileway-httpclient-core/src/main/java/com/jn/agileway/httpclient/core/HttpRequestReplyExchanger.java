package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.channel.PipedInboundChannel;
import com.jn.agileway.eipchannel.core.channel.PipedOutboundChannel;
import com.jn.agileway.eipchannel.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eipchannel.core.channel.pipe.PipedDuplexChannel;
import com.jn.agileway.eipchannel.core.endpoint.exchange.RequestReplyExchanger;
import com.jn.agileway.eipchannel.core.transformer.TransformerInboundMessageInterceptor;
import com.jn.agileway.eipchannel.core.transformer.TransformerOutboundMessageInterceptor;
import com.jn.agileway.httpclient.core.error.DefaultHttpResponseErrorHandler;
import com.jn.agileway.httpclient.core.error.HttpResponseErrorHandler;
import com.jn.agileway.httpclient.core.error.exception.HttpRequestClientErrorException;
import com.jn.agileway.httpclient.core.interceptor.*;
import com.jn.agileway.httpclient.core.payload.*;
import com.jn.agileway.httpclient.core.plugin.*;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequestFactoryBuilder;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequestFactoryBuilderSupplier;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.plugin.PluginRegistry;
import com.jn.langx.plugin.SimpleLoadablePluginRegistry;
import com.jn.langx.plugin.SpiPluginLoader;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.concurrent.promise.AsyncCallback;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Handler;

import java.util.List;

public class HttpRequestReplyExchanger extends AbstractLifecycle implements RequestReplyExchanger<HttpRequest<?>, HttpResponse<?>> {

    /**
     * <pre>
     *  OutboundChannel                             InboundChannel
     *
     *
     *  HttpRequest&lt;JavaBean>                   HttpResponse&lt;JavaBean>
     *        |                                             ^
     *        v                                             |
     *  ---------------------                       ---------------------
     *  |   Interceptors    |                       |   Interceptors    |
     *  ---------------------                       ---------------------
     *        |                                             ^
     *        v                                             |
     *  HttpRequest&lt;ByteArrayOutputStream>      HttpResponse&lt;byte[]>
     *        |                                             ^
     *        v                                             |
     *  ---------------------                       ---------------------
     *  |   Interceptors    |                       |   Interceptors    |
     *  ---------------------                       ---------------------
     *        |                                             ^
     *        v                                             |
     *  UnderlyingHttpRequest                      UnderlyingHttpResponse
     *        |                                             ^
     *        v                                             |
     * ---------------------------------------------------------------------------
     *         |------------> Transport Level ------------>|
     * ---------------------------------------------------------------------------
     * </pre>
     */
    private PipedDuplexChannel requestReplyChannel;
    private HttpExchangerConfiguration configuration;

    @NonNull
    private UnderlyingHttpRequestFactory requestFactory;
    /**
     * 对请求进行拦截处理
     */
    private List<HttpRequestInterceptor> requestInterceptors = Lists.newArrayList();
    /**
     * 主要是将 body进行转换，顺带补充 header等，只要一个转换成功就可以。
     */
    private List<HttpRequestPayloadWriter> requestContentWriters = Lists.newArrayList();

    /**
     * 对正常响应进行反序列化
     */
    private List<HttpResponsePayloadReader> responsePayloadReaders = Lists.newArrayList();
    /**
     * 对4xx,5xx的响应进行处理
     */
    @Nullable
    private HttpResponseErrorHandler globalHttpResponseErrorHandler;
    /**
     * 响应拦截器
     */
    private List<HttpResponseInterceptor> responseInterceptors = Lists.newArrayList();

    private PluginRegistry httpMessagePluginRegistry;

    private InternalHttpRequestExecutor internalHttpRequestExecutor;

    @Override
    protected void doInit() throws InitializationException {
        if (configuration == null) {
            configuration = new HttpExchangerConfiguration();
        }

        // 初始化 各种协议 Protocol 插件
        if (this.httpMessagePluginRegistry == null) {
            SimpleLoadablePluginRegistry httpMessagePluginRegistry = new SimpleLoadablePluginRegistry();
            httpMessagePluginRegistry.setPluginLoader(new SpiPluginLoader());
            this.httpMessagePluginRegistry = httpMessagePluginRegistry;
        }
        List<HttpMessageProtocolPlugin> plugins = this.httpMessagePluginRegistry.find(HttpMessageProtocolPlugin.class);

        /**********************************************************************************************************
         *                          初始化 outbound channel
         **********************************************************************************************************/

        // 组织 requestInterceptors，并创建 PipedOutboundChannel
        for (HttpMessageProtocolPlugin plugin : plugins) {
            this.requestInterceptors.add(new PluginBasedHttpRequestInterceptor(plugin));
        }
        this.requestInterceptors.add(new HttpRequestMultiPartsFormInterceptor());
        this.requestInterceptors.add(new HttpRequestHeadersInterceptor(configuration.getFixedHeaders()));

        this.requestInterceptors.add(0, new HttpRequestUriInterceptor(configuration.getAllowedSchemes(), configuration.getAllowedAuthorities(), configuration.getNotAllowedAuthorities()));
        this.requestInterceptors.add(0, new HttpRequestMethodInterceptor(configuration.getAllowedMethods(), configuration.getNotAllowedMethods()));
        this.requestInterceptors = Lists.immutableList(requestInterceptors);

        ChannelMessageInterceptorPipeline outboundChannelPipeline = new ChannelMessageInterceptorPipeline();
        for (HttpRequestInterceptor requestInterceptor : this.requestInterceptors) {
            outboundChannelPipeline.addLast(new HttpRequestInterceptorAdapter(requestInterceptor));
        }

        // 组织 requestBodyWriters ，并转换为 transformer, 作为 TransformerOutboundInterceptor 添加到拦截器链中
        for (HttpMessageProtocolPlugin plugin : plugins) {
            this.requestContentWriters.add(new PluginBasedHttpRequestWriter(plugin));
        }
        this.requestContentWriters.add(new GeneralFormHttpRequestWriter());
        this.requestContentWriters.add(new GeneralMultiPartsFormHttpRequestWriter());
        HttpRequestPayloadTransformer requestTransformer = new HttpRequestPayloadTransformer(this.requestContentWriters);
        TransformerOutboundMessageInterceptor transformerOutboundMessageInterceptor = new TransformerOutboundMessageInterceptor();
        transformerOutboundMessageInterceptor.setTransformer(requestTransformer);
        outboundChannelPipeline.addLast(transformerOutboundMessageInterceptor);
        // request logging
        outboundChannelPipeline.addLast(new HttpRequestInterceptorAdapter(new HttpRequestLoggingInterceptor()));

        PipedOutboundChannel outboundChannel = new PipedOutboundChannel();
        outboundChannel.setPipeline(outboundChannelPipeline);

        /**********************************************************************************************************
         *                          初始化 inbound channel
         **********************************************************************************************************/

        ChannelMessageInterceptorPipeline inboundChannelPipeline = new ChannelMessageInterceptorPipeline();

        // response logging
        inboundChannelPipeline.addFirst(new HttpResponseInterceptorAdapter(new HttpResponseLoggingInterceptor()));

        // 组织 requestBodyWriters ，并转换为 transformer, 作为 TransformerOutboundInterceptor 添加到拦截器链中
        for (HttpMessageProtocolPlugin plugin : plugins) {
            responsePayloadReaders.add(new PluginBasedHttpResponseReader(plugin));
        }
        this.responsePayloadReaders.add(new GeneralAttachmentReader());
        this.responsePayloadReaders.add(new GeneralTextHttpResponseReader());
        this.responsePayloadReaders.add(new GeneralResourceHttpResponseReader());

        HttpResponsePayloadTransformer responsePayloadTransformer = new HttpResponsePayloadTransformer(responsePayloadReaders);
        TransformerInboundMessageInterceptor transformerInboundMessageInterceptor = new TransformerInboundMessageInterceptor();
        transformerInboundMessageInterceptor.setTransformer(responsePayloadTransformer);
        inboundChannelPipeline.addLast(transformerInboundMessageInterceptor);

        // 自定义的 responseInterceptors
        for (HttpMessageProtocolPlugin plugin : plugins) {
            this.responseInterceptors.add(new PluginBasedHttpResponseInterceptor(plugin));
        }
        for (HttpResponseInterceptor responseInterceptor : this.responseInterceptors) {
            inboundChannelPipeline.addLast(new HttpResponseInterceptorAdapter(responseInterceptor));
        }

        PipedInboundChannel inboundChannel = new PipedInboundChannel();
        inboundChannel.setPipeline(inboundChannelPipeline);


        /**********************************************************************************************************
         *                          初始化 http response error handler
         **********************************************************************************************************/
        // httpResponseErrorHandler
        if (this.globalHttpResponseErrorHandler == null) {
            this.globalHttpResponseErrorHandler = new DefaultHttpResponseErrorHandler();
        }

        /**********************************************************************************************************
         *                          初始化 UnderlyingHttpRequestFactory
         **********************************************************************************************************/
        if (this.requestFactory == null) {
            UnderlyingHttpRequestFactoryBuilder requestFactoryBuilder = UnderlyingHttpRequestFactoryBuilderSupplier.getInstance().get();
            requestFactoryBuilder.connectTimeoutMills(configuration.getConnectTimeoutMillis());
            requestFactoryBuilder.readTimeoutMills(configuration.getConnectTimeoutMillis());
            requestFactoryBuilder.keepAliveDurationMills(configuration.getKeepAliveDurationMills());
            requestFactoryBuilder.hostnameVerifier(configuration.getHostnameVerifier());
            requestFactoryBuilder.sslContextBuilder(configuration.getSslContextBuilder());
            requestFactoryBuilder.poolMaxIdleConnections(configuration.getPoolMaxIdleConnections());
            requestFactoryBuilder.executor(configuration.getExecutor());
            this.requestFactory = requestFactoryBuilder.build();
        }

        /**********************************************************************************************************
         *                          初始化 InternalHttpRequestExecutor
         **********************************************************************************************************/
        InternalHttpRequestExecutor internalHttpRequestExecutor = new InternalHttpRequestExecutor();
        internalHttpRequestExecutor.setRequestFactory(requestFactory);
        this.internalHttpRequestExecutor = internalHttpRequestExecutor;

        /**********************************************************************************************************
         *                          初始化 requestReplyChannel
         **********************************************************************************************************/
        outboundChannel.setSinker(internalHttpRequestExecutor);
        inboundChannel.setInboundMessageSource(internalHttpRequestExecutor);
        this.requestReplyChannel = new PipedDuplexChannel(outboundChannel, inboundChannel);
    }

    @Override
    public Promise<HttpResponse<?>> exchangeAsync(HttpRequest<?> request) {
        return exchangeInternal(true, request);
    }

    @Override
    public HttpResponse<?> exchange(HttpRequest<?> request) {
        return exchangeInternal(false, request).await();
    }

    private Promise<HttpResponse<?>> exchangeInternal(boolean async, HttpRequest<?> request) {

        Task<HttpRequest<?>> sendRequestTask = new Task<HttpRequest<?>>() {
            @Override
            public HttpRequest<?> run(Handler<HttpRequest<?>> handler, ErrorHandler errorHandler) {
                boolean sent = requestReplyChannel.send(request);
                if (sent) {
                    throw new HttpRequestClientErrorException(request.getMethod(), request.getUri(), 408, "request is sent failed");
                }
                return request;
            }
        };


        return (async ? new Promise<HttpRequest<?>>(this.configuration.getExecutor(), sendRequestTask) : new Promise<HttpRequest<?>>(sendRequestTask))
                .then(new AsyncCallback<HttpRequest<?>, HttpResponse<?>>() {
                    @Override
                    public HttpResponse<?> apply(HttpRequest<?> request) {
                        internalHttpRequestExecutor.setRequestMessage(request);
                        HttpResponse<?> response = (HttpResponse<?>) requestReplyChannel.poll();
                        if (response.hasError()) {
                            globalHttpResponseErrorHandler.handle(response);
                        }
                        return response;
                    }
                });

    }

    public void setConfiguration(HttpExchangerConfiguration configuration) {
        if (!inited) {
            this.configuration = configuration;
        }
    }

    public void setRequestFactory(UnderlyingHttpRequestFactory requestFactory) {
        if (!inited) {
            return;
        }
        if (requestFactory != null) {
            this.requestFactory = requestFactory;
        }
    }

    public void addRequestInterceptor(HttpRequestInterceptor interceptor) {
        if (!inited) {
            return;
        }
        if (interceptor != null) {
            this.requestInterceptors.add(interceptor);
        }
    }

    public void addResponseInterceptor(HttpResponseInterceptor interceptor) {
        if (!inited) {
            return;
        }
        if (interceptor != null) {
            this.responseInterceptors.add(interceptor);
        }
    }

    public void addRequestContentWriter(HttpRequestPayloadWriter writer) {
        if (!inited) {
            return;
        }
        if (writer != null) {
            this.requestContentWriters.add(writer);
        }
    }

    public void addResponsePayloadReader(HttpResponsePayloadReader reader) {
        if (!inited) {
            return;
        }
        if (reader != null) {
            this.responsePayloadReaders.add(reader);
        }
    }

    public void setHttpMessagePluginRegistry(PluginRegistry httpMessagePluginRegistry) {
        if (!inited) {
            return;
        }
        if (httpMessagePluginRegistry != null) {
            this.httpMessagePluginRegistry = httpMessagePluginRegistry;
        }
    }
}
