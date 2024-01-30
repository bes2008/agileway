package com.jn.agileway.audit.entityloader.resttemplate;

import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.type.Types;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Type;

public class DefaultParameterizedResponseClassProvider implements ParameterizedResponseClassProvider {
    private static final Logger logger = Loggers.getLogger(DefaultParameterizedResponseClassProvider.class);

    private Class unifiedResponseClass = null;

    @Override
    public ParameterizedTypeReference get(String url, HttpMethod httpMethod, ResourceDefinition resourceDefinition) {
        Class entityClass = null;

        try {
            entityClass = RestTemplateEntityLoaders.loadEntityClass(resourceDefinition);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            throw Throwables.wrapAsRuntimeException(ex);
        }

        Type type = entityClass;
        if (unifiedResponseClass != null) {
            type = Types.getParameterizedType(unifiedResponseClass, entityClass);
        }
        return ParameterizedTypeReference.forType(type);

    }

    public Class getUnifiedResponseClass() {
        return unifiedResponseClass;
    }

    public void setUnifiedResponseClass(Class unifiedResponseClass) {
        this.unifiedResponseClass = unifiedResponseClass;
    }
}
