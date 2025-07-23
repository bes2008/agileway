package com.jn.agileway.httpclient.declarative;

import com.jn.langx.util.valuegetter.ArrayValueGetter;

import java.util.Map;

public interface UriVariablesResolver {
    Map<String, String> resolve(Map<String, ArrayValueGetter<Object>> uriVariables, Object[] methodArguments);
}
