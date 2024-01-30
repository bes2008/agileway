package com.jn.agileway.audit.core.resource.parser.parameter;

import com.jn.agileway.audit.core.annotation.Resource;
import com.jn.agileway.audit.core.annotation.ResourceMapping;
import com.jn.agileway.audit.core.resource.ResourceUtils;
import com.jn.agileway.audit.core.resource.parser.ResourceSupplierParser;
import com.jn.agileway.audit.core.resource.parser.clazz.CustomNamedEntityResourceSupplierParser;
import com.jn.agileway.audit.core.resource.parser.clazz.DefaultEntityClassResourceSupplierParser;
import com.jn.agileway.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.StringMap;
import com.jn.langx.util.reflect.Parameter;
import com.jn.langx.util.reflect.Reflects;

/**
 * 该类使用的场景：
 * 1）方法的参数是个集合
 * 2）方法的参数 有 @Resource注解
 * 3）组件类型是个 Java Bean
 */
public class ResourceAnnotatedCollectionParameterResourcesSupplierParser<T> implements ResourceSupplierParser<Parameter, EntityResourceSupplier<T>> {

    private Class<T> componentType;

    public ResourceAnnotatedCollectionParameterResourcesSupplierParser(Class<T> componentType) {
        Preconditions.checkNotNull(componentType);
        this.componentType = componentType;
    }

    @Override
    public EntityResourceSupplier<T> parse(Parameter parameter) {
        if (!Reflects.hasAnnotation(parameter, Resource.class)) {
            return null;
        }
        Resource resource = Reflects.getAnnotation(parameter, Resource.class);
        ResourceMapping mapping = resource.value();
        EntityResourceSupplier<T> entityResourceSupplier = null;
        if (ResourceUtils.isDefaultResourceMapping(mapping)) {
            entityResourceSupplier = DefaultEntityClassResourceSupplierParser.DEFAULT_INSTANCE.parse(componentType);
        } else {
            StringMap map = new StringMap();
            map.put(com.jn.agileway.audit.core.model.Resource.RESOURCE_ID, mapping.id());
            map.put(com.jn.agileway.audit.core.model.Resource.RESOURCE_NAME, mapping.name());
            map.put(com.jn.agileway.audit.core.model.Resource.RESOURCE_TYPE, mapping.type());
            CustomNamedEntityResourceSupplierParser<T> parser = new CustomNamedEntityResourceSupplierParser(map);
            entityResourceSupplier = parser.parse(componentType);
        }
        return entityResourceSupplier;
    }


}