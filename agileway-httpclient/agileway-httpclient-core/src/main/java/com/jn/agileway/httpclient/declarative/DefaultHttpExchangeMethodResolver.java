package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.declarative.anno.*;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.valuegetter.ArrayValueGetter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;

public class DefaultHttpExchangeMethodResolver extends AbstractHttpExchangeMethodResolver {
    @Override
    protected void resolveInternal(HttpExchangeMethod exchangeMethod, Method javaMethod) {
        Class<?> declaringClass = javaMethod.getDeclaringClass();
        HttpEndpoint httpEndpoint = declaringClass.getAnnotation(HttpEndpoint.class);
        if (httpEndpoint == null) {
            throw new HttpExchangeMethodDeclaringException("The class " + declaringClass.getName() + " is not annotated with @HttpExchange");
        }
        String uriPrefix = httpEndpoint.value();
        if (Strings.isBlank(uriPrefix)) {
            uriPrefix = "";
        }
        String[] defaultAccept = httpEndpoint.accept();
        String contentTypeString = httpEndpoint.contentType();
        MediaType defaultContentType = null;
        if (Strings.isNotBlank(contentTypeString)) {
            defaultContentType = MediaType.valueOf(contentTypeString);
        }
        resolveMethodAnnotation(exchangeMethod, uriPrefix, defaultAccept, defaultContentType, javaMethod);

        Parameter[] parameters = javaMethod.getParameters();
        if (Objs.isNotEmpty(parameters)) {
            for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
                Parameter parameter = parameters[parameterIndex];
                resolveMethodParameter(exchangeMethod, parameter, parameterIndex);
            }
        }

        if (exchangeMethod.getUriEncoding() == null) {
            String uriEncoding = httpEndpoint.uriEncoding();
            if (Strings.isNotBlank(uriEncoding)) {
                Charset charset = Charsets.getCharset(uriEncoding);
                exchangeMethod.setUriEncoding(charset);
            }
        }
    }

    private void resolveMethodParameter(HttpExchangeMethod exchangeMethod, Parameter parameter, int parameterIndex) {
        boolean annoResolved = false;
        QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
        if (queryParam != null) {
            resolveQueryParam(exchangeMethod, parameter, queryParam, parameterIndex);
            annoResolved = true;
        }

        if (!annoResolved) {
            UriVariable uriVariable = parameter.getAnnotation(UriVariable.class);
            if (uriVariable != null) {
                resolveUriVariable(exchangeMethod, parameter, uriVariable, parameterIndex);
                annoResolved = true;
            }
        }

        if (!annoResolved) {
            Header header = parameter.getAnnotation(Header.class);
            if (header != null) {
                resolveHeader(exchangeMethod, parameter, header, parameterIndex);
                annoResolved = true;
            }
        }

        if (!annoResolved) {
            Cookie cookie = parameter.getAnnotation(Cookie.class);
            if (cookie != null) {
                resolveCookie(exchangeMethod, parameter, cookie, parameterIndex);
                annoResolved = true;
            }
        }

        if (!annoResolved) {
            BodyPart bodyPart = parameter.getAnnotation(BodyPart.class);
            if (bodyPart != null) {
                resolveBodyPart(exchangeMethod, parameter, bodyPart, parameterIndex);
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
            resolveQueryParam(exchangeMethod, parameter, null, parameterIndex);
        }

    }

    private void resolveCookie(HttpExchangeMethod exchangeMethod, Parameter parameter, @NonNull Cookie cookie, int parameterIndex) {
        String cookieName = cookie.value();
        if (Strings.isBlank(cookieName)) {
            cookieName = parameter.getName();
        }
        exchangeMethod.getCookies().put(cookieName, new ArrayValueGetter(parameterIndex));
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

    private void resolveBodyPart(HttpExchangeMethod exchangeMethod, Parameter parameter, @NonNull BodyPart bodyPart, int parameterIndex) {
        String bodyPartName = bodyPart.value();
        if (Strings.isBlank(bodyPartName)) {
            bodyPartName = parameter.getName();
        }
        exchangeMethod.getBodyParts().put(bodyPartName, new ArrayValueGetter<Object>(parameterIndex));
    }

    private void resolveUriVariable(HttpExchangeMethod exchangeMethod, Parameter parameter, @NonNull UriVariable uriVariable, int parameterIndex) {
        String uriVariableName = uriVariable.value();
        if (Strings.isBlank(uriVariableName)) {
            uriVariableName = parameter.getName();
        }
        exchangeMethod.getUriVariables().put(uriVariableName, new ArrayValueGetter<Object>(parameterIndex));
    }

    private void resolveQueryParam(HttpExchangeMethod exchangeMethod, Parameter parameter, @Nullable QueryParam queryParam, int parameterIndex) {
        String queryParamName = null;
        if (queryParam != null) {
            queryParamName = queryParam.value();
        }
        if (Strings.isBlank(queryParamName)) {
            queryParamName = parameter.getName();
        }
        if (exchangeMethod.getQueryParams().containsKey(queryParamName)) {
            throw new HttpExchangeMethodDeclaringException("The query param " + queryParamName + " is already defined");
        }
        String defaultValue = queryParam == null ? null : queryParam.defaultValue();
        exchangeMethod.getQueryParams().put(queryParamName, new DefaultValueSupportedValueGetter(parameterIndex, defaultValue));
    }

    private void resolveMethodAnnotation(HttpExchangeMethod exchangeMethod, String uriPrefix, String[] defaultAccept, MediaType defaultContentType, Method javaMethod) {

        Get getAnno = javaMethod.getAnnotation(Get.class);
        boolean annoResolved = false;
        if (getAnno != null) {
            resolveGet(exchangeMethod, getAnno, uriPrefix, defaultAccept);
            annoResolved = true;
        }

        if (!annoResolved) {
            Post postAnno = javaMethod.getAnnotation(Post.class);
            if (postAnno != null) {
                resolvePost(exchangeMethod, postAnno, uriPrefix, defaultAccept, defaultContentType);
                annoResolved = true;
            }
        }
        if (!annoResolved) {
            Put putAnno = javaMethod.getAnnotation(Put.class);
            if (putAnno != null) {
                resolvePut(exchangeMethod, putAnno, uriPrefix, defaultAccept, defaultContentType);
                annoResolved = true;
            }
        }

        if (!annoResolved) {
            Patch patchAnno = javaMethod.getAnnotation(Patch.class);
            if (patchAnno != null) {
                resolvePatch(exchangeMethod, patchAnno, uriPrefix, defaultAccept, defaultContentType);
                annoResolved = true;
            }
        }
        if (!annoResolved) {
            Delete deleteAnno = javaMethod.getAnnotation(Delete.class);
            if (deleteAnno != null) {
                resolveDelete(exchangeMethod, deleteAnno, uriPrefix, defaultAccept, defaultContentType);
            }
        }

        if (!annoResolved) {
            throw new HttpExchangeMethodDeclaringException("The method " + javaMethod.getName() + " is not annotated with @Get, @Post, @Put, @Patch, @Delete");
        }
    }

    private void resolveDelete(HttpExchangeMethod exchangeMethod, Delete deleteAnno, String uriPrefix, String[] defaultAccept, MediaType defaultContentType) {
        String uri = deleteAnno.value();
        if (Strings.isBlank(uri)) {
            uri = "";
        }
        if (Strings.isEmpty(uri)) {
            uri = Strings.stripEnd(uriPrefix, "/");
        } else {
            uri = Strings.stripEnd(uriPrefix, "/") + "/" + Strings.stripStart(uri, "/");
        }

        String[] accept = deleteAnno.accept();
        accept = Objs.useValueIfEmpty(accept, defaultAccept);

        String uriEncoding = deleteAnno.uriEncoding();
        if (Strings.isNotBlank(uriEncoding)) {
            Charset charset = Charsets.getCharset(uriEncoding);
            exchangeMethod.setUriEncoding(charset);
        }

        String contentTypeString = deleteAnno.contentType();
        MediaType contentType = null;
        if (Strings.isNotBlank(contentTypeString)) {
            contentType = MediaType.valueOf(contentTypeString);
        }
        contentType = Objs.useValueIfNull(contentType, defaultContentType);

        exchangeMethod.setAccept(accept);
        exchangeMethod.setUriTemplate(uri);
        exchangeMethod.setContentType(contentType);
        exchangeMethod.setHttpMethod(HttpMethod.DELETE);
    }

    private void resolvePatch(HttpExchangeMethod exchangeMethod, Patch patchAnno, String uriPrefix, String[] defaultAccept, MediaType defaultContentType) {
        String uri = patchAnno.value();
        if (Strings.isBlank(uri)) {
            uri = "";
        }
        if (Strings.isEmpty(uri)) {
            uri = Strings.stripEnd(uriPrefix, "/");
        } else {
            uri = Strings.stripEnd(uriPrefix, "/") + "/" + Strings.stripStart(uri, "/");
        }

        String[] accept = patchAnno.accept();
        accept = Objs.useValueIfEmpty(accept, defaultAccept);

        String uriEncoding = patchAnno.uriEncoding();
        if (Strings.isNotBlank(uriEncoding)) {
            Charset charset = Charsets.getCharset(uriEncoding);
            exchangeMethod.setUriEncoding(charset);
        }

        String contentTypeString = patchAnno.contentType();
        MediaType contentType = null;
        if (Strings.isNotBlank(contentTypeString)) {
            contentType = MediaType.valueOf(contentTypeString);
        }
        contentType = Objs.useValueIfNull(contentType, defaultContentType);

        exchangeMethod.setAccept(accept);
        exchangeMethod.setUriTemplate(uri);
        exchangeMethod.setContentType(contentType);
        exchangeMethod.setHttpMethod(HttpMethod.PATCH);
    }

    private void resolvePut(HttpExchangeMethod exchangeMethod, Put putAnno, String uriPrefix, String[] defaultAccept, MediaType defaultContentType) {
        String uri = putAnno.value();
        if (Strings.isBlank(uri)) {
            uri = "";
        }
        if (Strings.isEmpty(uri)) {
            uri = Strings.stripEnd(uriPrefix, "/");
        } else {
            uri = Strings.stripEnd(uriPrefix, "/") + "/" + Strings.stripStart(uri, "/");
        }

        String[] accept = putAnno.accept();
        accept = Objs.useValueIfEmpty(accept, defaultAccept);

        String uriEncoding = putAnno.uriEncoding();
        if (Strings.isNotBlank(uriEncoding)) {
            Charset charset = Charsets.getCharset(uriEncoding);
            exchangeMethod.setUriEncoding(charset);
        }


        String contentTypeString = putAnno.contentType();
        MediaType contentType = null;
        if (Strings.isNotBlank(contentTypeString)) {
            contentType = MediaType.valueOf(contentTypeString);
        }
        contentType = Objs.useValueIfNull(contentType, defaultContentType);

        exchangeMethod.setAccept(accept);
        exchangeMethod.setUriTemplate(uri);
        exchangeMethod.setContentType(contentType);
        exchangeMethod.setHttpMethod(HttpMethod.PUT);
    }

    private void resolvePost(HttpExchangeMethod exchangeMethod, Post postAnno, String uriPrefix, String[] defaultAccept, MediaType defaultContentType) {
        String uri = postAnno.value();
        if (Strings.isBlank(uri)) {
            uri = "";
        }
        if (Strings.isEmpty(uri)) {
            uri = Strings.stripEnd(uriPrefix, "/");
        } else {
            uri = Strings.stripEnd(uriPrefix, "/") + "/" + Strings.stripStart(uri, "/");
        }

        String[] accept = postAnno.accept();
        accept = Objs.useValueIfEmpty(accept, defaultAccept);

        String uriEncoding = postAnno.uriEncoding();
        if (Strings.isNotBlank(uriEncoding)) {
            Charset charset = Charsets.getCharset(uriEncoding);
            exchangeMethod.setUriEncoding(charset);
        }

        String contentTypeString = postAnno.contentType();
        MediaType contentType = null;
        if (Strings.isNotBlank(contentTypeString)) {
            contentType = MediaType.valueOf(contentTypeString);
        }
        contentType = Objs.useValueIfNull(contentType, defaultContentType);

        exchangeMethod.setAccept(accept);
        exchangeMethod.setUriTemplate(uri);
        exchangeMethod.setContentType(contentType);
        exchangeMethod.setHttpMethod(HttpMethod.POST);
    }

    private void resolveGet(HttpExchangeMethod exchangeMethod, Get getAnno, String uriPrefix, String[] defaultAccept) {
        String uri = getAnno.value();
        if (Strings.isBlank(uri)) {
            uri = "";
        }
        if (Strings.isEmpty(uri)) {
            uri = Strings.stripEnd(uriPrefix, "/");
        } else {
            uri = Strings.stripEnd(uriPrefix, "/") + "/" + Strings.stripStart(uri, "/");
        }

        String[] accept = getAnno.accept();
        accept = Objs.useValueIfEmpty(accept, defaultAccept);

        String uriEncoding = getAnno.uriEncoding();
        if (Strings.isNotBlank(uriEncoding)) {
            Charset charset = Charsets.getCharset(uriEncoding);
            exchangeMethod.setUriEncoding(charset);
        }

        exchangeMethod.setAccept(accept);
        exchangeMethod.setUriTemplate(uri);
        exchangeMethod.setHttpMethod(HttpMethod.GET);
    }

    private static final Class<? extends Annotation>[] REQUIRED_METHOD_ANNOTATIONS = new Class[]{Get.class, Post.class, Put.class, Patch.class, Delete.class};

    @Override
    public Class<? extends Annotation>[] requiredMethodAnnotations() {
        return REQUIRED_METHOD_ANNOTATIONS;
    }
}
