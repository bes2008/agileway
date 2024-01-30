package com.jn.agileway.audit.core.operation;

import com.jn.agileway.audit.core.operation.method.OperationMethodAnnotationDefinitionParser;
import com.jn.agileway.audit.core.operation.repository.OperationRepositoryParser;
import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供多维度的 Operation Definition Parser 注册中心
 */
public class OperationDefinitionParserRegistry extends GenericRegistry<OperationDefinitionParser> {
    private Map<Class<? extends Annotation>, OperationMethodAnnotationDefinitionParser<?>> annotationParserMap = new LinkedHashMap<Class<? extends Annotation>, OperationMethodAnnotationDefinitionParser<?>>();
    private Map<String, OperationRepositoryParser> repositoryParserMap = new LinkedHashMap<String, OperationRepositoryParser>();

    public void registry(OperationMethodAnnotationDefinitionParser<? extends Annotation> parser) {
        Preconditions.checkNotNull(parser);
        annotationParserMap.put(parser.getAnnotation(), parser);
    }

    public void registry(OperationRepositoryParser parser) {
        Preconditions.checkNotNull(parser);
        repositoryParserMap.put(parser.getName(), parser);
    }

    public List<OperationMethodAnnotationDefinitionParser<? extends Annotation>> getAnnotationParsers() {
        return Collects.asList(annotationParserMap.values());
    }

    public OperationMethodAnnotationDefinitionParser<? extends Annotation> getAnnotationParser(Class<? extends Annotation> annotationClass) {
        return annotationParserMap.get(annotationClass);
    }

    public OperationRepositoryParser getRepositoryParser(String parserName) {
        return repositoryParserMap.get(parserName);
    }

    public List<OperationRepositoryParser> getRepositoryParsers() {
        return Collects.asList(repositoryParserMap.values());
    }


    @Override
    public void register(String name, OperationDefinitionParser operationDefinitionParser) {
        super.register(name, operationDefinitionParser);
        if (operationDefinitionParser instanceof OperationMethodAnnotationDefinitionParser) {
            OperationMethodAnnotationDefinitionParser parser = (OperationMethodAnnotationDefinitionParser) operationDefinitionParser;
            registry(parser);
        } else if (operationDefinitionParser instanceof OperationRepositoryParser) {
            OperationRepositoryParser parser = (OperationRepositoryParser) operationDefinitionParser;
            registry(parser);
        }
    }
}
