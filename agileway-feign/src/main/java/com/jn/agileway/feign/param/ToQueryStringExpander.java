package com.jn.agileway.feign.param;

import com.jn.easyjson.core.JSON;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.http.HttpQueryStrings;
import feign.Param;

import java.util.Map;

/**
 * @since 1.0.0
 */
public class ToQueryStringExpander implements Param.Expander {
    private static JSON jsons = JSONBuilderProvider.simplest();

    @Override
    public String expand(Object value) {
        if (value == null) {
            return "";
        }
        String jsonString = jsons.toJson(value);
        Map<String, Object> map = jsons.fromJson(jsonString, Map.class);
        String queryString = HttpQueryStrings.toQueryString(map, true, null, null);
        return queryString;
    }
}
