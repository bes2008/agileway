package com.jn.agileway.httpclient.soap.declarative;

import com.jn.agileway.httpclient.declarative.AbstractHttpExchangeMethodResolver;
import com.jn.agileway.httpclient.declarative.HttpExchangeMethod;
import com.jn.agileway.httpclient.declarative.HttpExchangeMethodDeclaringException;
import com.jn.agileway.httpclient.soap.declarative.anno.Soap;
import com.jn.agileway.httpclient.soap.declarative.anno.SoapEndpoint;
import com.jn.agileway.httpclient.soap.entity.SoapBinding;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.reflect.Reflects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DefaultSoapExchangeMethodResolver extends AbstractHttpExchangeMethodResolver {

    private static final Class<? extends Annotation>[] REQUIRED_METHOD_ANNOTATIONS = new Class[]{Soap.class};

    @Override
    public Class<? extends Annotation>[] requiredMethodAnnotations() {
        return REQUIRED_METHOD_ANNOTATIONS;
    }
    @Override
    protected void resolveInternal(HttpExchangeMethod exchangeMethod, Method javaMethod) {

        if (!Reflects.isAnnotationPresent(javaMethod, Soap.class)) {
            throw new HttpExchangeMethodDeclaringException("The method " + javaMethod.getName() + " is not annotated with @Soap");
        }
        Class<?> declaringClass = javaMethod.getDeclaringClass();
        SoapEndpoint soapEndpoint = declaringClass.getAnnotation(SoapEndpoint.class);
        if (soapEndpoint == null) {
            throw new HttpExchangeMethodDeclaringException("The class " + declaringClass.getName() + " is not annotated with @SoapExchange");
        }
        String uri = soapEndpoint.value();
        if (Strings.isBlank(uri)) {
            uri = "";
        }
        SoapBinding soapBinding = soapEndpoint.binding();
        if (soapBinding == null) {
            soapBinding = SoapBinding.SOAP12_HTTP;
        }
        exchangeMethod.setUriTemplate(uri);
        exchangeMethod.setContentType(soapBinding.getContentType());
        exchangeMethod.setHttpMethod(HttpMethod.POST);

        resolveMethodParameter();
    }

    private void resolveMethodParameter() {
    }
}
