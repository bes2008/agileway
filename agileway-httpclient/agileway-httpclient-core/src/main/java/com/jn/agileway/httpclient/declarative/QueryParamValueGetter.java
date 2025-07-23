package com.jn.agileway.httpclient.declarative;

import com.jn.langx.util.valuegetter.ArrayValueGetter;

public class QueryParamValueGetter extends ArrayValueGetter<Object> {
    private String defaultValue;

    public QueryParamValueGetter(int index, String defaultValue) {
        super(index);
        this.defaultValue = defaultValue;
    }

    @Override
    public Object get(Object[] input) {
        Object result = super.get(input);
        return result == null ? defaultValue : result;
    }
}
