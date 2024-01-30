package com.jn.agileway.audit.core.resource.parser.parameter;

import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.agileway.audit.core.resource.parser.ResourceSupplierParser;
import com.jn.agileway.audit.core.resource.supplier.EnumerationValueGetter;
import com.jn.agileway.audit.core.resource.supplier.IterableResourceSupplier;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Parameter;
import com.jn.langx.util.reflect.type.Types;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 该方式只针对 参数为 字面量类型时
 *
 * @see ResourceDefinition#getResourceId()
 * @see ResourceDefinition#getResourceName()
 * @see ResourceDefinition#getResourceType()
 * @see ResourcePropertyAnnotatedResourceSupplierParser
 */
public class CustomResourcePropertyParameterResourceSupplierParser implements ResourceSupplierParser<Parameter[], IterableResourceSupplier> {
    private static final Logger logger = Loggers.getLogger(CustomResourcePropertyParameterResourceSupplierParser.class);
    /**
     * resource property to parameter name mapping
     */
    private Map<String, String> parameterResourceMapping = new HashMap<String, String>();

    public CustomResourcePropertyParameterResourceSupplierParser(Map<String, Object> mapping) {
        Collects.forEach(mapping, new Consumer2<String, Object>() {
            @Override
            public void accept(String key, Object value) {
                if (value != null) {
                    parameterResourceMapping.put(key, value.toString());
                }
            }
        });
    }

    @Override
    public IterableResourceSupplier parse(final Parameter[] parameters) {
        final Map<String, EnumerationValueGetter> getterMap = new HashMap<String, EnumerationValueGetter>();
        Pipeline.of(parameters).forEach(
                new Predicate2<Integer, Parameter>() {
                    @Override
                    public boolean test(Integer key, Parameter parameter) {
                        Class type = parameter.getType();
                        return Types.isLiteralType(type);
                    }
                },
                new Consumer2<Integer, Parameter>() {
                    @Override
                    public void accept(final Integer index, Parameter parameter) {
                        final String parameterName = parameter.getName();
                        Collects.forEach(parameterResourceMapping, new Consumer2<String, String>() {
                            @Override
                            public void accept(String resourceProperty, String parameterName0) {
                                if (parameterName.equals(parameterName0)) {
                                    getterMap.put(resourceProperty, new EnumerationValueGetter(index));
                                }
                            }
                        });
                    }
                });
        if (Emptys.isEmpty(getterMap)) {
            return null;
        }
        IterableResourceSupplier supplier = new IterableResourceSupplier();
        supplier.register(getterMap);

        if(Emptys.isNotEmpty(supplier.getDeficientProperties())){
            logger.warn("the resource definition mapping has some deficient or invalid properties: {}", Strings.join(",", supplier.getDeficientProperties()));
        }
        return supplier;
    }
}
