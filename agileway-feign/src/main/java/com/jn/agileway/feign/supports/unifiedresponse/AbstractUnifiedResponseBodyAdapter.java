package com.jn.agileway.feign.supports.unifiedresponse;

import com.jn.langx.util.reflect.Reflects;
import feign.Response;

import java.lang.reflect.Type;

public abstract class AbstractUnifiedResponseBodyAdapter<REST_RESULT> implements UnifiedResponseBodyAdapter<REST_RESULT> {
    protected Class<?> expected = Object.class;

    public AbstractUnifiedResponseBodyAdapter(Class<REST_RESULT> type) {
        if (type != null) {
            this.expected = type;
        }
    }

    @Override
    public boolean accept(Object result) {
        if (result == null) {
            return true;
        }
        if (expected == Object.class) {
            return false;
        }
        if (Reflects.isSubClassOrEquals(expected, result.getClass())) {
            return false;
        }
        return true;
    }

    @Override
    public abstract REST_RESULT adapt(Response response, Type type, Object o);
}
