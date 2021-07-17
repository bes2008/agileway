package com.jn.agileway.feign;

import com.jn.agileway.feign.loadbalancer.DynamicLBClientFactory;
import com.jn.agileway.feign.supports.adaptable.AdaptableDecoder;
import com.jn.agileway.feign.supports.adaptable.ResponseBodyAdapter;
import com.jn.agileway.feign.supports.rpc.RpcInvocationHandlerFactory;
import com.jn.langx.Nameable;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.annotation.Prototype;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.net.NetworkAddress;
import com.jn.langx.util.reflect.Reflects;
import feign.Client;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import feign.ribbon.LBClientFactory;
import feign.ribbon.RibbonClient;
import feign.slf4j.Slf4jLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleStubProvider implements Initializable, StubProvider, Nameable {
    private static final Logger logger = LoggerFactory.getLogger(SimpleStubProvider.class);
    @NonNull
    protected Feign.Builder apiBuilder;
    @NonNull
    private HttpConnectionContext context;
    private volatile boolean initialed = false;
    private ConcurrentHashMap<Class, Object> serviceMap = new ConcurrentHashMap<Class, Object>();
    @NonNull
    private Encoder encoder;
    @NonNull
    private Decoder decoder;
    @Nullable
    private ResponseBodyAdapter responseBodyAdapter;
    @NonNull
    private ErrorDecoder errorDecoder;
    @NonNull
    private ErrorHandler errorHandler;
    @NonNull
    private String name;

    private List<RequestInterceptor> requestInterceptors = Collects.emptyArrayList();

    private StubProviderCustomizer customizer;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setCustomizer(StubProviderCustomizer customizer) {
        this.customizer = customizer;
    }

    public void setContext(HttpConnectionContext context) {
        this.context = context;
    }

    public void setResponseBodyAdapter(ResponseBodyAdapter responseBodyAdapter) {
        this.responseBodyAdapter = responseBodyAdapter;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    public void setErrorDecoder(ErrorDecoder errorDecoder) {
        this.errorDecoder = errorDecoder;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void addRequestInterceptor(RequestInterceptor requestInterceptor) {
        if (requestInterceptor != null) {
            this.requestInterceptors.add(requestInterceptor);
        }
    }

    public Encoder getEncoder() {
        return encoder;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public ResponseBodyAdapter getResponseBodyAdapter() {
        return responseBodyAdapter;
    }

    public ErrorDecoder getErrorDecoder() {
        return errorDecoder;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void init() throws InitializationException {
        if (!initialed) {
            initialed = true;
            apiBuilder = createApiBuilder();
        }
    }

    private Feign.Builder createApiBuilder() {
        Preconditions.checkNotNull(context, "the connection context is null");

        // server addresses
        final List<NetworkAddress> licenseServerAddresses = context.getNodes();
        Preconditions.checkNotEmpty(licenseServerAddresses, new Supplier<Object[], String>() {
            @Override
            public String get(Object[] input) {
                return StringTemplates.formatWithPlaceholder("server addresses is invalid : {}", licenseServerAddresses);
            }
        });

        String loggerName = context.getConfiguration().getAccessLoggerName();
        Logger accessLogger = LoggerFactory.getLogger(loggerName);

        // access log level
        feign.Logger.Level accessLogLevel = context.getAccessLogLevel();
        if (accessLogLevel == null) {
            if (accessLogger.isDebugEnabled()) {
                accessLogLevel = feign.Logger.Level.FULL;
            } else {
                accessLogLevel = feign.Logger.Level.NONE;
            }
        }
        if (accessLogLevel != feign.Logger.Level.NONE && !logger.isDebugEnabled()) {
            logger.warn("the access log is enabled, the logger level of {} should be DEBUG at least", logger.getName());
        }

        // http client
        Client client = new ApacheHttpClient(context.getHttpClient());
        if (context.isLoadbalancerEnabled()) {
            LBClientFactory clientFactory = new DynamicLBClientFactory(context);
            client = RibbonClient.builder().delegate(client).lbClientFactory(clientFactory).build();
        }

        // 设置 name
        if (Strings.isEmpty(name)) {
            name = context.getServiceName();
            if (Strings.isEmpty(name)) {
                name = context.getConfiguration().getProtocol() + " " + context.getNodesString();
            }
        }

        if(Emptys.isAnyEmpty(encoder, decoder, errorDecoder, errorHandler)){
            if(customizer!=null){
                customizer.customize(this);
            }
        }

        Preconditions.checkNotNull(encoder, "the encoder is null");
        Preconditions.checkNotNull(errorDecoder, "the error-decoder is null");
        Preconditions.checkNotNull(decoder, "the decoder is null");
        Preconditions.checkNotNull(errorHandler, "the error-handler is null");
        if (responseBodyAdapter != null) {
            if (decoder instanceof AdaptableDecoder) {
                ((AdaptableDecoder) decoder).setAdapter(responseBodyAdapter);
            } else {
                decoder = new AdaptableDecoder(decoder, responseBodyAdapter);
            }
        }

        Feign.Builder apiBuilder = Feign.builder()
                .logger(new Slf4jLogger(loggerName))
                .logLevel(accessLogLevel)
                .client(client)
                .encoder(encoder)
                .decoder(decoder)
                .errorDecoder(errorDecoder);

        RpcInvocationHandlerFactory invocationHandlerFactory = new RpcInvocationHandlerFactory();
        invocationHandlerFactory.setErrorHandler(errorHandler);
        apiBuilder.invocationHandlerFactory(invocationHandlerFactory);

        if (!requestInterceptors.isEmpty()) {
            apiBuilder.requestInterceptors(requestInterceptors);
        }

        return apiBuilder;

    }

    @Override
    public <Stub> Stub getStub(Class<Stub> stubInterface) {
        if (!initialed) {
            init();
        }
        Preconditions.checkTrue(initialed, "service provider is not initialed");
        Preconditions.checkArgument(stubInterface.isInterface(), new Supplier<Object[], String>() {
            @Override
            public String get(Object[] objects) {
                return StringTemplates.formatWithPlaceholder("the service class {} is not interface");
            }
        }, Reflects.getFQNClassName(stubInterface));

        boolean isNotSingleton = Reflects.isAnnotationPresent(stubInterface, Prototype.class);
        if (isNotSingleton) {
            return createStub(stubInterface);
        }
        return (Stub) Maps.putIfAbsent(serviceMap, stubInterface, (Supplier<Class<Stub>, Stub>) new Supplier<Class<Stub>, Stub>() {
            @Override
            public Stub get(Class<Stub> clazz) {
                return createStub(clazz);
            }
        });

    }

    private <Service> Service createStub(Class<Service> serviceClass) {
        String url = context.getUrl();
        logger.info("create a service [{}] at the [{}] url: {}", Reflects.getFQNClassName(serviceClass), context.getConfiguration().getLoadbalancerHost(), url);
        return apiBuilder.target(serviceClass, url);
    }
}
