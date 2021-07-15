package com.jn.agileway.feign;

import com.jn.agileway.feign.codec.EasyjsonDecoder;
import com.jn.agileway.feign.codec.EasyjsonEncoder;
import com.jn.agileway.feign.codec.EasyjsonErrorDecoder;
import com.jn.agileway.feign.loadbalancer.DynamicLBClientFactory;
import com.jn.agileway.feign.supports.unifiedresponse.UnifiedResponseBodyDecoder;
import com.jn.agileway.feign.supports.unifiedresponse.UnifiedResponseInvocationHandlerFactory;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
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
import feign.InvocationHandlerFactory;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
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
    private Encoder encoder;
    private Decoder decoder;
    private ErrorDecoder errorDecoder;
    private InvocationHandlerFactory invocationHandlerFactory;


    /**
     * 如果项目中，没有对返回值进行统一处理，则可以设置为 Object.class
     */
    private Class unifiedRestResponseClass = RestRespBody.class;

    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public void setContext(HttpConnectionContext context) {
        this.context = context;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }
    public void setUnifiedRestResponseClass(Class unifiedRestResponseClass) {
        if (unifiedRestResponseClass != null) {
            this.unifiedRestResponseClass = unifiedRestResponseClass;
        }
    }

    public void setUnifiedRestResponse(Class unifiedRestResponseClass, UnifiedResponseBodyDecoder decoder){
        setUnifiedRestResponseClass(unifiedRestResponseClass);
        setDecoder(decoder);
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

        if (jsonFactory == null) {
            jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);
        }
        if (encoder == null) {
            encoder = new FormEncoder(new EasyjsonEncoder(jsonFactory));
        }
        if (decoder == null) {
            decoder = new EasyjsonDecoder(jsonFactory);
        }
        if (errorDecoder == null) {
            errorDecoder = new EasyjsonErrorDecoder();
        }
        Feign.Builder apiBuilder = Feign.builder()
                .logger(new Slf4jLogger(loggerName))
                .logLevel(accessLogLevel)
                .client(client)
                .encoder(encoder)
                .decoder(decoder)
                .errorDecoder(errorDecoder);

        if (this.invocationHandlerFactory == null) {
            UnifiedResponseInvocationHandlerFactory factory = new UnifiedResponseInvocationHandlerFactory();
            factory.setJsonFactory(jsonFactory);
            factory.setUnifiedResponseClass(unifiedRestResponseClass);
            this.invocationHandlerFactory = factory;
        }
        apiBuilder.invocationHandlerFactory(invocationHandlerFactory);
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
