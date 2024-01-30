package com.jn.agileway.audit.swagger.operation;

import com.jn.agileway.audit.core.annotation.Resource;
import com.jn.agileway.audit.core.model.OperationDefinition;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.agileway.audit.core.operation.method.OperationMethodAnnotationDefinitionParser;
import com.jn.agileway.audit.core.operation.method.ResourceDefinitionParser;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.reflect.Reflects;
import io.swagger.annotations.ApiOperation;

import java.lang.reflect.Method;

public class SwaggerApiOperationAnnotationParser implements OperationMethodAnnotationDefinitionParser<ApiOperation> {
    private String name;
    private ResourceDefinitionParser resourceDefinitionParser = new ResourceDefinitionParser();

    @Override
    public Class<ApiOperation> getAnnotation() {
        return ApiOperation.class;
    }

    @Override
    public String getName() {
        return Objs.useValueIfEmpty(name, new Supplier<String, String>() {
            @Override
            public String get(String s) {
                return "Swagger-ApiOperation-Parser";
            }
        });
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public OperationDefinition parse(Method method) {
        ApiOperation operation = Reflects.getAnnotation(method, getAnnotation());

        if (operation != null) {
            OperationDefinition operationDefinition = new OperationDefinition();
            operationDefinition.setId(operation.nickname());
            String id = operation.nickname();
            if (Emptys.isEmpty(id)) {
                id = Reflects.getFQNClassName(method.getDeclaringClass()) + method.getName();
            }
            String code = id;
            String name = operation.value();
            operationDefinition.setId(id);
            operationDefinition.setCode(code);
            operationDefinition.setName(name);

            Resource resource = Reflects.getAnnotation(method, Resource.class);
            ResourceDefinition resourceDefinition = null;
            if (resource != null) {
                resourceDefinition = resourceDefinitionParser.parse(resource);
            } else {
                resourceDefinition = new ResourceDefinition();
            }

            operationDefinition.setResourceDefinition(resourceDefinition);
        }

        return null;
    }
}
