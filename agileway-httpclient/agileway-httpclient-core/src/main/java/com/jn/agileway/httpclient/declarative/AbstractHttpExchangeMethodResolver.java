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

    protected void resolveResponseType(HttpExchangeMethod exchangeMethod, Method javaMethod) {
        Type returnType = javaMethod.getGenericReturnType();
        if (returnType == void.class) {
            exchangeMethod.setExpectedResponseType(null);
        }
        if (returnType instanceof Class) {
            if (returnType == Promise.class || returnType == HttpResponse.class) {
                return;
            } else {
                exchangeMethod.setExpectedResponseType((Class) returnType);
            }
        }
        if (returnType instanceof ParameterizedType) {
            ParameterizedType prt = ((ParameterizedType) returnType);
            Type ownerType = prt.getOwnerType();
            if (ownerType == Promise.class) {
                Type[] args = prt.getActualTypeArguments();
                if (args.length == 1) {
                    Type promiseArgType = args[0];
                    if (!(promiseArgType instanceof ParameterizedType)) {
                        return;
                    } else {
                        if (((ParameterizedType) promiseArgType).getOwnerType() != HttpResponse.class) {
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
            Type ownerType = prt.getOwnerType();
            if (ownerType == HttpResponse.class) {
                Type[] args = prt.getActualTypeArguments();
                if (args.length == 1) {
                    Type httpResponsePayloadType = args[0];
                    exchangeMethod.setExpectedResponseType(httpResponsePayloadType);
                }
            }
        }
        exchangeMethod.setExpectedResponseType(returnType);
    }
}
