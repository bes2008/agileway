package com.jn.agileway.web.rest;

import com.jn.easyjson.core.JSONFactory;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.lifecycle.Lifecycle;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;


public abstract class GlobalRestExceptionHandler implements Initializable, Lifecycle {
    private static Logger logger = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);
    private JSONFactory jsonFactory;
    /**
     * 是否根据 异常链扫描
     * <p>
     * 这个配置存在的意义: 有人喜欢把真正的异常用RuntimeException包装
     */
    private boolean causeScanEnabled = false;

    /**
     * 默认的出错时的 http status code
     */
    private int defaultErrorStatusCode;

    private String defaultErrorCode;
    private String defaultErrorMessage;
    private volatile boolean inited = false;
    private volatile boolean running = false;
    private boolean writeUnifiedResponse = true;

    private RestErrorMessageHandler errorMessageHandler = NoopRestErrorMessageHandler.INSTANCE;

    /**
     * Key: Exception 类
     */
    private ConcurrentHashMap<Class, RestActionExceptionHandlerRegistration> cache = new ConcurrentHashMap<Class, RestActionExceptionHandlerRegistration>();

    private Map<String, RestActionExceptionHandlerRegistration> registrationMap = Collects.emptyHashMap(true);

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public RestRespBody handleException(HttpServletRequest request, HttpServletResponse response, Object action, Exception ex) {
        if (!inited) {
            init();
        }

        RestRespBody respBody = null;

        if (isSupportedRestAction(request, response, action, ex)) {

            // 找那些注册好了的异常处理
            RestActionExceptionHandlerRegistration restHandlerExceptionResolverRegistration = findExceptionResolver(ex);


            if (restHandlerExceptionResolverRegistration != null) {
                respBody = restHandlerExceptionResolverRegistration.getExceptionHandler().handle(request, response, action, ex);
                if (respBody == null) {
                    RestActionExceptionHandlerRegistrationElement element = restHandlerExceptionResolverRegistration.findMatchedRegistration(ex, causeScanEnabled);
                    RestRespBody.error(element.getDefaultStatusCode(), element.getDefaultErrorCode(), element.getDefaultErrorMessage());
                }
            }

            if (respBody == null) {
                logger.error(ex.getMessage(), ex);
                respBody = RestRespBody.error(defaultErrorStatusCode, defaultErrorCode, defaultErrorMessage);
            }

            errorMessageHandler.handler(respBody);

            if (writeUnifiedResponse) {
                try {
                    response.setStatus(respBody.getStatusCode());
                    String jsonstring = jsonFactory.get().toJson(respBody);
                    response.getWriter().write(jsonstring);
                } catch (IOException ioe) {
                    logger.warn(ioe.getMessage(), ioe);
                }
            }
        }
        return respBody;
    }

    private RestActionExceptionHandlerRegistration findExceptionResolver(final Throwable ex) {
        // 从缓存找
        RestActionExceptionHandlerRegistration restHandlerExceptionResolverRegistration = cache.get(ex.getClass());

        // 从注册找
        if (restHandlerExceptionResolverRegistration == null) {
            restHandlerExceptionResolverRegistration = Collects.findFirst(registrationMap.values(), new Predicate<RestActionExceptionHandlerRegistration>() {
                @Override
                public boolean test(RestActionExceptionHandlerRegistration registration) {
                    return registration.findMatchedRegistration(ex, causeScanEnabled) != null;
                }
            });
        }

        if (restHandlerExceptionResolverRegistration == null && causeScanEnabled && ex.getCause() != null && ex.getCause() != ex) {
            return findExceptionResolver(ex.getCause());
        }
        if (restHandlerExceptionResolverRegistration != null) {
            if (ex.getClass() == Exception.class || ex.getClass() == RuntimeException.class) {
                // noop
            } else {
                cache.putIfAbsent(ex.getClass(), restHandlerExceptionResolverRegistration);
            }
            return restHandlerExceptionResolverRegistration;
        }

        return null;
    }


    protected abstract boolean isSupportedRestAction(HttpServletRequest request, HttpServletResponse response, Object action, Exception ex);


    public void register(RestActionExceptionHandler exceptionHandler) {
        Class resolverClass = exceptionHandler.getClass();
        if (Reflects.isAnnotationPresent(resolverClass, RestActionExceptions.class)) {
            RestActionExceptions exceptions = Reflects.getAnnotation(resolverClass, RestActionExceptions.class);
            RestActionException[] restActionExceptions = exceptions.value();

            if (Emptys.isNotEmpty(restActionExceptions)) {
                final RestActionExceptionHandlerRegistration registration = new RestActionExceptionHandlerRegistration();

                Collects.forEach(restActionExceptions, new Consumer<RestActionException>() {
                    @Override
                    public void accept(RestActionException actionException) {
                        RestActionExceptionHandlerRegistrationElement element = new RestActionExceptionHandlerRegistrationElement();
                        element.setSupportExtends(actionException.supportExtends());
                        element.setDefaultStatusCode(actionException.defaultStatusCode());
                        element.setDefaultErrorCode(actionException.defaultErrorCode());
                        element.setDefaultErrorMessage(actionException.defaultErrorMessage());
                        element.setExceptionClass(actionException.value());
                        if (element.isValid()) {
                            registration.addExceptionClass(element);
                            GlobalRestExceptionHandler.this.cache.putIfAbsent(element.getExceptionClass(), registration);
                        }
                    }
                });
                String name = exceptions.name();
                if (Emptys.isEmpty(name)) {
                    name = Reflects.getFQNClassName(exceptionHandler.getClass());
                }
                registration.setName(name);
                registration.setExceptionHandler(exceptionHandler);
                register(registration);
            }

        }
    }


    @Override
    public void init() throws InitializationException {
        if (!inited) {
            inited = true;

            ServiceLoader<RestActionExceptionHandler> serviceLoader = ServiceLoader.load(RestActionExceptionHandler.class);
            Collects.forEach(serviceLoader, new Consumer<RestActionExceptionHandler>() {
                @Override
                public void accept(final RestActionExceptionHandler exceptionHandler) {
                    register(exceptionHandler);
                }
            });
        }
    }

    @Override
    public void startup() {
        init();
        if (!running) {
            running = true;
        }
    }

    @Override
    public void shutdown() {
        running = false;
    }

    public void setErrorMessageHandler(RestErrorMessageHandler errorMessageHandler) {
        if (errorMessageHandler != null) {
            this.errorMessageHandler = errorMessageHandler;
        }
    }

    public void setCauseScanEnabled(boolean causeScanEnabled) {
        this.causeScanEnabled = causeScanEnabled;
    }

    public void setDefaultErrorStatusCode(int defaultErrorStatusCode) {
        this.defaultErrorStatusCode = defaultErrorStatusCode;
    }

    public void setDefaultErrorCode(String defaultErrorCode) {
        this.defaultErrorCode = defaultErrorCode;
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }

    public void register(String name, RestActionExceptionHandlerRegistration registration) {
        this.registrationMap.put(name, registration);
    }

    public void register(RestActionExceptionHandlerRegistration registration) {
        Maps.putIfAbsent(this.registrationMap, registration.getName(), registration);
    }

    public void setWriteUnifiedResponse(boolean writeUnifiedResponse) {
        this.writeUnifiedResponse = writeUnifiedResponse;
    }

}
