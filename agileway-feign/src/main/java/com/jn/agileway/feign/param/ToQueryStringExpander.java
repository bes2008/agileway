package com.jn.agileway.feign.param;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.net.http.HttpQueryStrings;
import feign.Param;

import java.util.Map;

/**
 * @since 1.0.0
 */
public class ToQueryStringExpander implements Param.Expander {

    @Override
    public String expand(Object value) {
        if (value == null) {
            return "";
        }
        String jsonString = JSONs.toJson(value);
        Map<String, Object> map = JSONs.parse(jsonString, Map.class);
        String queryString = HttpQueryStrings.toQueryString(map, true, null, null);
        return queryString;
    }
}
