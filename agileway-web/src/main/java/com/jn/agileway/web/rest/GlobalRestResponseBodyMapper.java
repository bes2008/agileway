package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Mapper;
import com.jn.langx.util.function.Predicate2;

import java.util.Map;

public class GlobalRestResponseBodyMapper implements Mapper<RestRespBody, Map<String, Object>> {
    private GlobalRestResponseBodyHandlerConfiguration configuration;
    private Function<Map<String, Object>, Map<String, Object>> delegate = Functions.noopFunction();
    private Predicate2<String, Object> fieldCleaner = new Predicate2<String, Object>() {
        @Override
        public boolean test(String key, Object value) {
            if (GlobalRestResponseBodyMapper.this.configuration.isIgnoredField(key)) {
                return true;
            }
            return false;
        }
    };

    public GlobalRestResponseBodyMapper(GlobalRestResponseBodyHandlerConfiguration configuration) {
        setConfiguration(configuration);
    }

    public void setDelegate(Function<Map<String, Object>, Map<String, Object>> delegate) {
        this.delegate = delegate;
    }

    public void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setFieldCleaner(Predicate2<String, Object> fieldCleaner) {
        this.fieldCleaner = fieldCleaner;
    }

    @Override
    public Map<String, Object> apply(RestRespBody body) {
        Map<String, Object> m = body.toMap(fieldCleaner);
        if (delegate != null) {
            m = delegate.apply(m);
        }
        return m;
    }
}
