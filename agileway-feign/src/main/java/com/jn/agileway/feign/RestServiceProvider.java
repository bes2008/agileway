package com.jn.agileway.feign;

import com.jn.agileway.feign.loadbalancer.DynamicLBClientFactory;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.supports.feign.codec.EasyjsonDecoder;
import com.jn.easyjson.supports.feign.codec.EasyjsonEncoder;
import com.jn.easyjson.supports.feign.codec.EasyjsonErrorDecoder;
import com.jn.langx.annotation.Prototype;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.net.NetworkAddress;
import com.jn.langx.util.reflect.Reflects;
import feign.Client;
import feign.Feign;
import feign.form.FormEncoder;
import feign.httpclient.ApacheHttpClient;
import feign.ribbon.LBClientFactory;
import feign.ribbon.RibbonClient;
import feign.slf4j.Slf4jLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RestServiceProvider implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(RestServiceProvider.class);
    private Feign.Builder builder;
    private HttpConnectionContext context;
    private volatile boolean inited = false;
    private ConcurrentHashMap<Class, Object> serviceMap = new ConcurrentHashMap<Class, Object>();
    private JSONFactory jsonFactory;
    private Class unifiedRestResponseClass = RestRespBody.class;
    private boolean unifiedRestResponseEnabled = true;

    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    public void setUnifiedRestResponseClass(Class unifiedRestResponseClass) {
        if (unifiedRestResponseClass != null) {
            this.unifiedRestResponseClass = unifiedRestResponseClass;
        }
    }

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public void setContext(HttpConnectionContext context) {
        this.context = context;
    }

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            inited = true;
            builder = createFeignBuilder();
        }
    }

    private Feign.Builder createFeignBuilder() {
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



        Feign.Builder apiBuilder = Feign.builder()
                .logger(new Slf4jLogger(loggerName))
                .logLevel(accessLogLevel)
                .client(client)

                .encoder(new FormEncoder(new EasyjsonEncoder()))
                .decoder(new EasyjsonDecoder())
                .errorDecoder(new EasyjsonErrorDecoder());
        if(unifiedRestResponseEnabled){
            UnifiedResponseInvocationHandlerFactory invocationHandlerFactory = new UnifiedResponseInvocationHandlerFactory();
            invocationHandlerFactory.setJsonFactory(jsonFactory);
            invocationHandlerFactory.setUnifiedResponseClass(unifiedRestResponseClass);

            apiBuilder.invocationHandlerFactory(invocationHandlerFactory);
        }
        return apiBuilder;

    }


    public <Service> Service getService(Class<Service> serviceInterface) {
        if (!inited) {
            init();
        }
        Preconditions.checkTrue(inited, "service provider is not inited");
        Preconditions.checkArgument(serviceInterface.isInterface(), new Supplier<Object[], String>() {
            @Override
            public String get(Object[] objects) {
                return StringTemplates.formatWithPlaceholder("the service class {} is not interface");
            }
        }, Reflects.getFQNClassName(serviceInterface));

        boolean isNotSingleton = Reflects.isAnnotationPresent(serviceInterface, Prototype.class);
        if (isNotSingleton) {
            return createService(serviceInterface);
        }
        return (Service) Maps.putIfAbsent(serviceMap, serviceInterface, (Supplier<Class<Service>, Service>) new Supplier<Class<Service>, Service>() {
            @Override
            public Service get(Class<Service> clazz) {
                return createService(clazz);
            }
        });

    }

    private <Service> Service createService(Class<Service> serviceClass) {
        String url = context.getUrl();
        logger.info("create a service [{}] at the [{}] url: {}", Reflects.getFQNClassName(serviceClass), context.getConfiguration().getLoadbalancerHost(), url);
        return builder.target(serviceClass, url);
    }
}
