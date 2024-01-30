package com.jn.agileway.audit.core.operation.method;

import com.jn.agileway.audit.core.annotation.Operation;
import com.jn.agileway.audit.core.annotation.Resource;
import com.jn.agileway.audit.core.model.OperationDefinition;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.langx.util.Objs;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;

/**
 * 提供内置的 com.jn.agileway.audit.core.annotation.Operation 注解解析器
 * 如果要支持自定义的注解，可以自定义
 */
public class OperationAnnotationParser implements OperationMethodAnnotationDefinitionParser<Operation> {
    private String name;
    private ResourceDefinitionParser resourceDefinitionParser = new ResourceDefinitionParser();

    @Override
    public Class<Operation> getAnnotation() {
        return Operation.class;
    }

    @Override
    public String getName() {
        return Objs.useValueIfEmpty(name, new Supplier<String, String>() {
            @Override
            public String get(String s) {
                return Reflects.getFQNClassName(Operation.class);
            }
        });
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public OperationDefinition parse(Method method) {
        Operation operation = Reflects.getAnnotation(method, getAnnotation());


        if (operation != null) {
            OperationDefinition operationDefinition = new OperationDefinition();
            operationDefinition.setId(operation.code());
            operationDefinition.setCode(operation.code());
            operationDefinition.setName(operation.name());
            operationDefinition.setType(operation.type());
            operationDefinition.setDescription(operation.description());
            operationDefinition.setModule(operation.module());

            ResourceDefinition resourceDefinition = resourceDefinitionParser.parse(operation.resourceDefinition());

            Resource resource = Reflects.getAnnotation(method, Resource.class);
            ResourceDefinition resourceDefinition2 = null;
            if (resource != null) {
                resourceDefinition2 = resourceDefinitionParser.parse(resource);
            } else {
                resourceDefinition2 = new ResourceDefinition();
            }

            resourceDefinition.putAll(resourceDefinition2);
            operationDefinition.setResourceDefinition(resourceDefinition);
            return operationDefinition;
        }
        return null;
    }

}
