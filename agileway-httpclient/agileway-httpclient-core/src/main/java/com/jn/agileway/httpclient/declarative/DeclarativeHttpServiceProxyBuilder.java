package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.eipchannel.core.endpoint.mapper.UnsupportedObjectException;
import com.jn.agileway.httpclient.Exchanger;
import com.jn.langx.Builder;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.signature.TypeSignatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.*;

public class DeclarativeHttpServiceProxyBuilder<S> implements Builder<S> {
    private static final Logger logger = LoggerFactory.getLogger(DeclarativeHttpServiceProxyBuilder.class);
    /**
     * 当前支持 HttpExchanger, SoapExchanger
     */
    @NonNull
    private Exchanger exchanger;
    @NonNull
    private HttpExchangeMethodResolver methodResolver = new DefaultHttpExchangeMethodResolver();
    @Nullable
    private String baseUri;

    private Charset uriEncoding = Charsets.UTF_8;

    @NonNull
    private Class<S> serviceInterface;

    public DeclarativeHttpServiceProxyBuilder(Class<S> serviceInterface) {
        Preconditions.checkNotNull(serviceInterface, "service interface is required");
        Preconditions.checkArgument(serviceInterface.isInterface(), "service interface must be interface");
        this.serviceInterface = serviceInterface;
    }

    public final DeclarativeHttpServiceProxyBuilder<S> withExchanger(Exchanger exchanger) {
        this.exchanger = exchanger;
        return this;
    }

    public final DeclarativeHttpServiceProxyBuilder<S> withMethodResolver(HttpExchangeMethodResolver methodResolver) {
        this.methodResolver = methodResolver;
        return this;
    }

    public final DeclarativeHttpServiceProxyBuilder<S> withBaseUri(String baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    public final DeclarativeHttpServiceProxyBuilder withUriEncoding(Charset uriEncoding) {
        this.uriEncoding = uriEncoding;
        return this;
    }

    public final S build() {
        Map<Method, HttpExchangeMethod> httpExchangeMethods = resolveServiceMethods();
        return (S) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{serviceInterface}, new HttpExchangeMethodInvocationHandler(baseUri, this.uriEncoding, httpExchangeMethods, exchanger));
    }

    private Map<Method, HttpExchangeMethod> resolveServiceMethods() {
        Preconditions.checkNotNull(serviceInterface, "service interface is required");

        Map<Method, HttpExchangeMethod> httpExchangeMethods = new HashMap<Method, HttpExchangeMethod>();
        List<Class> interfaces = new ArrayList<>(Reflects.getAllInterfaces(serviceInterface));
        Collections.reverse(interfaces);
        interfaces.add(serviceInterface);
        Class<? extends Annotation> endpointAnnotation = methodResolver.endpointAnnotation();
        if (endpointAnnotation == null) {
            throw new UnsupportedObjectException("invalid method resolver, the endpoint annotation is required");
        }
        for (Class interfaceClass : interfaces) {
            if (!Reflects.hasAnnotation(interfaceClass, endpointAnnotation)) {
                continue;
            }
            Collection<Method> declaredMethods = Reflects.getAllDeclaredMethods(interfaceClass, false);
            for (Method method : declaredMethods) {
                if (!Reflects.isAnnotatedWithAny(method, this.methodResolver.requiredMethodAnnotations())) {
                    logger.debug("The method {} is not annotated as http exchange method, ignore it", TypeSignatures.toMethodSignature(method));
                    continue;
                }
                HttpExchangeMethod httpExchangeMethod = this.methodResolver.resolve(method);
                httpExchangeMethods.put(method, httpExchangeMethod);
            }
        }
        return httpExchangeMethods;
    }

}
