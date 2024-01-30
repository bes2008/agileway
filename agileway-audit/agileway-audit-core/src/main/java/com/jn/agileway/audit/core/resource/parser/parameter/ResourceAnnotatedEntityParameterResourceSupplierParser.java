package com.jn.agileway.audit.core.resource.parser.parameter;

import com.jn.agileway.audit.core.annotation.Resource;
import com.jn.agileway.audit.core.annotation.ResourceMapping;
import com.jn.agileway.audit.core.resource.ResourceUtils;
import com.jn.agileway.audit.core.resource.parser.ResourceSupplierParser;
import com.jn.agileway.audit.core.resource.parser.clazz.CustomNamedEntityResourceSupplierParser;
import com.jn.agileway.audit.core.resource.parser.clazz.DefaultEntityClassResourceSupplierParser;
import com.jn.agileway.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.util.collection.StringMap;
import com.jn.langx.util.reflect.Parameter;
import com.jn.langx.util.reflect.Reflects;


/**
 * 如果在一个方法的某个参数，是一个Entity时，并且具有 @Resource 注解时，将解析成实体的资源
 *
 * @see Resource
 * @see CustomNamedEntityResourceSupplierParser
 */
public class ResourceAnnotatedEntityParameterResourceSupplierParser<T> implements ResourceSupplierParser<Parameter, EntityResourceSupplier<T>> {

    @Override
    public EntityResourceSupplier<T> parse(Parameter parameter) {
        if (!Reflects.hasAnnotation(parameter, Resource.class)) {
            return null;
        }
        Resource resource = Reflects.getAnnotation(parameter, Resource.class);
        ResourceMapping mapping = resource.value();
        if (ResourceUtils.isDefaultResourceMapping(mapping)) {
            return DefaultEntityClassResourceSupplierParser.DEFAULT_INSTANCE.parse(parameter.getType());
        } else {
            StringMap map = new StringMap();
            map.put(com.jn.agileway.audit.core.model.Resource.RESOURCE_ID, mapping.id());
            map.put(com.jn.agileway.audit.core.model.Resource.RESOURCE_NAME, mapping.name());
            map.put(com.jn.agileway.audit.core.model.Resource.RESOURCE_TYPE, mapping.type());
            CustomNamedEntityResourceSupplierParser parser = new CustomNamedEntityResourceSupplierParser(map);
            return parser.parse(parameter.getType());
        }
    }


}
