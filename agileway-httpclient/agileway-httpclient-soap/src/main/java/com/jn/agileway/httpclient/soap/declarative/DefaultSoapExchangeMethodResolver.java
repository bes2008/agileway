package com.jn.agileway.httpclient.soap.declarative;

import com.jn.agileway.httpclient.declarative.AbstractHttpExchangeMethodResolver;
import com.jn.agileway.httpclient.declarative.DefaultValueSupportedValueGetter;
import com.jn.agileway.httpclient.declarative.HttpExchangeMethod;
import com.jn.agileway.httpclient.declarative.HttpExchangeMethodDeclaringException;
import com.jn.agileway.httpclient.declarative.anno.Body;
import com.jn.agileway.httpclient.declarative.anno.Header;
import com.jn.agileway.httpclient.declarative.anno.UriVariable;
import com.jn.agileway.httpclient.soap.declarative.anno.Soap;
import com.jn.agileway.httpclient.soap.declarative.anno.SoapEndpoint;
import com.jn.agileway.httpclient.soap.entity.SoapBinding;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.valuegetter.ArrayValueGetter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;

public class DefaultSoapExchangeMethodResolver extends AbstractHttpExchangeMethodResolver {

    private static final Class<? extends Annotation>[] REQUIRED_METHOD_ANNOTATIONS = new Class[]{Soap.class};

    @Override
    public Class<? extends Annotation>[] requiredMethodAnnotations() {
        return REQUIRED_METHOD_ANNOTATIONS;
    }

    @Override
    public Class<? extends Annotation> endpointAnnotation() {
        return SoapEndpoint.class;
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

        Parameter[] parameters = javaMethod.getParameters();
        if (Objs.isNotEmpty(parameters)) {
            for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
                Parameter parameter = parameters[parameterIndex];
                resolveMethodParameter(exchangeMethod, parameter, parameterIndex);
            }
        }

        if (exchangeMethod.getUriEncoding() == null) {
            String uriEncoding = soapEndpoint.uriEncoding();
            if (Strings.isNotBlank(uriEncoding)) {
                Charset charset = Charsets.getCharset(uriEncoding);
                exchangeMethod.setUriEncoding(charset);
            }
        }

    }


    private void resolveMethodParameter(HttpExchangeMethod exchangeMethod, Parameter parameter, int parameterIndex) {
        boolean annoResolved = false;

        UriVariable uriVariable = parameter.getAnnotation(UriVariable.class);
        if (uriVariable != null) {
            resolveUriVariable(exchangeMethod, parameter, uriVariable, parameterIndex);
            annoResolved = true;
        }

        if (!annoResolved) {
            Header header = parameter.getAnnotation(Header.class);
            if (header != null) {
                resolveHeader(exchangeMethod, parameter, header, parameterIndex);
                annoResolved = true;
            }
        }

        if (!annoResolved) {
            Body body = parameter.getAnnotation(Body.class);
            if (body != null) {
                resolveBody(exchangeMethod, parameter, body, parameterIndex);
                annoResolved = true;
            }
        }

        if (!annoResolved) {
            resolveBody(exchangeMethod, parameter, null, parameterIndex);
        }

    }

    private void resolveUriVariable(HttpExchangeMethod exchangeMethod, Parameter parameter, @NonNull UriVariable uriVariable, int parameterIndex) {
        String uriVariableName = uriVariable.value();
        if (Strings.isBlank(uriVariableName)) {
            uriVariableName = parameter.getName();
        }
        exchangeMethod.getUriVariables().put(uriVariableName, new ArrayValueGetter<Object>(parameterIndex));
    }

    private void resolveHeader(HttpExchangeMethod exchangeMethod, Parameter parameter, @NonNull Header header, int parameterIndex) {
        String headerName = header.value();
        if (Strings.isBlank(headerName)) {
            headerName = parameter.getName();
        }
        String defaultValue = header.defaultValue();
        exchangeMethod.getHeaders().put(headerName, new DefaultValueSupportedValueGetter(parameterIndex, defaultValue));
    }

    private void resolveBody(HttpExchangeMethod exchangeMethod, Parameter parameter, @NonNull Body body, int parameterIndex) {
        if (exchangeMethod.getBody() != null) {
            throw new HttpExchangeMethodDeclaringException("The method " + exchangeMethod.getJavaMethod().getName() + " has already a body");
        }
        exchangeMethod.setBody(new ArrayValueGetter<Object>(parameterIndex));
    }
}
