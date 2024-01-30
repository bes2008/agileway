package com.jn.agileway.audit.core.resource.parser.parameter;

import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.agileway.audit.core.resource.parser.ResourceSupplierParser;
import com.jn.agileway.audit.core.resource.supplier.MapResourceSupplier;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Parameter;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.valuegetter.MapValueGetter;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @see ResourceDefinition#getResource()
 * @see ResourceAnnotatedMapParameterResourceSupplierParser
 */
public class CustomNamedMapParameterResourceSupplierParser implements ResourceSupplierParser<Parameter, MapResourceSupplier> {
    private static final Logger logger = Loggers.getLogger(CustomNamedMapParameterResourceSupplierParser.class);

    /**
     * key: resource property, e.g.
     *
     * @see com.jn.agileway.audit.core.model.Resource#RESOURCE_ID
     * @see com.jn.agileway.audit.core.model.Resource#RESOURCE_NAME
     * @see com.jn.agileway.audit.core.model.Resource#RESOURCE_TYPE
     * <p>
     * value: key alias
     */
    private Map<String, String> nameMapping = new HashMap<String, String>();

    public CustomNamedMapParameterResourceSupplierParser(Map<String, Object> map) {
        Collects.forEach(map, new Consumer2<String, Object>() {
            @Override
            public void accept(String key, Object value) {
                nameMapping.put(key, value.toString());
            }
        });
    }

    @Override
    public MapResourceSupplier parse(Parameter parameter) {
        if (!Reflects.isSubClassOrEquals(Map.class, parameter.getType())) {
            return null;
        }
        if (Emptys.isEmpty(nameMapping)) {
            return null;
        }
        final Map<String, MapValueGetter> getterMap = new HashMap<String, MapValueGetter>();
        Collects.forEach(nameMapping, new Consumer2<String, String>() {
            @Override
            public void accept(String resourceProperty, String key) {
                getterMap.put(resourceProperty, new MapValueGetter(key));
            }
        });
        if (Emptys.isEmpty(getterMap)) {
            return null;
        }
        MapResourceSupplier supplier = new MapResourceSupplier();
        supplier.register(getterMap);

        if(Emptys.isNotEmpty(supplier.getDeficientProperties())){
            logger.warn("the resource definition mapping has some deficient or invalid properties: {}", Strings.join(",", supplier.getDeficientProperties()));
        }
        return supplier;
    }
}
