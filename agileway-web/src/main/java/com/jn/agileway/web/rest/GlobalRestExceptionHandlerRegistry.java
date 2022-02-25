package com.jn.agileway.web.rest;

import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.collection.NonDistinctTreeSet;
import com.jn.langx.util.comparator.OrderedComparator;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同一个web应用中，可能有多级的GlobalRestExceptionHandler，例如 Spring Controller级别的，有 javax.servlet.Filter级别的
 */
public class GlobalRestExceptionHandlerRegistry implements Registry<String, RestActionExceptionHandlerRegistration>, Initializable {
    private static final Logger logger = Loggers.getLogger(GlobalRestExceptionHandlerRegistry.class);
    private volatile boolean inited = false;
    /**
     * Key: Exception 类
     */
    private ConcurrentHashMap<Class, RestActionExceptionHandlerRegistration> cache = new ConcurrentHashMap<Class, RestActionExceptionHandlerRegistration>();

    private RestActionExceptionHandlerOrderFinder exceptionHandlerOrderFinder = new DefaultRestActionExceptionHandlerOrderFinder();

    private Map<String, RestActionExceptionHandlerRegistration> registrationMap = Collects.emptyHashMap(true);
    /**
     * 按照优先级 高到低 的顺序排序
     */
    private Set<RestActionExceptionHandlerRegistration> sortedRegistrations = new NonDistinctTreeSet<RestActionExceptionHandlerRegistration>(new OrderedComparator<RestActionExceptionHandlerRegistration>());

    private void addRestActionExceptionHandlerRegistration(String key, RestActionExceptionHandlerRegistration registration) {
        Preconditions.checkNotNull(registration);
        Maps.putIfAbsent(this.registrationMap, key, registration);
        sortedRegistrations.add(registration);
    }

    public void setExceptionHandlerOrderFinder(RestActionExceptionHandlerOrderFinder exceptionHandlerOrderFinder) {
        this.exceptionHandlerOrderFinder = exceptionHandlerOrderFinder;
    }

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            inited = true;
            logger.info("Initial global rest exception handler registry");
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
    public RestActionExceptionHandlerRegistration get(String name) {
        return registrationMap.get(name);
    }

    @Override
    public void register(String name, RestActionExceptionHandlerRegistration registration) {
        RestActionExceptionHandler exceptionHandler = registration.getExceptionHandler();
        name = Strings.useValueIfBlank(name, registration.getName());
        Preconditions.checkNotNull(name, "the exception handler names is null");
        Preconditions.checkNotNull(exceptionHandler, "exception handler is null for registration {}", name);
        Preconditions.checkTrue(!(exceptionHandler instanceof GlobalRestExceptionHandler), "can't register a global exception handler to registration");
        addRestActionExceptionHandlerRegistration(name, registration);
        logger.info("Register exception handler {} successfully", name);
    }

    public void register(RestActionExceptionHandlerRegistration registration) {
        register(registration.getName(), registration);
    }

    public void register(final RestActionExceptionHandler exceptionHandler) {
        if (exceptionHandler == null) {
            return;
        }
        if (exceptionHandler instanceof GlobalRestExceptionHandler) {
            return;
        }
        Class resolverClass = exceptionHandler.getClass();
        if (Reflects.isAnnotationPresent(resolverClass, RestActionExceptions.class)) {
            RestActionExceptions exceptions = Reflects.getAnnotation(resolverClass, RestActionExceptions.class);
            RestActionException[] restActionExceptions = exceptions.value();

            if (Emptys.isNotEmpty(restActionExceptions)) {
                final RestActionExceptionHandlerRegistration registration = new RestActionExceptionHandlerRegistration();

                Collects.forEach(Collects.asList(restActionExceptions), new Predicate<RestActionException>() {
                    @Override
                    public boolean test(RestActionException actionException) {
                        Class exceptionClass = actionException.value();
                        if (Reflects.isSubClassOrEquals(Throwable.class, exceptionClass)) {
                            return true;
                        } else {
                            logger.error("****ERROR**** Can't register {} for class : {}", Reflects.getFQNClassName(exceptionHandler.getClass()), Reflects.getFQNClassName(exceptionClass));
                            return false;
                        }
                    }
                }, new Consumer<RestActionException>() {
                    @Override
                    public void accept(RestActionException actionException) {
                        RestActionExceptionHandlerDefinition element = new RestActionExceptionHandlerDefinition();
                        element.setSupportExtends(actionException.supportExtends());
                        element.setDefaultStatusCode(actionException.defaultStatusCode());
                        element.setDefaultErrorCode(actionException.defaultErrorCode());
                        element.setDefaultErrorMessage(actionException.defaultErrorMessage());
                        element.setExceptionClass(actionException.value());
                        if (element.isValid()) {
                            registration.addExceptionClass(element);
                            GlobalRestExceptionHandlerRegistry.this.cache.putIfAbsent(element.getExceptionClass(), registration);
                        }
                    }
                });
                String name = exceptions.name();
                if (Emptys.isEmpty(name)) {
                    name = Reflects.getFQNClassName(exceptionHandler.getClass());
                }
                registration.setName(name);
                registration.setExceptionHandler(exceptionHandler);

                int order = exceptionHandlerOrderFinder.get(exceptionHandler);
                registration.setOrder(order);
                
                register(registration);
            } else {
                logger.warn("Can't register {} , please check @com.jn.agileway.web.rest.RestActionExceptions at the class", Reflects.getFQNClassName(resolverClass));
            }

        } else {
            logger.warn("Can't register {} , the @com.jn.agileway.web.rest.RestActionExceptions was not found at the class", Reflects.getFQNClassName(resolverClass));
        }
    }

    public RestActionExceptionHandlerRegistration findExceptionResolver(final Throwable ex) {
        return findExceptionResolver(ex, true);
    }

    public RestActionExceptionHandlerRegistration findExceptionResolver(final Throwable ex, final boolean causeScanEnabled) {
        // 从缓存找
        RestActionExceptionHandlerRegistration restHandlerExceptionResolverRegistration = cache.get(ex.getClass());

        // 从注册找
        if (restHandlerExceptionResolverRegistration == null) {
            restHandlerExceptionResolverRegistration = Collects.findFirst(sortedRegistrations, new Predicate<RestActionExceptionHandlerRegistration>() {
                @Override
                public boolean test(RestActionExceptionHandlerRegistration registration) {
                    return registration.findMatchedRegistration(ex, causeScanEnabled) != null;
                }
            });
        }

        if (restHandlerExceptionResolverRegistration == null && causeScanEnabled && ex.getCause() != null && ex.getCause() != ex) {
            return findExceptionResolver(ex.getCause(), causeScanEnabled);
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

}
