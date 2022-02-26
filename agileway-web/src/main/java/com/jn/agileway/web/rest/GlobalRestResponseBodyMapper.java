package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Mapper;
import com.jn.langx.util.function.Predicate2;

import java.util.Map;

public class GlobalRestResponseBodyMapper implements Mapper<RestRespBody, Map<String, Object>> {
    private GlobalRestResponseBodyHandlerConfiguration configuration;
    private Function<Map<String, Object>, Map<String, Object>> fieldsMapper = Functions.noopFunction();
    private Predicate2<String, Object> fieldsCleaner = new Predicate2<String, Object>() {
        @Override
        public boolean test(String key, Object value) {
            if (GlobalRestResponseBodyMapper.this.configuration.isIgnoredField(key)) {
                return true;
            }
            return false;
        }
    };

    public GlobalRestResponseBodyMapper() {
    }

    public GlobalRestResponseBodyMapper(GlobalRestResponseBodyHandlerConfiguration configuration) {
        setConfiguration(configuration);
    }


    public void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setFieldsMapper(Function<Map<String, Object>, Map<String, Object>> fieldsMapper) {
        this.fieldsMapper = fieldsMapper;
    }

    public void setFieldsCleaner(Predicate2<String, Object> fieldsCleaner) {
        this.fieldsCleaner = fieldsCleaner;
    }

    @Override
    public Map<String, Object> apply(RestRespBody body) {
        Map<String, Object> m = body.toMap(fieldsCleaner);
        if (fieldsMapper != null) {
            m = fieldsMapper.apply(m);
        }
        return m;
    }
}
