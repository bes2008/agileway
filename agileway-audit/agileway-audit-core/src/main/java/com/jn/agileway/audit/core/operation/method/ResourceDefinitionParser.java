package com.jn.agileway.audit.core.operation.method;

import com.jn.agileway.audit.core.annotation.Resource;
import com.jn.agileway.audit.core.annotation.ResourceMapping;
import com.jn.agileway.audit.core.annotation.ResourceProperty;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.langx.Parser;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

public class ResourceDefinitionParser implements Parser<Resource, ResourceDefinition> {
    public ResourceDefinition parse(@NonNull Resource resource) {
        final ResourceDefinition resourceDefinition = new ResourceDefinition();

        ResourceMapping resourceMapping = resource.value();
        resourceDefinition.setResourceId(resourceMapping.id());
        resourceDefinition.setResourceName(resourceMapping.name());
        resourceDefinition.setResourceType(resourceMapping.type());

        ResourceProperty[] properties = resource.properties();
        Collects.forEach(properties, new Consumer<ResourceProperty>() {
            @Override
            public void accept(ResourceProperty resourceProperty) {
                if (Emptys.isNoneEmpty(resourceProperty.name(), resourceProperty.value())) {
                    resourceDefinition.put(resourceProperty.name(), resourceProperty.value());
                }
            }
        });

        return resourceDefinition;
    }
}
