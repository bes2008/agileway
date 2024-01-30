package com.jn.agileway.audit.core.operation.repository;

import com.jn.agileway.audit.core.model.OperationDefinition;
import com.jn.langx.configuration.MultipleLevelConfigurationRepository;

public class DefaultOperationRepositoryParser implements OperationRepositoryParser {

    private String name = "DefaultOperationRepositoryParser";
    private MultipleLevelConfigurationRepository repository;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getRepositoryName() {
        return repository.getName();
    }

    public MultipleLevelConfigurationRepository getRepository() {
        return repository;
    }

    public void setRepository(MultipleLevelConfigurationRepository repository) {
        this.repository = repository;
    }

    @Override
    public OperationDefinition parse(String definitionId) {
        return (OperationDefinition) repository.getById(definitionId);
    }
}
