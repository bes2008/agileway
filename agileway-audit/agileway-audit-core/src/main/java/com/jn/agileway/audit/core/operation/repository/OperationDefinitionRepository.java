package com.jn.agileway.audit.core.operation.repository;

import com.jn.agileway.audit.core.model.OperationDefinition;
import com.jn.agileway.audit.core.model.OperationImportance;
import com.jn.langx.configuration.AbstractConfigurationRepository;
import com.jn.langx.configuration.ConfigurationWriter;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于langx-java : configuration API 提供的一套 OperationDefinition Repository API
 */
public class OperationDefinitionRepository extends AbstractConfigurationRepository<OperationDefinition, OperationDefinitionLoader, ConfigurationWriter<OperationDefinition>> {
    /**
     * key: OperationImportance#getKey()
     */
    private Map<String, OperationImportance> importanceMap = new ConcurrentHashMap<String, OperationImportance>();
    /**
     * key: code
     * value: definition
     */
    private Map<String, OperationDefinition> definitionMap = new ConcurrentHashMap<String, OperationDefinition>();

    private static final OperationDefinitionRepository instance = new OperationDefinitionRepository();

    public OperationDefinitionRepository() {
    }

    public static OperationDefinitionRepository getInstance() {
        return instance;
    }

    public void registerImportance(OperationImportance importance) {
        importanceMap.put(importance.getName(), importance);
    }

    public void add(OperationDefinition operationDefinition) {
        Preconditions.checkNotNull(operationDefinition);
        super.add(operationDefinition);
        definitionMap.put(operationDefinition.getCode(), operationDefinition);
        OperationImportance importance = operationDefinition.getImportance();
        if (importance != null) {
            registerImportance(importance);
        }
    }


    public OperationImportance getImportance(String key) {
        return importanceMap.get(key);
    }

    public OperationDefinition getDefinitionByCode(String code) {
        return definitionMap.get(code);
    }

    @Override
    public void reload() {
        super.reload();
        Map<String, OperationImportance> importanceMap = Collects.emptyHashMap(true);
        List<OperationDefinition> definitions = loader.reload(importanceMap);
        Collects.forEach(definitions, new Consumer<OperationDefinition>() {
            @Override
            public void accept(OperationDefinition operationDefinition) {
                add(operationDefinition);
            }
        });
    }

    public Map<String, OperationImportance> getImportanceMap() {
        return new HashMap<String, OperationImportance>(importanceMap);
    }
}
