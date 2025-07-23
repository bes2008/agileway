package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.declarative.anno.*;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.valuegetter.ArrayValueGetter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class HttpExchangeMethodResolver {
    public HttpExchangeMethod resolve(Method javaMethod) {
        HttpExchangeMethod exchangeMethod = new HttpExchangeMethod();
        exchangeMethod.setJavaMethod(javaMethod);

        Class<?> declaringClass = javaMethod.getDeclaringClass();
        HttpExchange httpExchange = declaringClass.getAnnotation(HttpExchange.class);
        if (httpExchange == null) {
            throw new HttpExchangeMethodDeclaringException("The class " + declaringClass.getName() + " is not annotated with @HttpExchange");
        }
        String uriPrefix = httpExchange.value();
        if (Strings.isNotBlank(uriPrefix)) {
            uriPrefix = "";
        }
        String[] defaultAccept = httpExchange.accept();
        String contentTypeString = httpExchange.contentType();
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

        exchangeMethod.checkValid();
        return exchangeMethod;
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
            BodyPart bodyPart = parameter.getAnnotation(BodyPart.class);
            if (bodyPart != null) {
                resolveBodyPart(exchangeMethod, parameter, bodyPart, parameterIndex);
            }
        }
    }

    private void resolveBodyPart(HttpExchangeMethod exchangeMethod, Parameter parameter, BodyPart bodyPart, int parameterIndex) {
        String bodyPartName = bodyPart.value();
    }

    private void resolveUriVariable(HttpExchangeMethod exchangeMethod, Parameter parameter, UriVariable uriVariable, int parameterIndex) {
        String uriVariableName = uriVariable.value();
        if (Strings.isBlank(uriVariableName)) {
            uriVariableName = parameter.getName();
        }
        if (exchangeMethod.getUriVariables().containsKey(uriVariableName)) {
            throw new HttpExchangeMethodDeclaringException("The uri variable " + uriVariableName + " is already defined");
        }
        exchangeMethod.getUriVariables().put(uriVariableName, new ArrayValueGetter<Object>(parameterIndex));
    }

    private void resolveQueryParam(HttpExchangeMethod exchangeMethod, Parameter parameter, QueryParam queryParam, int parameterIndex) {
        String queryParamName = queryParam.value();
        if (Strings.isBlank(queryParamName)) {
            queryParamName = parameter.getName();
        }
        if (exchangeMethod.getQueryParams().containsKey(queryParamName)) {
            throw new HttpExchangeMethodDeclaringException("The query param " + queryParamName + " is already defined");
        }
        String defaultValue = queryParam.defaultValue();
        exchangeMethod.getQueryParams().put(queryParamName, new QueryParamValueGetter(parameterIndex, defaultValue));
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

        exchangeMethod.setAccept(accept);
        exchangeMethod.setUriTemplate(uri);
        exchangeMethod.setHttpMethod(HttpMethod.GET);
    }

    private void resolveMethodArgument(HttpExchangeMethod exchangeMethod, Method javaMethod) {

    }
}
