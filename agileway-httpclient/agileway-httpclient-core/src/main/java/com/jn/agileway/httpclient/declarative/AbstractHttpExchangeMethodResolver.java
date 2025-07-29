package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.reflect.signature.TypeSignatures;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractHttpExchangeMethodResolver implements HttpExchangeMethodResolver {

    @Override
    public final HttpExchangeMethod resolve(Method javaMethod) {
        HttpExchangeMethod exchangeMethod = new HttpExchangeMethod();
        exchangeMethod.setJavaMethod(javaMethod);

        resolveInternal(exchangeMethod, javaMethod);

        resolveResponseType(exchangeMethod, javaMethod);
        exchangeMethod.checkValid();
        return exchangeMethod;
    }

    protected abstract void resolveInternal(HttpExchangeMethod exchangeMethod, Method javaMethod);

    /**
     * 解析期望的响应类型，解析出的类型，不能是 Promise, HttpResponse。
     *
     * @param exchangeMethod
     * @param javaMethod
     */
    protected void resolveResponseType(HttpExchangeMethod exchangeMethod, Method javaMethod) {
        if (javaMethod.getReturnType() == void.class) {
            exchangeMethod.setExpectedResponseType(null);
        }
        Type returnType = javaMethod.getGenericReturnType();

        if (returnType instanceof Class) {
            if (returnType != Promise.class && returnType != HttpResponse.class) {
                exchangeMethod.setExpectedResponseType((Class) returnType);
            }
            return;
        }
        if (returnType instanceof ParameterizedType) {
            ParameterizedType prt = ((ParameterizedType) returnType);
            Type rawType = prt.getRawType();
            if (rawType == Promise.class) {
                Type[] args = prt.getActualTypeArguments();
                if (args.length == 1) {
                    Type promiseArgType = args[0];
                    if (!(promiseArgType instanceof ParameterizedType)) {
                        if (promiseArgType != HttpResponse.class) {
                            throw new HttpExchangeMethodDeclaringException("Unsupported method return type :" + TypeSignatures.toMethodSignature(javaMethod));
                        } else {
                            return;
                        }
                    } else {
                        if (((ParameterizedType) promiseArgType).getRawType() != HttpResponse.class) {
                            throw new HttpExchangeMethodDeclaringException("Unsupported method return type :" + TypeSignatures.toMethodSignature(javaMethod));
                        } else {
                            returnType = promiseArgType;
                        }
                    }
                } else {
                    return;
                }
            }
        }

        if (returnType instanceof ParameterizedType) {
            ParameterizedType prt = ((ParameterizedType) returnType);
            Type rawType = prt.getRawType();
            if (rawType == HttpResponse.class) {
                Type[] args = prt.getActualTypeArguments();
                if (args.length == 1) {
                    Type httpResponsePayloadType = args[0];
                    exchangeMethod.setExpectedResponseType(httpResponsePayloadType);
                    return;
                }
            }
        }

        exchangeMethod.setExpectedResponseType(returnType);
    }
}
