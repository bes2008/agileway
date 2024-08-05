package com.jn.agileway.audit.core.operation.repository;

import com.jn.agileway.audit.core.model.OperationDefinition;
import com.jn.agileway.audit.core.model.OperationImportance;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.configuration.file.InvalidConfigurationFileException;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 基于YAML文件方式配置 Operation Definition
 */
public class YamlOperationDefinitionLoader implements OperationDefinitionLoader {
    private static final Logger logger = Loggers.getLogger(YamlOperationDefinitionLoader.class);
    private String definitionFilePath;


    @Override
    public Map<String, OperationDefinition> loadAll() {
        return reloadAll(null);
    }

    @NonNull
    @Override
    public List<OperationDefinition> reload(Map<String, OperationImportance> importances) {
        return Collects.newArrayList(reloadAll(importances).values());
    }

    private Map<String, OperationDefinition> reloadAll(Map<String, OperationImportance> importances) {
        final Map<String, OperationImportance> importanceMap = new HashMap<String, OperationImportance>();
        if (importances != null) {
            importanceMap.putAll(importances);
        }
        Map<String, OperationDefinition> definitionMap = Collects.<String, OperationDefinition>emptyHashMap();

        Resource operationDefinitionResource = Resources.loadResource(definitionFilePath, Thread.currentThread().getContextClassLoader());

        InputStream inputStream = null;

        try {
            inputStream = operationDefinitionResource.getInputStream();
            Yaml yaml = new Yaml();
            Iterable<Object> iterable = yaml.loadAll(inputStream);
            Iterator iterator = iterable.iterator();

            // getAll
            Map<String, Object> segments = new HashMap<String, Object>();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Map) {
                    segments.putAll((Map) obj);
                }
            }
            // load importanceMap
            if (segments.containsKey("operationImportance")) {
                doLoadImportanceMap(importanceMap, (Map) segments.get("operationImportance"));
                if (importances != null) {
                    importances.putAll(importanceMap);
                }
            } else {
                logger.warn("Had not any operationImportance found in the location:{}", definitionFilePath);
                throw new InvalidConfigurationFileException(definitionFilePath);
            }

            // common props
            Map<String, Object> commonProps = null;
            if (segments.containsKey("operationCommonProps")) {
                commonProps = doLoadCommonProps((Map) segments.get("operationCommonProps"));
            }

            // load definitions
            if (segments.containsKey("operationDefinitions")) {
                doLoadDefinitions(definitionMap, importanceMap, commonProps, (Map<String, Map<String, Object>>) segments.get("operationDefinitions"));
            } else {
                logger.warn("Had not any operationImportance found in the location:{}", definitionFilePath);
                throw new InvalidConfigurationFileException(definitionFilePath);
            }
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        } finally {
            IOs.close(inputStream);
        }
        return definitionMap;
    }

    private void doLoadImportanceMap(@NonNull final Map<String, OperationImportance> importanceMap, @Nullable Map<String, Object> rawMap) {
        Collects.forEach(rawMap, new Consumer2<String, Object>() {
            @Override
            public void accept(String key, Object value) {
                OperationImportance importance = new OperationImportance();
                importance.setKey(key);
                importance.setValue(value.toString());
                importanceMap.put(key, importance);
            }
        });
    }

    private Map<String, Object> doLoadCommonProps(@Nullable Map<String, Object> rawMap) {
        if (rawMap == null) {
            return Collects.emptyHashMap();
        }
        return rawMap;
    }

    private void doLoadDefinitions(@NonNull final Map<String, OperationDefinition> definitionMap, final Map<String, OperationImportance> importanceMap, final Map<String, Object> commonProps, Map<String, Map<String, Object>> rawMap) {
        Collects.forEach(rawMap, new Consumer2<String, Map<String, Object>>() {
            @Override
            public void accept(String id, Map<String, Object> propertyPairMap) {
                OperationDefinition definition = new OperationDefinition();
                // id
                definition.setId(id);
                // code
                Object code = propertyPairMap.get("code");
                if (Objs.isEmpty(code)) {
                    logger.warn("Found an invalid operation definition: id:{}, code is empty", id);
                    return;
                }
                definition.setCode(code.toString());
                // name
                Object name = propertyPairMap.get("name");
                if (Objs.isEmpty(name)) {
                    logger.warn("Found an invalid operation definition: id:{}, name is empty", id);
                    return;
                }
                definition.setName(name.toString());
                // type
                Object type = propertyPairMap.get("type");
                if (type != null) {
                    definition.setType(type.toString());
                }
                // module
                Object module = propertyPairMap.get("module");
                if (module != null) {
                    definition.setModule(module.toString());
                }
                // description
                Object description = propertyPairMap.get("description");
                if (description != null) {
                    definition.setDescription(description.toString());
                } else {
                    definition.setDescription("");
                }
                // importance
                Object importanceKey = propertyPairMap.get("importance");
                if (Objs.isEmpty(importanceKey)) {
                    importanceKey = Collects.findFirst(importanceMap.keySet(), Functions.<String>nonNullPredicate());
                }
                if (Objs.isEmpty(importanceKey)) {
                    logger.warn("Found an invalid operation definition: id:{}, importance is empty", id);
                    return;
                }
                OperationImportance importance = importanceMap.get(importanceKey.toString());
                definition.setImportance(importance);

                // resource definition
                Object resourceDefinitionMap = propertyPairMap.get("resourceDefinition");
                if (Objs.isNotEmpty(resourceDefinitionMap) && resourceDefinitionMap instanceof Map) {
                    definition.setResourceDefinition(new ResourceDefinition((Map<String, Object>) resourceDefinitionMap));
                }

                Map<String, Object> props = Collects.newHashMap(commonProps);
                Object privateProps = propertyPairMap.get("props");
                if (Objs.isNotEmpty(privateProps) && privateProps instanceof Map) {
                    props.putAll((Map) privateProps);
                }
                definition.setProps(props);
                definitionMap.put(id, definition);
            }
        });
    }


    @Override
    public OperationDefinition load(String id) {
        return reloadAll(null).get(id);
    }

    public void setDefinitionFilePath(String definitionFilePath) {
        this.definitionFilePath = definitionFilePath;
    }
}
