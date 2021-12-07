package com.jn.agileway.feign.supports.rpc.rest.param;

import com.jn.easyjson.core.JSON;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import feign.Param;
import org.slf4j.Logger;
/**
 * @since 2.6.0
 */
public class ToJsonExpander implements Param.Expander {
    private static final Logger logger = Loggers.getLogger(ToJsonExpander.class);
    private final JSON jsons = JSONBuilderProvider.simplest();

    @Override
    public String expand(Object value) {
        if (value == null) {
            return "";
        }
        try {
            return jsons.toJson(value);
        } catch (Throwable ex) {
            logger.error("error occur when convert a {} object to a json string", Reflects.getFQNClassName(value.getClass()));
        }
        return "";
    }
}
