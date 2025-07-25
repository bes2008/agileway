package com.jn.agileway.httpclient.declarative;

import java.lang.reflect.Method;

public interface HttpExchangeMethodResolver {
    HttpExchangeMethod resolve(Method method);
}
