package com.jn.agileway.audit.entityloader.resttemplate;

import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.MapAccessor;

public class RestTemplateEntityLoaders {
    public static Class loadEntityClass( ResourceDefinition resourceDefinition) throws ClassNotFoundException{
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        String entityClassName = mapAccessor.getString("entityClass");
        Preconditions.checkNotNull(entityClassName);
        ClassLoader cl = DefaultParameterizedResponseClassProvider.class.getClassLoader();
        if (ClassLoaders.hasClass(entityClassName, cl)) {
            return ClassLoaders.loadClass(entityClassName, cl);
        }
        throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Can't find the entity class: {}", entityClassName));
    }
}
