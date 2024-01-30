package com.jn.agileway.audit.core.resource.parser.parameter;

import com.jn.agileway.audit.core.annotation.Resource;
import com.jn.agileway.audit.core.annotation.ResourceMapping;
import com.jn.agileway.audit.core.resource.parser.ResourceSupplierParser;
import com.jn.agileway.audit.core.resource.supplier.MapResourceSupplier;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Parameter;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.valuegetter.MapValueGetter;

import java.util.Map;

/**
 * 一个方法的参数是 Map，且有 @Resource 注解时，可以用它来处理
 *
 * @see Resource
 * @see com.jn.agileway.audit.core.resource.parser.parameter.CustomNamedMapParameterResourceSupplierParser
 */
public class ResourceAnnotatedMapParameterResourceSupplierParser implements ResourceSupplierParser<Parameter, MapResourceSupplier> {
    @Override
    public MapResourceSupplier parse(Parameter parameter) {
        if (!Reflects.isSubClassOrEquals(Map.class, parameter.getType())) {
            return null;
        }
        if (!Reflects.hasAnnotation(parameter, Resource.class)) {
            return null;
        }
        Resource resource = Reflects.getAnnotation(parameter, Resource.class);
        ResourceMapping mapping = resource.value();
        if (Emptys.isNotEmpty(mapping)) {
            MapResourceSupplier supplier = new MapResourceSupplier();
            if (Emptys.isNotEmpty(mapping.id())) {
                supplier.register(com.jn.agileway.audit.core.model.Resource.RESOURCE_ID, new MapValueGetter(mapping.id()));
            }
            supplier.register(com.jn.agileway.audit.core.model.Resource.RESOURCE_NAME, new MapValueGetter(mapping.name()));
            if (Emptys.isNotEmpty(mapping.type())) {
                supplier.register(com.jn.agileway.audit.core.model.Resource.RESOURCE_TYPE, new MapValueGetter(mapping.type()));
            }

            return supplier;
        }
        return null;
    }

}
